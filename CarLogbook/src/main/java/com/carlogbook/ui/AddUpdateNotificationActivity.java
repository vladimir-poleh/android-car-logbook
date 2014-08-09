/*
    CarLogbook.
    Copyright (C) 2014  Eugene Nadein

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.carlogbook.ui;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;
import com.carlogbook.db.CommonUtils;
import com.carlogbook.db.DBUtils;
import com.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;
import java.util.Date;

public class AddUpdateNotificationActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {
	private int mode;
	private long id;
	protected Date date;
	private TextView dateView;
	private EditText nameView;

	private EditText odometerView;
	private Spinner typeSpinner;

	private TypeState state = new OdometerTypeState();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_notification);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
		}


		dateView = (TextView) findViewById(R.id.date);
		odometerView = (EditText) findViewById(R.id.odometer);
		nameView = (EditText) findViewById(R.id.name);
	    typeSpinner = (Spinner) findViewById(R.id.typeSpinner);

		typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				if (pos == 0) {
					state = new OdometerTypeState();
				} else {
					state = new DateTypeState();
				}
				state.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		populateEntity();

	}

	private void populateEntity() {
		if (mode == PARAM_EDIT) {
			Cursor c = getContentResolver()
					.query(ProviderDescriptor.Notify.CONTENT_URI, null, SELECTION_ID_FILTER,
							new String[]{String.valueOf(id)}, null);

			if (c == null) {
				return;
			}
			boolean hasItem = c.moveToFirst();

			if (!hasItem) {
				return;
			}

			int trigerIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE);
			int nameIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME);
			int typeIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.TYPE);

			long trigerValue = c.getLong(trigerIdx);
			String name = c.getString(nameIdx);
			int type = c.getInt(typeIdx);

			nameView.setText(name);

			if (type == ProviderDescriptor.Notify.Type.ODOMETER) {
				odometerView.setText(String.valueOf(trigerValue));
				typeSpinner.setSelection(0);
			} else {
				date = new Date(trigerValue);
				dateView.setText(CommonUtils.formatDate(date));
				state = new DateTypeState();
				typeSpinner.setSelection(1);
			}


			c.close();
		} else {
			date = new Date(System.currentTimeMillis());
			dateView.setText(CommonUtils.formatDate(date));
			long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
			odometerView.setText(String.valueOf(odometerValue));
			typeSpinner.setSelection(0);
		}
	}

	@Override
	public String getSubTitle() {
		return (mode == PARAM_EDIT) ? getString(R.string.notify_title_edit) : getString(R.string.notify_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_delete).setVisible(mode == AddUpdateCarActivity.PARAM_EDIT);
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_save: {
				if (state.validate()) {
					state.save();
					NavUtils.navigateUpFromSameTask(this);
				}

				break;
			}

			case R.id.action_delete: {
				getMediator().showConfirmDeleteView();
				break;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment(date, this);

		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (ConfirmDeleteDialog.REQUEST_CODE_DELETE == requestCode) {
			getContentResolver().delete(ProviderDescriptor.Notify.CONTENT_URI, "_id = ?", new String[] {String.valueOf(id)});
			NavUtils.navigateUpFromSameTask(this);
		}
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		date = c.getTime();
		dateView.setText(CommonUtils.formatDate(date));
	}

	interface TypeState {
		public boolean validate();

		public void save();

		public void show();
	}

	abstract class TypeStateImpl implements TypeState {
		public void hideAllErrors() {
			showError(R.id.errorOdometer, false);
			showError(R.id.errorDate, false);
		}

		public ContentValues createBaseContentValues() {
			ContentValues cv = new ContentValues();
			cv.put(ProviderDescriptor.Notify.Cols.NAME, nameView.getText().toString());
			long carId = DBUtils.getActiveCarId(getContentResolver());
			cv.put(ProviderDescriptor.Notify.Cols.CAR_ID, carId);
			return cv;
		}

		public void createOrUpdate(ContentValues cv) {
			if (mode == PARAM_EDIT) {
				getContentResolver().update(ProviderDescriptor.Notify.CONTENT_URI, cv, "_id = ?" , new String[] {String.valueOf(id)});
			} else {
				getContentResolver().insert(ProviderDescriptor.Notify.CONTENT_URI, cv);
			}
		}

	}

	class OdometerTypeState extends TypeStateImpl {

		@Override
		public boolean validate() {
			boolean result = true;

			if (!validateTextView(R.id.errorName, nameView)) {
				result = false;
			}

			long odometerMaxValue = DBUtils.getMaxOdometerValue(getContentResolver());
			long currentOdometerValue = Long.valueOf(odometerView.getText().toString());

			if (currentOdometerValue <= odometerMaxValue) {
				showError(R.id.errorOdometer, true);
				result = false;
			} else {
				showError(R.id.errorOdometer, false);
			}

			return result;
		}

		@Override
		public void save() {

			ContentValues cv = createBaseContentValues();

			cv.put(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE, Long.valueOf(odometerView.getText().toString()));
			cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.ODOMETER);

			createOrUpdate(cv);
		}

		@Override
		public void show() {
			hideAllErrors();
			findViewById(R.id.dateGroup).setVisibility(View.GONE);
			findViewById(R.id.odometerGroup).setVisibility(View.VISIBLE);
		}
	}

	private class DateTypeState extends TypeStateImpl {

		@Override
		public boolean validate() {
			boolean result = true;

			if (!validateTextView(R.id.errorName, nameView)) {
				result = false;
			}

			long selectedDate = date.getTime();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			c.set(Calendar.HOUR, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DAY_OF_MONTH, 1);
			long currentDate = c.getTimeInMillis();


			if (selectedDate < currentDate) {
				showError(R.id.errorDate, true);
				result = false;
			} else {
				showError(R.id.errorDate, false);
			}


			return result;
		}

		@Override
		public void save() {
			ContentValues cv = createBaseContentValues();

			cv.put(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE, date.getTime());
			cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.DATE);

			createOrUpdate(cv);
		}

		@Override
		public void show() {
			hideAllErrors();
			findViewById(R.id.odometerGroup).setVisibility(View.GONE);
			findViewById(R.id.dateGroup).setVisibility(View.VISIBLE);
		}
	}
}

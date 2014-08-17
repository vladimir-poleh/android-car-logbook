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
package com.enadein.carlogbook.ui;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;
import java.util.Date;

public class AddUpdateNotificationActivity extends SaveUpdateBaseActivity implements DatePickerDialog.OnDateSetListener {
	protected Date date = new Date();
	private TextView dateView;
	private EditText nameView;

	private EditText odometerView;
	private Spinner typeSpinner;

	private TypeState state = new OdometerTypeState();

	@Override
	public String getSubTitle() {
		return (mode == PARAM_EDIT) ? getString(R.string.notify_title_edit) : getString(R.string.notify_title);
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment(date, this);

		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}

	@Override
	protected boolean validateEntity() {
		return state.validate();
	}

	@Override
	protected void createEntity() {
		state.save();
	}

	@Override
	protected void updateEntity() {
		state.save();
	}

	@Override
	protected void preDelete() {
		getMediator().showConfirmDeleteView();
	}

	@Override
	protected void deleteEntity() {
		getContentResolver().delete(ProviderDescriptor.Notify.CONTENT_URI, ID_PARAM, new String[] {String.valueOf(id)});
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	protected void populateEditEntity() {

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
	}

	@Override
	protected void populateCreateEntity() {
		long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
		odometerView.setText(String.valueOf(odometerValue));
		typeSpinner.setSelection(0);
	}

	@Override
	protected void postCreate() {
		dateView = (TextView) findViewById(R.id.date);
		odometerView = (EditText) findViewById(R.id.odometer);
		nameView = (EditText) findViewById(R.id.name);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);

		dateView.setText(CommonUtils.formatDate(date));

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
	}

	@Override
	protected int getContentLayout() {
		return R.layout.add_notification;
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
				getContentResolver().update(ProviderDescriptor.Notify.CONTENT_URI, cv, ID_PARAM , new String[] {String.valueOf(id)});
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

			if (!validateTextView(R.id.errorOdometer, odometerView)) {
				result = false;
			} else {
				long odometerMaxValue = DBUtils.getMaxOdometerValue(getContentResolver());
				long currentOdometerValue = Long.valueOf(odometerView.getText().toString());

				if (currentOdometerValue <= odometerMaxValue) {
					showError(R.id.errorOdometer, true);
					result = false;
				} else {
					showError(R.id.errorOdometer, false);
				}
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

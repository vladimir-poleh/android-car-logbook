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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddUpdateNotificationActivity extends SaveUpdateBaseActivity implements DatePickerDialog.OnDateSetListener {
	public static final String NAME = "NAME";
	protected Date date = new Date();
	private TextView dateView;
	private EditText nameView;

	private EditText odometerView;
	private Spinner typeSpinner;

	private TypeState state = new OdometerTypeState();

	private AQuery a;

	private boolean fromNotification;
	private String populatedName;

	private long odometerValueOld;
	private long dateValueOld;


//	public static HashMap<Integer, Long> timeStamps = new HashMap<Integer, Long>();
//
//	static  {
//		timeStamps.put(1, DBUtils.DAY * 5);
//		timeStamps.put(2, DBUtils.MONTH);
//
//		timeStamps.put(3, DBUtils.MONTH * 2);
//		timeStamps.put(4, DBUtils.MONTH * 3);
//		timeStamps.put(5, DBUtils.MONTH * 4);
//		timeStamps.put(6, DBUtils.MONTH * 6);
//		timeStamps.put(7, DBUtils.YEAR);
//		timeStamps.put(8, DBUtils.YEAR * 2);
//	}

	@Override
	public String getSubTitle() {
		return (mode == PARAM_EDIT) ? getString(R.string.notify_title_edit) : getString(R.string.notify_title);
	}

	@Override
	protected void populateExtraParams(Bundle params) {
		fromNotification = params.getBoolean(BaseActivity.NOTIFY_EXTRA);
		populatedName = params.getString(NAME);
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setListener(date, this);

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
		CommonUtils.validateDateNotifications(AddUpdateNotificationActivity.this);
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
		int triger2Idx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE2);
		int nameIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME);
		int typeIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.TYPE);
		int repeatIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.REPEAT);
		int repeat2Idx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.REPEAT_2);
		int commentsIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.COMMENTS);
		int carIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.CAR_ID);

		long trigerValue = c.getLong(trigerIdx);
		long carId = c.getLong(carIdx);
		long trigerValue2 = c.getLong(triger2Idx);
		String name = c.getString(nameIdx);
		int type = c.getInt(typeIdx);
		long repeat = c.getLong(repeatIdx);
		long repeat2 = c.getLong(repeat2Idx);

		String carName = DBUtils.getActiveCarName(getContentResolver(), carId);



		nameView.setText(name);
		a.id(R.id.comment).text(c.getString(commentsIdx));
		a.id(R.id.carView).visible().text(carName);
		odometerValueOld =
				(type == ProviderDescriptor.Notify.Type.DATE_ODOMETER)
						? trigerValue2 : trigerValue;

		dateValueOld = trigerValue;

		if (type == ProviderDescriptor.Notify.Type.ODOMETER) {
			odometerView.setText(String.valueOf(trigerValue));
			typeSpinner.setSelection(0);

			if (repeat > 0 ) {
				a.id(R.id.repeatOdometer).text(String.valueOf(repeat));
			}
		} else if (type == ProviderDescriptor.Notify.Type.DATE){
			date = new Date(trigerValue);
			dateView.setText(CommonUtils.formatDate(date));
			state = new DateTypeState();
			typeSpinner.setSelection(1);
			a.id(R.id.dateRepeat).getSpinner().setSelection((int) repeat);
		} else {
			//todo refactor it
			date = new Date(trigerValue);
			dateView.setText(CommonUtils.formatDate(date));
			state = new DateOdometerState();
			typeSpinner.setSelection(2);

			a.id(R.id.dateRepeat).getSpinner().setSelection((int) repeat);

			odometerView.setText(String.valueOf(trigerValue2));
			if (repeat2 > 0 ) {
				a.id(R.id.repeatOdometer).text(String.valueOf(repeat2));
			}
		}


		c.close();

//		if (fromNotification) {
//			nameView.setEnabled(false);
//			dateView.setEnabled(false);
//			odometerView.setEnabled(false);
//			typeSpinner.setEnabled(false);
//			a.id(R.id.repeatOdometer).enabled(false);
//			a.id(R.id.dateRepeat).enabled(false);
//		}
	}

	@Override
	protected void populateCreateEntity() {
		long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
		odometerView.setText(String.valueOf(odometerValue));
		typeSpinner.setSelection(0);

		getMediator().showCarSelection(new CarChangedListener() {
			@Override
			public void onCarChanged(long id) {

			}
		});
	}

	@Override
	public int getCarSelectorViewId() {
		return R.id.carsAdd;
	}

	@Override
	protected void postCreate() {
		dateView = (TextView) findViewById(R.id.date);
		odometerView = (EditText) findViewById(R.id.odometer);
		nameView = (EditText) findViewById(R.id.name);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);

		a = new AQuery(this);

		if (!CommonUtils.isEmpty(populatedName)) {
			nameView.setText(populatedName);
		}

		dateView.setText(CommonUtils.formatDate(date));

		typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				if (pos == 0) {
					state = new OdometerTypeState();
				} else if (pos == 1) {
					state = new DateTypeState();
				} else {
					state = new DateOdometerState();
				}
				state.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

//	@Override
//	protected void onNewIntent(Intent intent) {
//		super.onNewIntent(intent);
//
//		Bundle params = intent.getExtras();
//		if (params != null) {
//			mode = params.getInt(BaseActivity.MODE_KEY);
//			id = params.getLong(BaseActivity.ENTITY_ID);
//			populateExtraParams(params);
//		}
//	}

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

		protected String getRepeatKey() {
			return null;
		}

		protected String getTrigerKey() {
			return null;
		}


		abstract public void populateMainValues(ContentValues cv);

		public ContentValues createBaseContentValues() {
			ContentValues cv = new ContentValues();
			cv.put(ProviderDescriptor.Notify.Cols.NAME, nameView.getText().toString());
			long carId = DBUtils.getActiveCarId(getContentResolver());
			cv.put(ProviderDescriptor.Notify.Cols.CAR_ID, carId);

			String comments = ((EditText)findViewById(R.id.comment)).getText().toString();

			if (!CommonUtils.isEmpty(comments)) {
				cv.put(ProviderDescriptor.Notify.Cols.COMMENTS, comments);
			}


			return cv;
		}

		public void createOrUpdate(ContentValues cv) {
			if (mode == PARAM_EDIT) {
				getContentResolver().update(ProviderDescriptor.Notify.CONTENT_URI, cv, ID_PARAM , new String[] {String.valueOf(id)});
			} else {
				cv.put(ProviderDescriptor.Notify.Cols.CREATE_DATE, new Date().getTime());
				getContentResolver().insert(ProviderDescriptor.Notify.CONTENT_URI, cv);
			}

			CommonUtils.validateDateNotifications(AddUpdateNotificationActivity.this);
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
			}
//			else {
//				long odometerMaxValue = DBUtils.getMaxOdometerValue(getContentResolver());
//				long currentOdometerValue = Long.valueOf(odometerView.getText().toString());
//
//				if (currentOdometerValue <= odometerMaxValue) {
//					showError(R.id.errorOdometer, true);
//					result = false;
//				} else {
//					showError(R.id.errorOdometer, false);
//				}
//			}
			return result;
		}

		@Override
		public void save() {

			ContentValues cv = createBaseContentValues();
			populateMainValues(cv);

			createOrUpdate(cv);
		}

		@Override
		public void show() {
			hideAllErrors();
			findViewById(R.id.div).setVisibility(View.GONE);
			findViewById(R.id.dateGroup).setVisibility(View.GONE);
			findViewById(R.id.odometerGroup).setVisibility(View.VISIBLE);
		}

		protected String getRepeatKey() {
			return ProviderDescriptor.Notify.Cols.REPEAT;
		}

		protected String getTrigerKey() {
			return ProviderDescriptor.Notify.Cols.TRIGGER_VALUE;
		}


		@Override
		public void populateMainValues(ContentValues cv) {
			long currentOdometerValue = Long.valueOf(odometerView.getText().toString());
			if (fromNotification) {
				if (currentOdometerValue == odometerValueOld) {
					String repeatValue = a.id(R.id.repeatOdometer).getText().toString();
					if (CommonUtils.isNotEmpty(repeatValue)) {
						currentOdometerValue += Long.valueOf(repeatValue);
					}
				}
			}

			cv.put(getTrigerKey(), currentOdometerValue);
			cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.ODOMETER);
			String repeatValue = a.id(R.id.repeatOdometer).getText().toString();

			if (CommonUtils.isNotEmpty(repeatValue)) {
				cv.put(getRepeatKey(), Long.valueOf(repeatValue));
			}
		}
	}

	private class DateTypeState extends TypeStateImpl {

		@Override
		public boolean validate() {
			boolean result = true;

			if (!validateTextView(R.id.errorName, nameView)) {
				result = false;
			}

//			long selectedDate = date.getTime();
//			Calendar c = Calendar.getInstance();
//			c.setTimeInMillis(System.currentTimeMillis());
//			c.set(Calendar.HOUR, 0);
//			c.set(Calendar.MINUTE, 0);
//			c.set(Calendar.SECOND, 0);
//			c.add(Calendar.DAY_OF_MONTH, 1);
//			long currentDate = c.getTimeInMillis();


//			if (selectedDate < currentDate) {
//				showError(R.id.errorDate, true);
//				result = false;
//			} else {
//				showError(R.id.errorDate, false);
//			}


			return result;
		}

		@Override
		public void save() {
			ContentValues cv = createBaseContentValues();
			populateMainValues(cv);

			createOrUpdate(cv);
		}

		@Override
		public void show() {
			hideAllErrors();
			findViewById(R.id.div).setVisibility(View.GONE);
			findViewById(R.id.odometerGroup).setVisibility(View.GONE);
			findViewById(R.id.dateGroup).setVisibility(View.VISIBLE);
		}

		@Override
		public void populateMainValues(ContentValues cv) {
			long currentDate = date.getTime();
			int repeatPosititon = a.id(R.id.dateRepeat).getSpinner().getSelectedItemPosition();

			if (fromNotification) {
				Calendar calCurrent = Calendar.getInstance();
				calCurrent.setTime(date);
				CommonUtils.trunkDay(calCurrent);

				Calendar calOld = Calendar.getInstance();
				calOld.setTimeInMillis(dateValueOld);
				CommonUtils.trunkDay(calOld);

				if (calCurrent.getTimeInMillis() == calOld.getTimeInMillis() && repeatPosititon > 0) {
//					currentDate = calCurrent.getTimeInMillis() + timeStamps.get(repeatPosititon);
					switch (repeatPosititon) {
						case 1: {
							calCurrent.add(Calendar.DAY_OF_YEAR, 1);
							break;
						}
						case 2: {
							calCurrent.add(Calendar.DAY_OF_YEAR, 7);
							break;
						}
						case 3: {
							calCurrent.add(Calendar.MONTH, 1);
							break;
						}
						case 4: {
							calCurrent.add(Calendar.MONTH, 2);
							break;
						}
						case 5: {
							calCurrent.add(Calendar.MONTH, 3);
							break;
						}
						case 6: {
							calCurrent.add(Calendar.MONTH, 4);
							break;
						}
						case 7: {
							calCurrent.add(Calendar.MONTH, 6);
							break;
						}
						case 8: {
							calCurrent.add(Calendar.YEAR, 1);
							break;
						}
						case 9: {
							calCurrent.add(Calendar.YEAR, 2);
							break;
						}
					}

					currentDate = calCurrent.getTimeInMillis();
				}
			}

			cv.put(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE, currentDate);
			cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.DATE);
			cv.put(ProviderDescriptor.Notify.Cols.REPEAT, repeatPosititon);
		}
	}

	private class DateOdometerState extends TypeStateImpl {
		TypeStateImpl odometerState = new OdometerTypeState() {
			@Override
			protected String getRepeatKey() {
				return ProviderDescriptor.Notify.Cols.REPEAT_2;
			}

			@Override
			protected String getTrigerKey() {
				return ProviderDescriptor.Notify.Cols.TRIGGER_VALUE2;
			}

			@Override
			public void save() {
				//
			}

			@Override
			public void populateMainValues(ContentValues cv) {
				super.populateMainValues(cv);
				cv.remove(ProviderDescriptor.Notify.Cols.TYPE);
			}
		};
		TypeStateImpl dateTypeState = new DateTypeState(){
			@Override
			public void save() {
				//
			}

			@Override
			public void populateMainValues(ContentValues cv) {
				super.populateMainValues(cv);
				cv.remove(ProviderDescriptor.Notify.Cols.TYPE);
			}
		};
		@Override
		public boolean validate() {
			return odometerState.validate() && dateTypeState.validate();
		}

		@Override
		public void save() {
			ContentValues cv = createBaseContentValues();
			populateMainValues(cv);
			createOrUpdate(cv);
		}

		@Override
		public void show() {
			hideAllErrors();
			findViewById(R.id.dateGroup).setVisibility(View.VISIBLE);
			findViewById(R.id.odometerGroup).setVisibility(View.VISIBLE);
			findViewById(R.id.div).setVisibility(View.VISIBLE);
		}

		@Override
		public void populateMainValues(ContentValues cv) {
			odometerState.populateMainValues(cv);
			dateTypeState.populateMainValues(cv);
			cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.DATE_ODOMETER);
		}
	}


}

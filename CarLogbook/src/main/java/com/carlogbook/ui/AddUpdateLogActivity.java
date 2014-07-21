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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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

public class AddUpdateLogActivity extends BaseLogAcivity implements DatePickerDialog.OnDateSetListener {

	public static final int PARAM_EDIT = 1;
	private long id;
	private int mode;

	private EditText odomenterView;
	private TextView dateView;
	private EditText priceView;
	private EditText nameView;

	private Spinner typeSpinner;

	private Date date;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_log);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
		}

		odomenterView = (EditText) findViewById(R.id.odometer);
		priceView = (EditText) findViewById(R.id.price);
		nameView = (EditText) findViewById(R.id.name);

		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		populateEntity();
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment(date, this);

		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}


	private void populateEntity() {
		date = new Date(System.currentTimeMillis()); // todo
		dateView = (TextView) findViewById(R.id.date);
		dateView.setText(CommonUtils.formatDate(date));

		long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
		odomenterView.setText(String.valueOf(odometerValue));
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.log_title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_save: {
				if (validate()) {
					save();
					NavUtils.navigateUpFromSameTask(this);
				}
				break;
			}

			case R.id.action_delete: {
				break;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_delete).setVisible(mode == AddUpdateDataValue.PARAM_EDIT);
		return super.onPrepareOptionsMenu(menu);
	}

	//todo Refactor it
	private void save() {
		ContentResolver cr = getContentResolver();
		ContentValues cv = new ContentValues();


		long carId = DBUtils.getActiveCarId(cr);
		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.OTHER);

		cv.put(ProviderDescriptor.Log.Cols.DATE, date.getTime());
		cv.put(ProviderDescriptor.Log.Cols.ODOMETER,
				Integer.valueOf(odomenterView.getText().toString()));
		cv.put(ProviderDescriptor.Log.Cols.TYPE_ID, typeSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Log.Cols.NAME, nameView.getText().toString());
		cv.put(ProviderDescriptor.Log.Cols.PRICE, CommonUtils.getPriceValue(priceView));

		EditText commentEditText = (EditText) findViewById(R.id.comment);
		String comment = commentEditText.getText().toString().trim();

		if (!"".equals(comment)) {
			cv.put(ProviderDescriptor.Log.Cols.CMMMENT, comment);
		}


		getContentResolver().insert(ProviderDescriptor.Log.CONTENT_URI, cv);
	}

	private boolean validate() {
		boolean result = true;

		if (!validateOdometer(R.id.errorOdometer, odomenterView, date)) {
			result = false;
		}

		if (!validateView(R.id.errorPrice, priceView)) {
			result = false;
		}

		if (!validateTextView(R.id.errorName, nameView)) {
			result = false;
		}


		return result;
	}

	//TODO allow date > today?
	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		date = c.getTime();
		dateView.setText(CommonUtils.formatDate(date));
	}
}

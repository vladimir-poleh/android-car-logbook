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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddUpdateCarActivity extends SaveUpdateBaseActivity implements DatePickerDialog.OnDateSetListener {

	private long selectedCarId;
	private CheckBox selectCarView;
	private Spinner distSpinner;
	private Spinner fuelSpinner;
	private Spinner consumSpinner;

	private int consumValue = 0;
	private EditText curency;
	private TextView dateView;
	protected Date date = new Date();

	private AQuery a;


	@Override
	protected int getContentLayout() {
		return R.layout.add_car_fragment;
	}

	@Override
	protected void postPopulate() {
		super.postPopulate();
		setDateText(CommonUtils.formatDate(date));
	}

	@Override
	protected void populateEditEntity() {
		Cursor c = getContentResolver()
				.query(ProviderDescriptor.Car.CONTENT_URI, null, SELECTION_ID_FILTER,
						new String[]{String.valueOf(id)}, null);

		if (c == null) {
			return;
		}

		boolean hasItem = c.moveToFirst();
		EditText carNameView = (EditText) findViewById(R.id.carName);
		if (hasItem) {
			int idxName = c.getColumnIndex(ProviderDescriptor.Car.Cols.NAME);
			int activeFlagIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols.ACTIVE_FLAG);
			String carName = c.getString(idxName);
			boolean active = c.getInt(activeFlagIdx) > 0;

			int unitFuel = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_FUEL));
			int unitDistance = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_DISTANCE));
			consumValue = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION));
			String currencyValue = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CURRENCY));
			curency.setText(currencyValue);
			fuelSpinner.setSelection(unitFuel);
			distSpinner.setSelection(unitDistance);
//			String uuid = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UUID));
//			Log.e("XXX", ""  +uuid);

			if (active) {
				selectCarView.setVisibility(View.GONE);
			}


			carNameView.setText(carName);

			a.id(R.id.make).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.MAKE)));
			a.id(R.id.model).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.MODEL)));
			a.id(R.id.manuf).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.MANUF)));
			a.id(R.id.car_cost).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.CAR_COST)));
//            a.id(R.id.purchase).text(c.getString(
//                    c.getColumnIndex(ProviderDescriptor.Car.Cols.PURCHASE)));

			long datePurshase = c.getLong(c.getColumnIndex(
					ProviderDescriptor.Car.Cols.PURCHASE));

			if (datePurshase > 0) {
				date = new Date(datePurshase);
			} else {
				date = new Date();
			}

			a.id(R.id.open_mil).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.OPEN_MIL)));
			a.id(R.id.id_no).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.ID_NO)));
			a.id(R.id.reg_num).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.REG_NUM)));
			a.id(R.id.fuel_type).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.FUEL_TYPE)));
			a.id(R.id.tyre).text(c.getString(
					c.getColumnIndex(ProviderDescriptor.Car.Cols.TYRE)));

		}
		c.close();
	}

	@Override
	protected void populateCreateEntity() {
		UnitFacade unitFacade = getMediator().getUnitFacadeDefault();
		consumValue = unitFacade.getConsumptionValue();

		distSpinner.setSelection(unitFacade.getDistanceValue());
		fuelSpinner.setSelection(unitFacade.getFuelValue());

		curency.setText(unitFacade.getCurrency());
//		setupConsumptionSpinner(distSpinner.getSelectedItemPosition(),
//				fuelSpinner.getSelectedItemPosition());
	}


	@Override
	protected void postCreate() {
		a = new AQuery(this);

		curency = (EditText) findViewById(R.id.currency);

		selectCarView = (CheckBox) findViewById(R.id.selectCar);

		selectedCarId = DBUtils.getActiveCarId(getContentResolver());
		if (selectedCarId == -1) {
			selectCarView.setVisibility(View.GONE);
		}

		distSpinner = (Spinner) findViewById(R.id.distanceSpinner);
		distSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				setupConsumptionSpinner(distSpinner.getSelectedItemPosition(),
						fuelSpinner.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		fuelSpinner = (Spinner) findViewById(R.id.fuelSpinner);
		fuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				setupConsumptionSpinner(distSpinner.getSelectedItemPosition(),
						fuelSpinner.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		consumSpinner = (Spinner) findViewById(R.id.consumptionSpinner);
	}

	public void setupConsumptionSpinner(int dist, int fuel) {
		int consumId = CommonUtils.getConsumptionArrayId(dist, fuel);
		String[] list = getResources()
				.getStringArray(consumId);
		ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		consumSpinner.setAdapter(spinnerAdapter);


		consumSpinner.setSelection(consumValue);
	}

	private String getCarName() {
		EditText carName = (EditText) findViewById(R.id.carName);
		return carName.getText().toString();
	}

	@Override
	protected boolean validateEntity() {
		return validateData(getCarName());
	}

	@Override
	protected void createEntity() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, getCarName());

		cv.put(ProviderDescriptor.Car.Cols.UNIT_DISTANCE, distSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Car.Cols.UNIT_FUEL, fuelSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION, consumSpinner.getSelectedItemPosition());

		cv.put(ProviderDescriptor.Car.Cols.MAKE, a.id(R.id.make).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.MODEL, a.id(R.id.model).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.MANUF, a.id(R.id.manuf).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.CAR_COST, a.id(R.id.car_cost).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.PURCHASE, date.getTime());
		cv.put(ProviderDescriptor.Car.Cols.OPEN_MIL, a.id(R.id.open_mil).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.ID_NO, a.id(R.id.id_no).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.REG_NUM, a.id(R.id.reg_num).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.FUEL_TYPE, a.id(R.id.fuel_type).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.TYRE, a.id(R.id.tyre).getText().toString());


		String currencyVal = curency.getText() != null ? curency.getText().toString() : "";
		if (currencyVal.trim().length() > 0) {
			cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY, currencyVal);
		} else {
			currencyVal = getMediator().getUnitFacadeDefault().getCurrency();
			cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY, currencyVal);
		}

		if (selectCarView.isChecked()) {
			DBUtils.resetCurrentActiveFlag(getContentResolver());
		}

		if (selectedCarId == -1 || selectCarView.isChecked()) {
			cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		}

		String uuid = UUID.randomUUID().toString();
		cv.put(ProviderDescriptor.Car.Cols.UUID, uuid);

		getContentResolver().insert(ProviderDescriptor.Car.CONTENT_URI, cv);

		getMediator().getUnitFacade().reload(DBUtils.getActiveCarId(getContentResolver()));
	}

	@Override
	protected void updateEntity() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, getCarName());
		cv.put(ProviderDescriptor.Car.Cols.UNIT_DISTANCE, distSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Car.Cols.UNIT_FUEL, fuelSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION, consumSpinner.getSelectedItemPosition());

		cv.put(ProviderDescriptor.Car.Cols.MAKE, a.id(R.id.make).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.MODEL, a.id(R.id.model).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.MANUF, a.id(R.id.manuf).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.CAR_COST, a.id(R.id.car_cost).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.PURCHASE, date.getTime());
		cv.put(ProviderDescriptor.Car.Cols.OPEN_MIL, a.id(R.id.open_mil).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.ID_NO, a.id(R.id.id_no).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.REG_NUM, a.id(R.id.reg_num).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.FUEL_TYPE, a.id(R.id.fuel_type).getText().toString());
		cv.put(ProviderDescriptor.Car.Cols.TYRE, a.id(R.id.tyre).getText().toString());


		String currencyVal = curency.getText() != null ? curency.getText().toString() : "";
		if (currencyVal.trim().length() > 0) {
			cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY, currencyVal);
		}

		if (selectCarView.isChecked()) {
			DBUtils.resetCurrentActiveFlag(getContentResolver());
			cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		}


		getContentResolver().update(ProviderDescriptor.Car.CONTENT_URI, cv, ID_PARAM,
				new String[]{String.valueOf(id)});

		getMediator().getUnitFacade().reload(id);
	}


	@Override
	protected void preDelete() {
		getMediator().showConfirmDeleteView();
	}

	@Override
	protected void deleteEntity() {
		long carCount = DBUtils.getCount(getContentResolver(), ProviderDescriptor.Car.CONTENT_URI);

		if (id == selectedCarId && carCount > 1) {
			getMediator().showAlert(getString(R.string.deleteActive));
		} else {
			DBUtils.deleteCascadeCar(getContentResolver(), id);
			upToParent();
		}

		getMediator().getUnitFacade().reload(DBUtils.getActiveCarId(getContentResolver()));
	}

	@Override
	public String getSubTitle() {
		return (mode == AddUpdateCarActivity.PARAM_EDIT)
				? getString(R.string.edit_car_title)
				: getString(R.string.add_car_title);
	}

	private boolean validateData(String carNameValue) {
		boolean result = true;
		if (carNameValue == null || carNameValue.trim().length() == 0) {
			findViewById(R.id.carNameError).setVisibility(View.VISIBLE);
			result = false;
		}

		return result;
	}


	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		date = c.getTime();
		setDateText(CommonUtils.formatDate(date));
	}

	void setDateText(String text) {
		a.id(R.id.date).text(text);
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setListener(date, this);
		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}

}

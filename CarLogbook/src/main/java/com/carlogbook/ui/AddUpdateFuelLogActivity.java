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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
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

public class AddUpdateFuelLogActivity extends BaseLogAcivity implements
		LoaderManager.LoaderCallbacks<Cursor>, DatePickerDialog.OnDateSetListener {
	public static final int LOADER_TYPE = 102;
	public static final int LOADER_STATION = 103;

	public static final int PARAM_EDIT = 1;
	private long id;
	private int mode;

	private SimpleCursorAdapter fuelAdapter;
	private SimpleCursorAdapter stationAdapter;

	private EditText odomenterView;
	private TextView dateView;
	private EditText fuelValueView;
	private EditText priceView;
	private EditText priceTotalView;
	private Spinner fuelTypeSpinner;
	private Spinner stationSpinner;

	private Date date;

	private PriceValueState priceValueState = new PriceValueState();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_fuel_log);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
		}

		odomenterView = (EditText) findViewById(R.id.odometer);

		priceView = (EditText) findViewById(R.id.price);
		priceTotalView = (EditText) findViewById(R.id.priceTotal);

		fuelValueView = (EditText) findViewById(R.id.fuel_volume);

		fuelTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		stationSpinner = (Spinner) findViewById(R.id.stationSpinner);
		String[] adapterCols = new String[]{ProviderDescriptor.DataValue.Cols.NAME};
		int[] adapterRowViews = new int[]{android.R.id.text1};
		fuelAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
				null, adapterCols, adapterRowViews, 0);
		fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fuelTypeSpinner.setAdapter(fuelAdapter);

		stationAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
				null, adapterCols, adapterRowViews, 0);
		stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationSpinner.setAdapter(stationAdapter);

		getSupportLoaderManager().initLoader(LOADER_STATION, null, this);
		getSupportLoaderManager().initLoader(LOADER_TYPE, null, this);

		populateEntity();

		fuelValueView.setOnFocusChangeListener(new StateOnFocusChangeListener(new FuelState()));
		priceView.setOnFocusChangeListener(new StateOnFocusChangeListener(new PriceState()));
		priceTotalView.setOnFocusChangeListener(new StateOnFocusChangeListener(new TotalState()));

		fuelValueView.addTextChangedListener(new TextWatcherWrapper() {
			@Override
			public void update() {
				priceValueState.updateFuel();
			}
		});

		priceView.addTextChangedListener(new TextWatcherWrapper() {
			@Override
			public void update() {
				priceValueState.updatePrice();
			}
		});
		priceTotalView.addTextChangedListener(new TextWatcherWrapper() {
			@Override
			public void update() {
				priceValueState.updateTotal();
			}
		});


	}


	private abstract class TextWatcherWrapper implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
			update();
		}

		@Override
		public void afterTextChanged(Editable editable) {

		}

		abstract public void update();
	}

	private void populateEntity() {
		date = new Date(System.currentTimeMillis()); // todo
		dateView = (TextView) findViewById(R.id.date);
		dateView.setText(CommonUtils.formatDate(date));

		long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
		odomenterView.setText(String.valueOf(odometerValue));

		priceView.setText(String.valueOf(DBUtils.getLastPriceValue(getContentResolver())));
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.log_fuel_title);
	}

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment(date, this);

		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		CursorLoader cursorLoader = null;

		switch (id) {
			case LOADER_STATION: {
				cursorLoader = new CursorLoader(this,
						ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
						"TYPE = ?", new String[]{String.valueOf(ProviderDescriptor.DataValue.Type.STATION)}, null);
				break;
			}
			case LOADER_TYPE: {
				cursorLoader = new CursorLoader(this,
						ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
						"TYPE = ?", new String[]{String.valueOf(ProviderDescriptor.DataValue.Type.FUEL)}, null);

				break;
			}
		}

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		//TODO need to refactor 'set default value'
		if (loader.getId() == LOADER_TYPE) {
			fuelAdapter.swapCursor(data);
			long defaultId = DBUtils.getDefaultId(getContentResolver(),
					ProviderDescriptor.DataValue.Type.FUEL);

			Spinner fuelTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
			fuelTypeSpinner.setSelection(getPositionFromAdapterById(fuelAdapter, defaultId));

		} else {
			stationAdapter.swapCursor(data);
			long defaultId = DBUtils.getDefaultId(getContentResolver(),
					ProviderDescriptor.DataValue.Type.STATION);

			Spinner stationSpinner = (Spinner) findViewById(R.id.stationSpinner);
			stationSpinner.setSelection(getPositionFromAdapterById(stationAdapter, defaultId));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == LOADER_TYPE) {
			fuelAdapter.swapCursor(null);
		} else {
			stationAdapter.swapCursor(null);
		}
	}

	private int getPositionFromAdapterById(SimpleCursorAdapter adapter, long id) {

		int position = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			long currentId = adapter.getItemId(i);
			if (currentId == id) {
				position = i;
				break;
			}
		}
		return position;
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

	//TODO refactor it in future releases
	private void save() {
		ContentResolver cr = getContentResolver();
		ContentValues cv = new ContentValues();

		cv.put(ProviderDescriptor.Log.Cols.ODOMETER,
				Integer.valueOf(odomenterView.getText().toString()));

		long carId = DBUtils.getActiveCarId(cr);
		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);

		cv.put(ProviderDescriptor.Log.Cols.FUEL_VOLUME, CommonUtils.getPriceValue(fuelValueView));
		cv.put(ProviderDescriptor.Log.Cols.PRICE, CommonUtils.getPriceValue(priceView));

		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.FUEL);

		cv.put(ProviderDescriptor.Log.Cols.DATE, date.getTime());

		EditText commentEditText = (EditText) findViewById(R.id.comment);
		String comment = commentEditText.getText().toString().trim();

		if (!"".equals(comment)) {
			cv.put(ProviderDescriptor.Log.Cols.CMMMENT, comment);
		}

		long fuelTypeId = fuelAdapter.getItemId(fuelTypeSpinner.getSelectedItemPosition());
		DBUtils.setDafaultId(cr, ProviderDescriptor.DataValue.Type.FUEL, fuelTypeId);
		cv.put(ProviderDescriptor.Log.Cols.FUEL_TYPE_ID, fuelTypeId);

		long fuelStationId = stationAdapter.getItemId(stationSpinner.getSelectedItemPosition());
		DBUtils.setDafaultId(cr, ProviderDescriptor.DataValue.Type.STATION, fuelStationId);
		cv.put(ProviderDescriptor.Log.Cols.FUEL_STATION_ID, fuelStationId);

		getContentResolver().insert(ProviderDescriptor.Log.CONTENT_URI, cv);
	}

	private boolean validate() {
		boolean result = true;

		if (!validateView(R.id.errorFuel, fuelValueView)) {
			result = false;
		}

		if (!validateView(R.id.errorPrice, priceView)) {
			result = false;
		}

		if (!validateOdometer(R.id.errorOdometer, odomenterView, date)) {
			result = false;
		}

		return result;
	}


	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		date = c.getTime();
		dateView.setText(CommonUtils.formatDate(date));
	}

	private class PriceValueState {
		public void updatePrice() {	};

		public void updateTotal() {	};

		public void updateFuel() {}	;

		protected void calculateTotal() {
			double fuelValue = CommonUtils.getPriceValue(fuelValueView);
			double priceValue = CommonUtils.getPriceValue(priceView);
			double priceTotalValue = fuelValue * priceValue;

			priceTotalView.setText(CommonUtils.formatPrice(priceTotalValue));
		}
	}

	private class PriceState extends PriceValueState {

		@Override
		public void updatePrice() {
			calculateTotal();
		}
	}

	private class FuelState extends PriceValueState {

		@Override
		public void updateFuel() {
			calculateTotal();
		}
	}

	private class TotalState extends PriceValueState {

		@Override
		public void updateTotal() {
			double priceValue = CommonUtils.getPriceValue(priceView);
			double priceTotalValue = CommonUtils.getPriceValue(priceTotalView);

			double fuelValue = CommonUtils.div(priceTotalValue, priceValue);
			fuelValueView.setText(CommonUtils.formatPrice(fuelValue));
		}
	}

	private class StateOnFocusChangeListener implements View.OnFocusChangeListener {
		private PriceValueState state;

	 	private StateOnFocusChangeListener(PriceValueState state) {
			this.state = state;
		}

		@Override
		public void onFocusChange(View view, boolean focused) {
			if (focused) {
				priceValueState = state;
			}
		}
	}

}

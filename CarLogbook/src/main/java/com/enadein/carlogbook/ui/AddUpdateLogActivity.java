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

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Date;

public class AddUpdateLogActivity extends BaseLogAcivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
	private EditText odometerView;
	private TextView dateView;
	private EditText priceView;
	private EditText nameView;
	private EditText commentView;

	private Spinner typeSpinner;
	private Spinner ohterTypeSpinner;
    private UnitFacade unitFacade;

    private SimpleCursorAdapter otherTypesApater;

    private long otherTypeId = -1;

    @Override
	protected boolean validateEntity() {
		boolean result = true;

		if (!validateOdometer(R.id.errorOdometer, odometerView)) {
			result = false;
		}

//		if (!validateView(R.id.errorPrice, priceView)) {
//			result = false;
//		}

		if (!validateTextView(R.id.errorName, nameView)) {
			result = false;
		}

		return result;
	}

	@Override
	protected void createEntity() {
		getContentResolver().insert(ProviderDescriptor.Log.CONTENT_URI, getContentValues());
		CommonUtils.validateOdometerNotifications(this,
				Integer.valueOf(odometerView.getText().toString()));
        updateActiveCar();
	}

	@Override
	protected void updateEntity() {
		getContentResolver().update(ProviderDescriptor.Log.CONTENT_URI, getContentValues(), ID_PARAM, new String[]{String.valueOf(id)});
	}

	@Override
	protected void populateEditEntity() {
		Cursor logCursor = getContentResolver().query(ProviderDescriptor.Log.CONTENT_URI, null, "_id = ?", new String[]{String.valueOf(id)}, null);
		if (logCursor != null && logCursor.moveToFirst()) {
			int odometerIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.ODOMETER);
			int priceIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.PRICE);
			int dateIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.DATE);
			int nameIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.NAME);
			int typeIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.TYPE_ID);
			int commentIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.CMMMENT);
			int otherTypeIdx = logCursor.getColumnIndex(ProviderDescriptor.Log.Cols.OTHER_TYPE_ID);
			commentView.setText(logCursor.getString(commentIdx));

			odometerView.setText(String.valueOf(logCursor.getLong(odometerIdx)));
			priceView.setText(CommonUtils.formatPriceNew(logCursor.getDouble(priceIdx), unitFacade));
			date = new Date(logCursor.getLong(dateIdx));
			priceView.setText(CommonUtils.formatPriceNew(logCursor.getDouble(priceIdx), unitFacade));
			nameView.setText(logCursor.getString(nameIdx));
            int typePos = logCursor.getInt(typeIdx);
			typeSpinner.setSelection(typePos);

            otherTypeId = logCursor.getLong(otherTypeIdx);

//            if (typePos != 0) {
//                findViewById(R.id.otherGroup).setVisibility(View.GONE);
//            }

		}
	}

	@Override
	protected void populateCreateEntity() {
        showCarSelection();
		date = new Date(System.currentTimeMillis());

        populateValuesByCar();
	}

	@Override
	protected void postCreate() {
        unitFacade = getMediator().getUnitFacade();

		odometerView = (EditText) findViewById(R.id.odometer);
		priceView = (EditText) findViewById(R.id.price);
		nameView = (EditText) findViewById(R.id.name);
		commentView = (EditText) findViewById(R.id.comment);

		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		ohterTypeSpinner = (Spinner) findViewById(R.id.ohterTypeSpinner);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                findViewById(R.id.otherGroup).setVisibility(pos == 0 ? View.VISIBLE: View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        final Cursor c = getContentResolver().query(ProviderDescriptor.DataValue.CONTENT_URI, null, null, null, null);
        String[] from = new String[] {ProviderDescriptor.DataValue.Cols.NAME};
        int[] to = new int[] {android.R.id.text1};

        otherTypesApater = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
                null, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        otherTypesApater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ohterTypeSpinner.setAdapter(otherTypesApater);


        updateLabels();
	}


	@Override
	protected int getContentLayout() {
		return R.layout.add_log;
	}

	@Override
	public String getSubTitle() {
		return (mode == PARAM_EDIT) ? getString(R.string.log_title_edit) : getString(R.string.log_title);
	}

	private ContentValues getContentValues() {
//		ContentResolver cr = getContentResolver();
		ContentValues cv = new ContentValues();


//		long carId = DBUtils.getActiveCarId(cr);
		long carId = getCarId();

		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.OTHER);

		cv.put(ProviderDescriptor.Log.Cols.DATE, date.getTime());
		cv.put(ProviderDescriptor.Log.Cols.ODOMETER,
				Integer.valueOf(odometerView.getText().toString()));
		cv.put(ProviderDescriptor.Log.Cols.TYPE_ID, typeSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Log.Cols.NAME, nameView.getText().toString());
		cv.put(ProviderDescriptor.Log.Cols.PRICE, CommonUtils.getPriceValue(priceView));

        long otherTypeId = otherTypesApater.getItemId(ohterTypeSpinner.getSelectedItemPosition());
        cv.put(ProviderDescriptor.Log.Cols.OTHER_TYPE_ID, otherTypeId);

		EditText commentEditText = (EditText) findViewById(R.id.comment);
		String comment = commentEditText.getText().toString().trim();

		setComments(cv, comment);

		return cv;
	}



	@Override
	void setDateText(String text) {
		dateView.setText(text);
	}

	@Override
	protected void postPopulate() {
		dateView = (TextView) findViewById(R.id.date);
		dateView.setText(CommonUtils.formatDate(date));

		odometerView.setSelection(odometerView.getText().length());
		odometerView.requestFocus();

        getSupportLoaderManager().initLoader(0, null, this);
	}


    ///CAR SELECT
    @Override
    public void onCarChanged(long carId) {
        setCarId(carId);
        updateCarName(carId);
        populateValuesByCar();
    }

    public void populateValuesByCar() {
        long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver(), getCarId());
        odometerView.setText(String.valueOf(odometerValue));
        updateLabels();
    }

    public void updateLabels() {
        UnitFacade labelFacade = new UnitFacade(this);
        labelFacade.reload(getCarId(), true);

        TextView priceLabel = (TextView) findViewById(R.id.label_price);
        priceLabel.setText(getString(R.string.log_price));
        labelFacade.appendCurrency(priceLabel, true);

        TextView odometerLabel = (TextView) findViewById(R.id.label_odometer);
        odometerLabel.setText(getString(R.string.log_fuel_odometer));
        labelFacade.appendDistUnit(odometerLabel, true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                ProviderDescriptor.DataValue.CONTENT_URI, null,
                "TYPE = ?", new String[]{String.valueOf(ProviderDescriptor.DataValue.Type.OTHERS)}, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        otherTypesApater.swapCursor(cursor);


        for(int pos = otherTypesApater.getCount(); pos >= 0; pos--) {
            if (otherTypesApater.getItemId(pos) == otherTypeId) {
                ohterTypeSpinner.setSelection(pos);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        otherTypesApater.swapCursor(null);
    }
}

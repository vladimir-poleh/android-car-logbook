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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Date;

public class AddUpdateLogActivity extends BaseLogAcivity {
	private EditText odometerView;
	private TextView dateView;
	private EditText priceView;
	private EditText nameView;
	private EditText commentView;

	private Spinner typeSpinner;

	@Override
	protected boolean validateEntity() {
		boolean result = true;

		if (!validateOdometer(R.id.errorOdometer, odometerView)) {
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

	@Override
	protected void createEntity() {
		getContentResolver().insert(ProviderDescriptor.Log.CONTENT_URI, getContentValues());
		CommonUtils.validateOdometerNotifications(this,
				Integer.valueOf(odometerView.getText().toString()));
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
			commentView.setText(logCursor.getString(commentIdx));

			odometerView.setText(String.valueOf(logCursor.getLong(odometerIdx)));
			priceView.setText(CommonUtils.formatPrice(logCursor.getDouble(priceIdx)));
			date = new Date(logCursor.getLong(dateIdx));
			priceView.setText(CommonUtils.formatPrice(logCursor.getDouble(priceIdx)));
			nameView.setText(logCursor.getString(nameIdx));
			typeSpinner.setSelection(logCursor.getInt(typeIdx));
		}
	}

	@Override
	protected void populateCreateEntity() {
		date = new Date(System.currentTimeMillis());
		long odometerValue = DBUtils.getMaxOdometerValue(getContentResolver());
		odometerView.setText(String.valueOf(odometerValue));
	}

	@Override
	protected void postCreate() {
		odometerView = (EditText) findViewById(R.id.odometer);
		priceView = (EditText) findViewById(R.id.price);
		nameView = (EditText) findViewById(R.id.name);
		commentView = (EditText) findViewById(R.id.comment);

		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
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
		ContentResolver cr = getContentResolver();
		ContentValues cv = new ContentValues();


		long carId = DBUtils.getActiveCarId(cr);
		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.OTHER);

		cv.put(ProviderDescriptor.Log.Cols.DATE, date.getTime());
		cv.put(ProviderDescriptor.Log.Cols.ODOMETER,
				Integer.valueOf(odometerView.getText().toString()));
		cv.put(ProviderDescriptor.Log.Cols.TYPE_ID, typeSpinner.getSelectedItemPosition());
		cv.put(ProviderDescriptor.Log.Cols.NAME, nameView.getText().toString());
		cv.put(ProviderDescriptor.Log.Cols.PRICE, CommonUtils.getPriceValue(priceView));

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

	}
}

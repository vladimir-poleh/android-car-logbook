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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.EditText;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class AddUpdateDataValue extends SaveUpdateBaseActivity {
	private int type;
	private EditText name;

	@Override
	protected boolean validateEntity() {
		return validateData(name.getText().toString());
	}

	private boolean validateData(String value) {
		boolean result = true;
		if (value == null || value.trim().length() == 0) {
			findViewById(R.id.errorName).setVisibility(View.VISIBLE);
			result = false;
		}

		return result;
	}

	@Override
	protected void createEntity() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.NAME, name.getText().toString());
		cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
		getContentResolver().insert(ProviderDescriptor.DataValue.CONTENT_URI, cv);
	}

	@Override
	protected void updateEntity() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.NAME, name.getText().toString());
		getContentResolver().update(ProviderDescriptor.DataValue.CONTENT_URI,  cv, ID_PARAM, new String[] {String.valueOf(id)});
	}

	@Override
	protected void preDelete() {
		getMediator().showConfirmDeleteView();
	}

	@Override
	protected void deleteEntity() {
		boolean used = false;

		if (type == ProviderDescriptor.DataValue.Type.FUEL) {
			used = DBUtils.isFuelTypeUsed(getContentResolver(), id);
		} else if (type == ProviderDescriptor.DataValue.Type.STATION) {
			used = DBUtils.isStationUsed(getContentResolver(), id);
		}

		long count = DBUtils.getCount(getContentResolver(),
				ProviderDescriptor.DataValue.CONTENT_URI,
				ProviderDescriptor.DataValue.Cols.TYPE + " = ?" ,
				new String[] {String.valueOf(type)});

		if (used) {
			getMediator().showAlert(getString(R.string.value_used_error));
		} else if (count == 1) {
			getMediator().showAlert(getString(R.string.error_last_delete));
		} else {
			getContentResolver().delete(ProviderDescriptor.DataValue.CONTENT_URI, "_id = ?", new String[]{String.valueOf(id)});
			NavUtils.navigateUpFromSameTask(this);
		}
	}

	@Override
	protected void populateEditEntity() {
		String nameValue = DBUtils.getDataValueNameById(getContentResolver(), id);
		name.setText(nameValue);
	}

	@Override
	protected void populateCreateEntity() {

	}

	@Override
	protected void populateExtraParams(Bundle params) {
		type = params.getInt(BaseActivity.TYPE_KEY);
	}

	@Override
	protected void postCreate() {
		name = (EditText) findViewById(R.id.name);
	}

	@Override
	protected int getContentLayout() {
		return R.layout.add_data_value;
	}

	@Override
	public String getSubTitle() {
		if (type == ProviderDescriptor.DataValue.Type.FUEL) {
			return (mode == PARAM_EDIT) ? getString(R.string.sett_stations_edit_fuel):
					getString(R.string.sett_stations_add_fuel);
		} else {
			return (mode == PARAM_EDIT) ? getString(R.string.sett_stations_edit_gas):
					getString(R.string.sett_stations_add_gas);
		}
	}

}

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

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;
import com.carlogbook.db.DBUtils;
import com.carlogbook.db.ProviderDescriptor;

public class AddUpdateDataValue extends BaseActivity {
	public static final int PARAM_EDIT = 1;
	private long id;
	private int type;
	private int mode;
	private EditText name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_data_value);
		name = (EditText) findViewById(R.id.name);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
			type = params.getInt(BaseActivity.TYPE_KEY);
			if (mode == PARAM_EDIT) {
				String nameValue = DBUtils.getDataValueNameById(getContentResolver(), id);
				name.setText(nameValue);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_delete).setVisible(mode == AddUpdateDataValue.PARAM_EDIT);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {

			case R.id.action_save: {
				String nameVal = name.getText().toString();
				if (validateData(nameVal)) {
					save(nameVal);
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

	private void save(String nameVal) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.NAME, nameVal);
		if (mode == PARAM_EDIT) {
			getContentResolver().update(ProviderDescriptor.DataValue.CONTENT_URI,  cv, "_id = ?" , new String[] {String.valueOf(id)});
		} else {
			cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
			getContentResolver().insert(ProviderDescriptor.DataValue.CONTENT_URI, cv);
		}
		NavUtils.navigateUpFromSameTask(this);
	}

	private boolean validateData(String value) {
		boolean result = true;
		if (value == null || value.trim().length() == 0) {
			findViewById(R.id.errorName).setVisibility(View.VISIBLE);
			result = false;
		}

		return result;
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (ConfirmDeleteDialog.REQUEST_CODE_DELETE == requestCode) {
			getContentResolver().delete(ProviderDescriptor.DataValue.CONTENT_URI, "_id = ?", new String[] {String.valueOf(id)});
			NavUtils.navigateUpFromSameTask(this);
		}
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

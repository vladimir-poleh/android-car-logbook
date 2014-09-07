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
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.UUID;

public class AddUpdateCarActivity extends SaveUpdateBaseActivity {

	private long selectedCarId;
	private CheckBox selectCarView;

	@Override
	protected int getContentLayout() {
		return R.layout.add_car_fragment;
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

//			String uuid = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UUID));
//			Log.e("XXX", ""  +uuid);

			if (active) {
				selectCarView.setVisibility(View.GONE);
			}

			carNameView.setText(carName);
		}
		c.close();
	}

	@Override
	protected void populateCreateEntity() {
	}

	@Override
	protected void postCreate() {
		selectCarView = (CheckBox) findViewById(R.id.selectCar);

		selectedCarId = DBUtils.getActiveCarId(getContentResolver());
		if (selectedCarId == -1) {
			selectCarView.setVisibility(View.GONE);
		}
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

		if (selectCarView.isChecked()) {
			resetCurrentActiveFlag();
		}

		if (selectedCarId == -1 || selectCarView.isChecked()) {
			cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		}

		String uuid = UUID.randomUUID().toString();
		cv.put(ProviderDescriptor.Car.Cols.UUID, uuid);

		getContentResolver().insert(ProviderDescriptor.Car.CONTENT_URI, cv);
	}

	@Override
	protected void updateEntity() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, getCarName());

		if (selectCarView.isChecked()) {
			resetCurrentActiveFlag();
			cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		}
		getContentResolver().update(ProviderDescriptor.Car.CONTENT_URI, cv, ID_PARAM,
				new String[]{String.valueOf(id)});
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

	private void resetCurrentActiveFlag() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 0);

		getContentResolver().update(ProviderDescriptor.Car.CONTENT_URI, cv, "active_flag = 1",
				null);
	}

}

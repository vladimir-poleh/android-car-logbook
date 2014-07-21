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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;
import com.carlogbook.core.Logger;
import com.carlogbook.db.DBUtils;
import com.carlogbook.db.ProviderDescriptor;

public class AddUpdateCarActivity extends BaseActivity {
	private Logger log = Logger.createLogger(AddUpdateCarActivity.class);
	public static final int PARAM_EDIT = 1;
	public static final String SELECTION_ID_FILTER = "_id = ?";
	private int mode = -1;
	private long id = -1;
	private long selectedCarId;

	private CheckBox selectCarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_car_fragment);

		selectCarView = (CheckBox) findViewById(R.id.selectCar);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
			populateEntity();
		}

		selectedCarId = DBUtils.getActiveCarId(getContentResolver());
		if (selectedCarId == -1) {
			selectCarView.setVisibility(View.GONE);
		}

		getSupportActionBar().setHomeButtonEnabled(true);

	}

	private void populateEntity() {
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

			if (active) {
				selectCarView.setVisibility(View.GONE);
			}

			carNameView.setText(carName);
		}
		c.close();
	}

	@Override
	public String getSubTitle() {
		return (mode == AddUpdateCarActivity.PARAM_EDIT)
				? getString(R.string.edit_car_title)
				: getString(R.string.add_car_title);
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
				EditText carName = (EditText) findViewById(R.id.carName);
				String carNameValue = carName.getText().toString();

				boolean valid = validateData(carNameValue);
				if (valid) {
					saveOrUpdateCar(carNameValue);
					upToParent();
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

	private void upToParent() {
		Intent upIntent = NavUtils.getParentActivityIntent(this);
		NavUtils.navigateUpTo(this, upIntent);
	}

	private boolean validateData(String carNameValue) {
		boolean result = true;
		if (carNameValue == null || carNameValue.trim().length() == 0) {
			findViewById(R.id.carNameError).setVisibility(View.VISIBLE);
			result = false;
		}

		return result;
	}

	private void saveOrUpdateCar(String carNameValue) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, carNameValue);

		if (mode == AddUpdateCarActivity.PARAM_EDIT) {
			if (selectCarView.isChecked()) {
				resetCurrentActiveFlag();
				cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
			}
			getContentResolver().update(ProviderDescriptor.Car.CONTENT_URI, cv, "_id = ?",
					new String[]{String.valueOf(id)});
		} else {
			if (selectCarView.isChecked()) {
				resetCurrentActiveFlag();
			}

			if (selectedCarId == -1 || selectCarView.isChecked()) {
				cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
			}
			getContentResolver().insert(ProviderDescriptor.Car.CONTENT_URI, cv);
		}
	}

	private void resetCurrentActiveFlag() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 0);

		getContentResolver().update(ProviderDescriptor.Car.CONTENT_URI, cv, "active_flag = 1",
				null);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_delete).setVisible(mode == AddUpdateCarActivity.PARAM_EDIT);
		return super.onPrepareOptionsMenu(menu);
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (ConfirmDeleteDialog.REQUEST_CODE_DELETE == requestCode) {
			long carCount = DBUtils.getCount(getContentResolver(), ProviderDescriptor.Car.CONTENT_URI);
			log.debug("Car Count " + carCount);

			if (id == selectedCarId && carCount > 1) {
				getMediator().showAlert(getString(R.string.deleteActive));
			} else {
				DBUtils.deleteCascadeCar(getContentResolver(), id);
				upToParent();
			}
		}
	}
}

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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;
import com.carlogbook.db.ProviderDescriptor;

public class AddUpdateCarActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_car_fragment);

	}

	@Override
	public String getSubTitle() {
		return getString(R.string.add_car_title);
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
				saveCar();
				upToParent();
				break;
			}

			case R.id.action_cancel: {
				upToParent();
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

	private void saveCar() {
		EditText carName = (EditText) findViewById(R.id.carName);
		String carNameValue = carName.getText().toString();
		if (carNameValue != null && carNameValue.trim().length() > 0) {
			ContentValues cv = new ContentValues();
			cv.put(ProviderDescriptor.Car.Cols.NAME, carNameValue);
			getContentResolver().insert(ProviderDescriptor.Car.CONTENT_URI, cv);
		} else {
			findViewById(R.id.carNameError).setVisibility(View.VISIBLE);
		}
	}
}

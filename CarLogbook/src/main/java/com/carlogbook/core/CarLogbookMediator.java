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
package com.carlogbook.core;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import com.carlogbook.R;
import com.carlogbook.db.DBUtils;
import com.carlogbook.db.ProviderDescriptor;
import com.carlogbook.ui.AboutFragment;
import com.carlogbook.ui.AddUpdateCarActivity;
import com.carlogbook.ui.AddUpdateDataValue;
import com.carlogbook.ui.AddUpdateFuelLogActivity;
import com.carlogbook.ui.AddUpdateLogActivity;
import com.carlogbook.ui.AddUpdateNotificationActivity;
import com.carlogbook.ui.AlertDialog;
import com.carlogbook.ui.ConfirmDeleteDialog;
import com.carlogbook.ui.DataValueActivity;
import com.carlogbook.ui.LogbookFragment;
import com.carlogbook.ui.MyCarsFragment;
import com.carlogbook.ui.NotificationFragment;
import com.carlogbook.ui.ReportsFramgent;
import com.carlogbook.ui.SettingsFragment;

public class CarLogbookMediator extends AppMediator {
	private boolean drawerOpenned;

	public CarLogbookMediator(ActionBarActivity activity) {
		super(activity);
	}

	public void showLogbook() {
		replaceMainContainter(new LogbookFragment());
	}

	public void showReports() {
		replaceMainContainter(new ReportsFramgent());
	}

	public void showMyCars() {
		replaceMainContainter(new MyCarsFragment());
	}

	public void showNotifications() {
		replaceMainContainter(new NotificationFragment());
	}

	public void showSettings() {
		replaceMainContainter(new SettingsFragment());
	}

	public void showAbout() {
		replaceMainContainter(new AboutFragment());
	}

	public void showAddCar() {
		startActivity(AddUpdateCarActivity.class);
	}

	public void showViewCar(long id) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.MODE_KEY, AddUpdateCarActivity.PARAM_EDIT);
		params.putLong(BaseActivity.ENTITY_ID, id);
		startActivity(AddUpdateCarActivity.class, params);
	}

	public void showAddNotification() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateNotificationActivity.class);
		}
	}

	public void showAddFuelLog() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateFuelLogActivity.class);
		}
	}

	public void showAddLog() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateLogActivity.class);
		}
	}

	public void showConfirmDeleteView() {
		DialogFragment confirmDeleteDialog = ConfirmDeleteDialog.newInstance();
		confirmDeleteDialog.show(activity.getSupportFragmentManager(), "confirm_delete");
	}

	public void showAlert(String text) {
		AlertDialog alertDialog = AlertDialog.newInstance();
		alertDialog.setText(text);
		alertDialog.show(activity.getSupportFragmentManager(), "alert");
	}

	public void showDataValues(int type) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.TYPE_KEY, type);
		startActivity(DataValueActivity.class, params);
	}

	public void showAddDataValue(int type) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.TYPE_KEY, type);
		startActivity(AddUpdateDataValue.class, params);
	}

	public void showUpdateDataValue(int type, long id) {
		if (DBUtils.isDataValueIsSystemById(activity.getContentResolver(), id)) {
			showAlert(activity.getString(R.string.value_sys_error));
		} else {
			Bundle params = new Bundle();
			params.putInt(BaseActivity.TYPE_KEY, type);
			params.putInt(BaseActivity.MODE_KEY, AddUpdateDataValue.PARAM_EDIT);
			params.putLong(BaseActivity.ENTITY_ID, id);
			startActivity(AddUpdateDataValue.class, params);
		}
	}

	public boolean isDrawerOpenned() {
		return drawerOpenned;
	}

	public void setDrawerOpenned(boolean drawerOpenned) {
		this.drawerOpenned = drawerOpenned;
	}
}

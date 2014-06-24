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

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import com.carlogbook.ui.AboutFragment;
import com.carlogbook.ui.AddUpdateCarActivity;
import com.carlogbook.ui.AddUpdateFuelLogActivity;
import com.carlogbook.ui.AddUpdateLogActivity;
import com.carlogbook.ui.AddUpdateNotificationActivity;
import com.carlogbook.ui.AlertDialog;
import com.carlogbook.ui.ConfirmDeleteDialog;
import com.carlogbook.ui.LogbookFragment;
import com.carlogbook.ui.MyCarsFragment;
import com.carlogbook.ui.NotificationFragment;
import com.carlogbook.ui.ReportsFramgent;
import com.carlogbook.ui.SettingsFragment;

public class CarLogbookMediator extends AppMediator {

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
		startActivity(AddUpdateNotificationActivity.class);
	}

	public void showAddFuelLog() {
		startActivity(AddUpdateFuelLogActivity.class);
	}

	public void showAddLog() {
		startActivity(AddUpdateLogActivity.class);
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
}

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

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.enadein.carlogbook.core.SaveUpdateBaseActivity;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;
import java.util.Date;

abstract public class  BaseLogAcivity extends SaveUpdateBaseActivity implements DatePickerDialog.OnDateSetListener  {
	protected Date date = new Date();

	protected int getPositionFromAdapterById(SimpleCursorAdapter adapter, long id) {

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


	protected void setComments(ContentValues cv, String comment) {
		if (!"".equals(comment)) {
			cv.put(ProviderDescriptor.Log.Cols.CMMMENT, comment);
		}
	}

	@Override
	protected void deleteEntity() {
		getContentResolver().delete(ProviderDescriptor.Log.CONTENT_URI, "_id = ?", new String[] {String.valueOf(id)});
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	protected void preDelete() {
		getMediator().showConfirmDeleteView();
	}

	@Override
	public void onDateSet(DatePicker datePicker, int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		date = c.getTime();
		setDateText(CommonUtils.formatDate(date));

	}

	abstract void setDateText(String text);

	public void showDatePickerDialog(View v) {
		DatePickerFragment datePickerFragment = new DatePickerFragment();
		datePickerFragment.setListener(date, this);
		datePickerFragment.show(getSupportFragmentManager(), "date_picker");
	}
}

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

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.carlogbook.core.BaseActivity;
import com.carlogbook.db.CommonUtils;
import com.carlogbook.db.DBUtils;

import java.util.Date;

public class BaseLogAcivity extends BaseActivity {
	protected boolean validateView(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		double valueDouble = CommonUtils.getPriceValue(editView);
		boolean result = valueDouble > 0;

		errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
		return result;
	}

	protected boolean validateTextView(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		boolean result = CommonUtils.isNotEmpty(editView.getText().toString());

		errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
		return result;
	}

	protected boolean validateOdometer(int errorViewId, EditText view, Date date) {
		String stringValue = view.getText().toString();

		long currentOdometer = (CommonUtils.isNotEmpty(stringValue)) ? Long.valueOf(stringValue) : 0;

		long odometerMin = DBUtils.getMinOdometerValueByDate(getContentResolver(), date.getTime());
		long odometerMax = DBUtils.getMaxOdometerValueByDate(getContentResolver(), date.getTime());

		boolean result = (odometerMin < currentOdometer) && (currentOdometer < odometerMax);
		findViewById(errorViewId).setVisibility((!result)?View.VISIBLE : View.GONE);
		return result;
	}
}

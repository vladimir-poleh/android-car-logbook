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
package com.carlogbook.db;

import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

	public static double getPriceValue(EditText text) {
		double result = 0;
		String stringValue = text.getText().toString().trim();
		if (stringValue.length() > 0) {
			result = parsePriceString(stringValue);
		}
		return result;
	}

	public static double parsePriceString(String string) {
		NumberFormat format = getPriceNumberFormat();

		Number result = 0;
		try {
			result = format.parse(string);
		} catch (ParseException e) {
			//nothing
		}

		return result.doubleValue();
	}

	public static String formatPrice(double price) {
		NumberFormat format = getPriceNumberFormat();
		String result = format.format(price);
		return  "0.0".equals(result) ? "" : result;
	}

	public static double div(double a, double b) {
		return (b == 0) ? 0 : a / b;
	}

	private static NumberFormat getPriceNumberFormat() {
		NumberFormat format = DecimalFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(1);
		format.setMaximumIntegerDigits(10);
		format.setMinimumIntegerDigits(1);
		format.setGroupingUsed(false);
		return format;
	}

	public static boolean isNotEmpty(String str) {
		return (str != null && !str.trim().equals(""));
	}
	public static boolean isEmpty(String str) {
		return (str == null || str.trim().equals(""));
	}
}

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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.Spinner;

import java.util.Calendar;

public class DBUtils {
	public static long getActiveCarId(ContentResolver cr) {
		long result = -1;

		Cursor c = cr.query(ProviderDescriptor.Car.CONTENT_URI, null, "active_flag = 1", null, null);

		if (c != null && c.moveToFirst()) {
			int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
			result = c.getLong(idIdx);
			c.close();
		}

		return result;
	}

	public static void deleteCascadeCar(ContentResolver cr, long id) {
		cr.delete(ProviderDescriptor.Car.CONTENT_URI, "_id = ?", new String[] {String.valueOf(id)});
	}

	public static long getCount(ContentResolver cr, Uri uri) {
		long result = 0;
		Cursor c = cr.query(ProviderDescriptor.Car.CONTENT_URI, new String[] {"count(*)"}, null, null, null);
		if (c != null && c.moveToFirst()) {
			result = c.getLong(0);
			c.close();
		}

		return result;
	}

	public static void setDafaultId(ContentResolver cr,  long type, long id) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 0);
		cr.update(ProviderDescriptor.DataValue.CONTENT_URI, cv, "TYPE = ? and DEFAULT_FLAG = 1",  new String[] {String.valueOf(type)});
		cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 1);
		cr.update(ProviderDescriptor.DataValue.CONTENT_URI, cv, "TYPE = ? and _id = ?",  new String[] {String.valueOf(type), String.valueOf(id)});
	}

	public static long getDefaultId(ContentResolver cr,
	                        int type) {
		long result = -1;

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		Cursor cursor = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
				"TYPE = ? and DEFAULT_FLAG = 1", new String[] {String.valueOf(type)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(0);
			cursor.close();
		}

		return result;
	}

	public static String getDataValueNameById(ContentResolver cr,
	                                long id) {
		String result = "";

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		Cursor cursor = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
				"_id = ?", new String[] {String.valueOf(id)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			int nameIdx = cursor.getColumnIndex(ProviderDescriptor.DataValue.Cols.NAME);
			result = cursor.getString(nameIdx);
			cursor.close();
		}

		return result;
	}

	public static int getLogTypeById(ContentResolver cr,
	                                          long id) {
		int result = ProviderDescriptor.Log.Type.FUEL;

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.Log.Cols.TYPE_LOG};

		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI, queryCols,
				"_id = ?", new String[] {String.valueOf(id)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			int nameIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.TYPE_LOG);
			result = cursor.getInt(nameIdx);
			cursor.close();
		}

		return result;
	}

	public static long getMaxOdometerValue(ContentResolver cr) {
		long result = 0;
		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] {ProviderDescriptor.Log.Cols.ODOMETER},
				null, null, ProviderDescriptor.Log.Cols.ODOMETER + " DESC");

		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(0);
			cursor.close();
		}

		return result;
	}

	public static long getMinOdometerValueByDate(ContentResolver cr, long date) {
		long result = 0;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		long dateT = calendar.getTimeInMillis();

		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] {ProviderDescriptor.Log.Cols.ODOMETER},
				"date < ?", new String[] {String.valueOf(dateT)}, ProviderDescriptor.Log.Cols.ODOMETER + " DESC");

		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(0);
			cursor.close();
		}

		return result;
	}

	public static long getMaxOdometerValueByDate(ContentResolver cr, long date) {
		long result = Long.MAX_VALUE;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		long dateT = calendar.getTimeInMillis();

		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] {ProviderDescriptor.Log.Cols.ODOMETER},
				"date > ?", new String[] {String.valueOf(dateT)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(0);
			cursor.close();
		}

		return result;
	}


	public static double getLastPriceValue(ContentResolver cr) {
		double result = 0;
		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] { ProviderDescriptor.Log.Cols._ID, ProviderDescriptor.Log.Cols.DATE,ProviderDescriptor.Log.Cols.PRICE },
				ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?", new String[] {String.valueOf(ProviderDescriptor.Log.Type.FUEL)}, ProviderDescriptor.Log.Cols.DATE + " DESC");

		if (cursor != null && cursor.moveToFirst()) {
			int priceIDX = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.PRICE);
			result = cursor.getDouble(priceIDX);
			cursor.close();
		}

		return result;
	}


	public static boolean isDataValueIsSystemById(ContentResolver cr,
	                                          long id) {
		boolean result = false;

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.SYSTEM};

		Cursor cursor = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
				"_id = ?", new String[] {String.valueOf(id)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			int sysIdx = cursor.getColumnIndex(ProviderDescriptor.DataValue.Cols.SYSTEM);
			result = cursor.getInt(sysIdx) > 0;
			cursor.close();
		}

		return result;
	}


	public static boolean isOdometerValid(ContentResolver cr, int dodmeterValue, long logTime) {
		boolean result = false;


		return result;
	}


	//TODO
	public static void test(ContentResolver cr,
	                                        int type) {

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		Cursor cursor = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
				"TYPE = ?", new String[] {String.valueOf(type)}, null);
		if (cursor != null && cursor.moveToFirst()) {
				do {
					Log.e("HELLO ",  cursor.getInt(0) + "//" + cursor.getString(1));
				} while (cursor.moveToNext());
			cursor.close();
		}
	}

	public static void deleteTest(ContentResolver cr) {
		cr.delete(ProviderDescriptor.DataValue.CONTENT_URI, "_id != ?", new String[] {String.valueOf(-1)});
	}
}

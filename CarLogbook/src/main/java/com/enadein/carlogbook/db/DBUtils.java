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
package com.enadein.carlogbook.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.enadein.carlogbook.core.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

public class DBUtils {
	private static Logger log = Logger.createLogger(DBUtils.class);

	public static final String CAR_SELECTION = ProviderDescriptor.Log.Cols.CAR_ID + " = ?";

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
		Cursor c = cr.query(uri, new String[] {"count(*)"}, null, null, null);
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
		long carId = getActiveCarId(cr);

		long result = 0;
		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] {ProviderDescriptor.Log.Cols.ODOMETER},
				ProviderDescriptor.Log.Cols.CAR_ID + " = ?",  new String[] {String.valueOf(carId)}, ProviderDescriptor.Log.Cols.ODOMETER + " DESC");

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
				ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?" , new String[] {String.valueOf(ProviderDescriptor.Log.Type.FUEL)}, ProviderDescriptor.Log.Cols.DATE + " DESC");

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

	//for reports ========================================================

	public static int getOdometerCount(ContentResolver cr) {
		return getOdometerCount(cr, 0, 0, -1);
	}

	//ODOMETER COUNT BY LOG TYPE
	public static int getOdometerCount(ContentResolver cr, long from, long to, int type) {
		int min = getMinOdometer(cr, from, to, type);
		int max = getOdometerMax(cr, from, to, type);

		return max - min;
	}



	public static int getMinOdometer(ContentResolver cr, long from, long to, int type) {
		int min = 0;

		String selection = buildCarDateSelection(from, to, type, null);
		String[] args = buildCarDateSelectionArgs(cr, from, to, type, null);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] { "min("+ProviderDescriptor.Log.Cols.ODOMETER + ") as min"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			min = getIntByName(c, "min");
			c.close();
		}

		return min;
	}

	public static int getOdometerMax(ContentResolver cr, long from, long to, int type) {
		int max = 0;

		String selection = buildCarDateSelection(from, to, type, null);
		String[] args = buildCarDateSelectionArgs(cr, from, to, type, null);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[] { "max("+ProviderDescriptor.Log.Cols.ODOMETER + ") as max"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			max = getIntByName(c, "max");
			c.close();
		}

		return max;
	}

	public static double getTotalPrice(ContentResolver cr) {
		return getTotalPrice(cr, 0, 0, -1, null);
	}

	public static double getTotalPrice(ContentResolver cr, long from, long to, int type) {
		return getTotalPrice(cr, from, to, type, null);
	}
	//TOTAL PRICE BY TYPE
	public static double getTotalPrice(ContentResolver cr, long from, long to, int type,  int[] otherTypes) {
		double result = 0;

		String selection = buildCarDateSelection(from, to, type, otherTypes);
		String[] args = buildCarDateSelectionArgs(cr, from, to, type, otherTypes);

		Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
				new String[] { "sum("+ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			result = getDubleByName(c, "sum");
		}

		return result;
	}

	public static double getPricePer1km(ContentResolver cr, long from, long to) {
		double result = 0;

		int odometerCount = getOdometerCount(cr, from, to,  -1);

		if (odometerCount > 0) {
			double price = getTotalPrice(cr, from, to, -1,  null);

			result = price / odometerCount;
		}

		return result;
	}

	public static int getTotalFuel(ContentResolver cr, long from, long to, int firstOdometer) {
		int result = 0;
//		Arrays.cop
		return result;
	}

	public double getAvgFuel(ContentResolver cr) {
		double result = 0;

//		int odometerCount = getOdometerCount(cr);


		return 0;
	}

	public static double getAvgFuelByType(int fueltypeId, int gasStationId) {
		return 0;
	}

	public static String buildCarDateSelection(long from, long to, int type, int[] otherTypes) {
		StringBuilder sb = new StringBuilder();
		sb.append(CAR_SELECTION);

		if (from > 0) {
			sb.append(" and ").append(ProviderDescriptor.Log.Cols.DATE).append(" >= ?");

		}

		if (to > 0) {
			sb.append(" and ").append(ProviderDescriptor.Log.Cols.DATE).append(" <= ?");
		}

		if (type != -1) {
			sb.append(" and ").append(ProviderDescriptor.Log.Cols.TYPE_LOG).append(" = ?");
		}

		String result = sb.toString();
		log.debug(result);

		return result;
	}

	public static String[] buildCarDateSelectionArgs(ContentResolver cr, long from, long to,  int type, int[] otherTypes) {
		Collection<String> args = new ArrayList<String>();
		args.add(String.valueOf(getActiveCarId(cr)));

		if (from > 0) {
			args.add(String.valueOf(from));
		}

		if (from > 0) {
			args.add(String.valueOf(to));
		}

		if (type != -1) {
			args.add(String.valueOf(type));
		}

		return args.toArray(new String[args.size()]);
	}

	public static String getStringByName(Cursor c, String name) {
		return c.getString(c.getColumnIndex(name));
	}

	public static long getLongByName(Cursor c, String name) {
		return c.getLong(c.getColumnIndex(name));
	}

	public static double getDubleByName(Cursor c, String name) {
		return c.getDouble(c.getColumnIndex(name));
	}

	public static int getIntByName(Cursor c, String name) {
		return c.getInt(c.getColumnIndex(name));
	}

	public static boolean isCursorHasValue(Cursor c) {
		return c != null && c.moveToFirst();
	}
}

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

import com.enadein.carlogbook.bean.FuelRateBean;
import com.enadein.carlogbook.core.Logger;

import java.util.ArrayList;
import java.util.Collection;

public class DBUtils {
	private static Logger log = Logger.createLogger(DBUtils.class);

	public static final String CAR_SELECTION = ProviderDescriptor.Log.Cols.CAR_ID + " = ?";
	public static final String CAR_SELECTION_NOTIFY = ProviderDescriptor.Notify.Cols.CAR_ID + " = ?";
	public static final String CAR_SELECTION_RATE = ProviderDescriptor.FuelRateView.Cols.CAR_ID + " = ?";

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

	public static long getValueId(ContentResolver cr, Uri uri, String field, String value) {
		long result = -1;

		Cursor c = cr.query(uri, null, field + " = ?", new String[] {value}, null);

		if (c != null && c.moveToFirst()) {
			int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
			result = c.getLong(idIdx);
			c.close();
		}

		return result;
	}

	public static long getFuelRateId(ContentResolver cr, String type, String station) {
		long result = -1;

		Cursor c = cr.query(ProviderDescriptor.FuelRate.CONTENT_URI,
				null, ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID + " = ? and "
						+ ProviderDescriptor.FuelRate.Cols.STATION_ID + " = ?" , new String[] {type, station}, null);

		if (c != null && c.moveToFirst()) {
			int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
			result = c.getLong(idIdx);
			c.close();
		}

		return result;
	}

	public static void deleteCascadeCar(ContentResolver cr, long id) {
		cr.delete(ProviderDescriptor.Log.CONTENT_URI, CAR_SELECTION, new String[] {String.valueOf(id)});
		cr.delete(ProviderDescriptor.Notify.CONTENT_URI, CAR_SELECTION_NOTIFY, new String[] {String.valueOf(id)});
		cr.delete(ProviderDescriptor.Car.CONTENT_URI, "_id = ?", new String[]{String.valueOf(id)});
	}

	public static long getCount(ContentResolver cr, Uri uri) {
		return getCount(cr, uri, null, null);
	}
	public static long getCount(ContentResolver cr, Uri uri, String selection, String[] args) {
		long result = 0;

		Cursor c = cr.query(uri, null, selection, args, null);
		if (c != null && c.moveToFirst()) {
			result = c.getCount();
			c.close();
		}

		return result;
	}

	public static  boolean hasValue(ContentResolver cr, Uri uri, String field, String value) {
		return getCount(cr, uri, field + " = ?", new String[] {value}) > 0;
	}

	public static void updateFuelRate(ContentResolver cr, int curentOdometer, double fuelVolume) {
		long carId = getActiveCarId(cr);
		String carSelection = " and " + ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
				.build(), null, ProviderDescriptor.Log.Cols.TYPE_LOG + " = " + ProviderDescriptor.Log.Type.FUEL + carSelection, null, ProviderDescriptor.Log.Cols.DATE + " DESC");

		int odometer = -1;
		long stationId = 0;
		long fuelTypeId = 0;

		if (isCursorHasValue(c)) {
			odometer = getIntByName(c, ProviderDescriptor.Log.Cols.ODOMETER);
			stationId = getLongByName(c, ProviderDescriptor.Log.Cols.FUEL_STATION_ID);
			fuelTypeId = getLongByName(c, ProviderDescriptor.Log.Cols.FUEL_TYPE_ID);
		}
		c.close();

		if (odometer != -1) {
			double rate = (fuelVolume / (curentOdometer - odometer)) * 100;

			FuelRateBean fuelRateBean = getCurrentFuelRate(cr, carId, fuelTypeId, stationId);

			if (fuelRateBean == null) {
				fuelRateBean = new FuelRateBean();
				fuelRateBean.setCarId(carId);
				fuelRateBean.setStationId(stationId);
				fuelRateBean.setFuelTypeId(fuelTypeId);
				fuelRateBean.setRate(rate);
				fuelRateBean.setMinRate(rate);
				fuelRateBean.setMaxRate(rate);
				cr.insert(ProviderDescriptor.FuelRate.CONTENT_URI, fuelRateBean.getCV());
			} else {
				fuelRateBean.setRate(rate);
				if (rate < fuelRateBean.getMinRate()) {
					fuelRateBean.setMinRate(rate);
				}
				if (rate > fuelRateBean.getMaxRate()) {
					fuelRateBean.setMaxRate(rate);
				}
				cr.update(ProviderDescriptor.FuelRate.CONTENT_URI, fuelRateBean.getCV(), "_id = ?",
						new String[]{String.valueOf(fuelRateBean.getId())});
			}

		}
	}

	private static FuelRateBean getCurrentFuelRate(ContentResolver cr, long carId, long fuelTypeId, long sstationId) {
		FuelRateBean fuelRateBean = null;
		StringBuilder sb = new StringBuilder();
		sb.append(ProviderDescriptor.FuelRate.Cols.CAR_ID)
				.append(" = ").append(carId)
				.append(" and ").append(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID)
				.append(" = ").append(fuelTypeId)
				.append(" and ").append(ProviderDescriptor.FuelRate.Cols.STATION_ID)
				.append(" = ").append(sstationId);

		Cursor c = cr.query(ProviderDescriptor.FuelRate.CONTENT_URI, null, sb.toString(), null, null);

		if (isCursorHasValue(c)) {
			fuelRateBean = new FuelRateBean();
			fuelRateBean.populate(c);
		}

		return fuelRateBean;
	}


	public static void setDafaultId(ContentResolver cr, long type, long id) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 0);
		cr.update(ProviderDescriptor.DataValue.CONTENT_URI, cv, "TYPE = ? and DEFAULT_FLAG = 1", new String[]{String.valueOf(type)});
		cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 1);
		cr.update(ProviderDescriptor.DataValue.CONTENT_URI, cv, "TYPE = ? and _id = ?", new String[]{String.valueOf(type), String.valueOf(id)});
	}

	public static long getDefaultId(ContentResolver cr,
	                                int type) {
		long result = -1;

		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		Cursor cursor = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, queryCols,
				"TYPE = ? and DEFAULT_FLAG = 1", new String[]{String.valueOf(type)}, null);

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
				"_id = ?", new String[]{String.valueOf(id)}, null);

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
				"_id = ?", new String[]{String.valueOf(id)}, null);

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
				new String[]{ProviderDescriptor.Log.Cols.ODOMETER},
				ProviderDescriptor.Log.Cols.CAR_ID + " = ?", new String[]{String.valueOf(carId)}, ProviderDescriptor.Log.Cols.ODOMETER + " DESC");

		if (cursor != null && cursor.moveToFirst()) {
			result = cursor.getLong(0);
			cursor.close();
		}

		return result;
	}

	public static double getLastPriceValue(ContentResolver cr) {
		double result = 0;
		Cursor cursor = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[]{ProviderDescriptor.Log.Cols._ID, ProviderDescriptor.Log.Cols.DATE, ProviderDescriptor.Log.Cols.PRICE},
				ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?", new String[]{String.valueOf(ProviderDescriptor.Log.Type.FUEL)}, ProviderDescriptor.Log.Cols.DATE + " DESC");

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
				"_id = ?", new String[]{String.valueOf(id)}, null);

		if (cursor != null && cursor.moveToFirst()) {
			int sysIdx = cursor.getColumnIndex(ProviderDescriptor.DataValue.Cols.SYSTEM);
			result = cursor.getInt(sysIdx) > 0;
			cursor.close();
		}

		return result;
	}

	public static boolean isUsedInLog(ContentResolver cr, String field,  long id) {
		return getCount(cr, ProviderDescriptor.Log.CONTENT_URI, field + " = ?" ,  new String[] {String.valueOf(id)}) > 0;
	}

	public static boolean isStationUsed(ContentResolver cr, long id) {
		return isUsedInLog(cr, ProviderDescriptor.Log.Cols.FUEL_STATION_ID, id);
	}

	public static boolean isFuelTypeUsed(ContentResolver cr, long id) {
		return isUsedInLog(cr, ProviderDescriptor.Log.Cols.FUEL_TYPE_ID, id);
	}


	//for reports ========================================================

	public static int getOdometerCount(ContentResolver cr) {
		return getOdometerCount(cr, 0, 0, -1);
	}

	//ODOMETER COUNT BY LOG TYPE
	public static int getOdometerCount(ContentResolver cr, long from, long to, int type) {
		int min = getMinOdometer(cr, from, to, type);
		int minFrom = getOdometerMax(cr, 0, from, type);

		if (from > 0 && minFrom < min) {
			int globalMin = getMinOdometer(cr, 0, 0, type);
			min = (globalMin == min) ? min : minFrom;
		}

		int max = getOdometerMax(cr, from, to, type);

		return max - min;
	}


	public static int getMinOdometer(ContentResolver cr, long from, long to, int type) {
		int min = 0;

		String selection = buildCarDateSelection(from, to, type, null);
		String[] args = buildCarDateSelectionArgs(cr, from, to, type);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[]{"min(" + ProviderDescriptor.Log.Cols.ODOMETER + ") as min"},
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
		String[] args = buildCarDateSelectionArgs(cr, from, to, type);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[]{"max(" + ProviderDescriptor.Log.Cols.ODOMETER + ") as max"},
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

	public static double getTotalPrice(ContentResolver cr, long from, long to, int type, int[] otherTypes) {
		return getTotalPrice(cr, from, to, type, otherTypes, false);
	}
	public static double getTotalPrice(ContentResolver cr, long from, long to, int type, int[] otherTypes, boolean skipFirst) {
		double result = 0;

		String selection = buildCarDateSelection(from, to, type, otherTypes);
		String[] args = buildCarDateSelectionArgs(cr, from, to, type);

		if (skipFirst) {
			int id = getLogIdItemFirstItemNoType(cr);
			selection += " and " + ProviderDescriptor.Log.Cols._ID + " != " + id;
		}

		Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
				new String[]{"sum(" + ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			result = getDubleByName(c, "sum");
		}

		return result;
	}

	public static double getPricePer1km(ContentResolver cr, long from, long to) {
		double result = 0;

		int odometerCount = getOdometerCount(cr, from, to, -1);

		if (odometerCount > 0) {
			double price = getTotalPrice(cr, from, to, -1, null, true);

			result = price / odometerCount;
		}

		return result;
	}


	public static int getLogIdItemFirstItem(ContentResolver cr) {
		int id = -1;

		long carId = getActiveCarId(cr);

		String carSelection = " and " + ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
				.build(), null, ProviderDescriptor.Log.Cols.TYPE_LOG + " = " + ProviderDescriptor.Log.Type.FUEL + carSelection, null, ProviderDescriptor.Log.Cols.DATE + " ASC");
		if (isCursorHasValue(c)) {
			id = getIntByName(c, ProviderDescriptor.Log.Cols._ID);
		}


		return id;
	}

	public static int getLogIdItemFirstItemNoType(ContentResolver cr) {
		int id = -1;

		long carId = getActiveCarId(cr);

		String carSelection = ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
				.build(), null, carSelection, null, ProviderDescriptor.Log.Cols.DATE + " ASC");
		if (isCursorHasValue(c)) {
			id = getIntByName(c, ProviderDescriptor.Log.Cols._ID);
		}


		return id;
	}

	public static long getLastEventDate(ContentResolver cr, int type, int otherTypeId) {
		long result = 0;

		long carId = getActiveCarId(cr);
		String carSelection = " and " + ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

		String extraSelect = (otherTypeId > -1) ? " and " + ProviderDescriptor.Log.Cols.TYPE_ID + " = " + otherTypeId : "";

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
				.build(), null, ProviderDescriptor.Log.Cols.TYPE_LOG + " = " + type + extraSelect + carSelection, null, ProviderDescriptor.Log.Cols.DATE + " DESC");
		if (isCursorHasValue(c)) {
			result = getLongByName(c, ProviderDescriptor.Log.Cols.DATE);
		}


		return result;
	}


	public static int getTotalFuel(ContentResolver cr, long from, long to, boolean skipFirst) {
		int result = 0;

		String selection = buildCarDateSelection(from, to, ProviderDescriptor.Log.Type.FUEL, null);

		if (skipFirst) {
			int id = getLogIdItemFirstItem(cr);
			selection += " and " + ProviderDescriptor.Log.Cols._ID + " != " + id;
		}

		String[] args = buildCarDateSelectionArgs(cr, from, to, ProviderDescriptor.Log.Type.FUEL);
		Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
				new String[]{"sum(" + ProviderDescriptor.LogView.Cols.FUEL_VOLUME + ") as sum"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			result = getIntByName(c, "sum");
		}


		return result;
	}

	public static double getAvgFuel(ContentResolver cr, long from, long to) {
		double result = 0;

		int odometerCount = getOdometerCount(cr, from, to, ProviderDescriptor.Log.Type.FUEL);
		double allFuel = getTotalFuel(cr, from, to, true);

		if (odometerCount > 0) {
			result = (allFuel / odometerCount) * 100;
		}

		return result;
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

		if (otherTypes != null) {
			sb.append(" and (");
			for (int i = 0; i < otherTypes.length; i++) {
				if (i > 0) {
					sb.append(" or ");
				}
				sb.append(ProviderDescriptor.Log.Cols.TYPE_ID).append(" = ").append(otherTypes[i]);
			}
			sb.append(")");
		}

		String result = sb.toString();
		log.debug(result);

		return result;
	}

	public static String[] buildCarDateSelectionArgs(ContentResolver cr, long from, long to, int type) {
		Collection<String> args = new ArrayList<String>();
		args.add(String.valueOf(getActiveCarId(cr)));

		if (from > 0) {
			args.add(String.valueOf(from));
		}

		if (to > 0) {
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

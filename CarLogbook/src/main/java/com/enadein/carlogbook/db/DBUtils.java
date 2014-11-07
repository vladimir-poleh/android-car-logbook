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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.FuelRateBean;
import com.enadein.carlogbook.core.Logger;
import com.enadein.carlogbook.core.UnitFacade;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Collection;

public class DBUtils {
	private static Logger log = Logger.createLogger(DBUtils.class);

    public static final long DAY = 60 * 60 * 24 * 1000;
    public static final long MONTH = DAY * 31;
    public static final long YEAR = MONTH * 12;

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

    public static String getActiveCarName(ContentResolver cr, long id) {
        String result = "";

        Cursor c = cr.query(ProviderDescriptor.Car.CONTENT_URI, null, "_id = ?", new String[] {String.valueOf(id)}, null);

        if (c != null && c.moveToFirst()) {
            int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols.NAME);
            result = c.getString(idIdx);
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

    public static long getDataValueId(ContentResolver cr , String value, String type) {
        long result = -1;

        Cursor c = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, null, ProviderDescriptor.DataValue.Cols.TYPE + " = ? and " + ProviderDescriptor.DataValue.Cols.TYPE + " = ?", new String[] {value, type}, null);

        if (c != null && c.moveToFirst()) {
            int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
            result = c.getLong(idIdx);
            c.close();
        }

        return result;
    }

	public static String getSettValue(ContentResolver cr, String key) {
		String result = null;

		Cursor c = cr.query(ProviderDescriptor.Sett.CONTENT_URI, null, ProviderDescriptor.Sett.Cols.KEY + " = ?", new String[] {key}, null);

		if (c != null && c.moveToFirst()) {
			int idIdx = c.getColumnIndex(ProviderDescriptor.Sett.Cols.VALUE);
			result = c.getString(idIdx);
			c.close();
		}

		return result;
	}

	public static void createSetValue(ContentResolver cr, String key, String value) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Sett.Cols.KEY, key);
		cv.put(ProviderDescriptor.Sett.Cols.VALUE, value);
		cr.insert(ProviderDescriptor.Sett.CONTENT_URI, cv);
	}

	public static void updateSetValue(ContentResolver cr, String key, String value) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Sett.Cols.VALUE, value);
		cr.update(ProviderDescriptor.Sett.CONTENT_URI, cv, ProviderDescriptor.Sett.Cols.KEY + " = ?", new String[] {key});
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

	public static void updateFuelRate(ContentResolver cr, int curentOdometer, double fuelVolume, UnitFacade unitFacade) {
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
//			double rate = (fuelVolume / (curentOdometer - odometer)) * 100;
			double rate = unitFacade.getRate(fuelVolume, (curentOdometer - odometer));

			if (rate <= 0 || rate > 1000) {
				return;
			}

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

	public static FuelRateBean getCurrentFuelRate(ContentResolver cr, long carId, long fuelTypeId, long sstationId) {
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
        return getMaxOdometerValue(cr, carId);
    }

    public static   void resetCurrentActiveFlag(ContentResolver cr) {
        ContentValues cv = new ContentValues();
        cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 0);

        cr.update(ProviderDescriptor.Car.CONTENT_URI, cv, "active_flag = 1",
                null);
    }

    public static  void selectActivCar(ContentResolver cr, long carId) {
        resetCurrentActiveFlag(cr);

        ContentValues cv = new ContentValues();
        cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);

        cr.update(ProviderDescriptor.Car.CONTENT_URI, cv, ProviderDescriptor.Car.Cols._ID + " = ? ",
                new String[] {String.valueOf(carId)});
    }

	public static long getMaxOdometerValue(ContentResolver cr, long carId) {
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

    public static boolean isOtherTypeUsed(ContentResolver cr, long id) {
        return isUsedInLog(cr, ProviderDescriptor.Log.Cols.OTHER_TYPE_ID, id);
    }

	public static boolean isFuelTypeUsed(ContentResolver cr, long id) {
		return isUsedInLog(cr, ProviderDescriptor.Log.Cols.FUEL_TYPE_ID, id);
	}


	//for reports ========================================================

	public static int getOdometerCount(long carId, ContentResolver cr) {
		return getOdometerCount(carId, cr, 0, 0, -1);
	}

	//ODOMETER SUM_FUEL BY LOG TYPE
	public static int getOdometerCount(long carId, ContentResolver cr, long from, long to, int type) {
		int min = getMinOdometer(carId, cr, from, to, type);
		int minFrom = getOdometerMax(carId, cr, 0, from, type);

		if (from > 0 && minFrom < min) {
			int globalMin = getMinOdometer(carId, cr, 0, 0, type);
			min = (globalMin == min) ? min : minFrom;
		}

		int max = getOdometerMax(carId, cr, from, to, type);

		return max - min;
	}


	public static int getMinOdometer(long carId, ContentResolver cr, long from, long to, int type) {
		int min = 0;

		String selection = buildCarDateSelection(from, to, type, null);
		String[] args = buildCarDateSelectionArgs(carId, cr, from, to, type);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[]{"min(" + ProviderDescriptor.Log.Cols.ODOMETER + ") as min"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			min = getIntByName(c, "min");
			c.close();
		}

		return min;
	}

	public static int getOdometerMax(long carId, ContentResolver cr, long from, long to, int type) {
		int max = 0;

		String selection = buildCarDateSelection(from, to, type, null);
		String[] args = buildCarDateSelectionArgs(carId, cr, from, to, type);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI,
				new String[]{"max(" + ProviderDescriptor.Log.Cols.ODOMETER + ") as max"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			max = getIntByName(c, "max");
			c.close();
		}

		return max;
	}

    public static long createDataValue(SQLiteDatabase db, String value, int type) {
        ContentValues cv = new ContentValues();
        cv.put(ProviderDescriptor.DataValue.Cols.NAME, value);
        cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
        cv.put(ProviderDescriptor.DataValue.Cols.SYSTEM, 0);
        cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 0);
        return db.insert(ProviderDescriptor.DataValue.TABLE_NAME, null, cv);
    }

    public static long createDataValue(ContentResolver cr, String value, int type) {
        ContentValues cv = new ContentValues();
        cv.put(ProviderDescriptor.DataValue.Cols.NAME, value);
        cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
        cv.put(ProviderDescriptor.DataValue.Cols.SYSTEM, 0);
        cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 0);
        Uri uri = cr.insert(ProviderDescriptor.DataValue.CONTENT_URI, cv);
        return Integer.valueOf(uri.getLastPathSegment());
    }

	public static double getTotalPrice(long carId, ContentResolver cr) {
		return getTotalPrice(carId,cr, 0, 0, -1, null);
	}

	public static double getTotalPrice(long carId, ContentResolver cr, long from, long to, int type) {
		return getTotalPrice(carId, cr, from, to, type, null);
	}

	//TOTAL PRICE BY TYPE

	public static double getTotalPrice(long carId, ContentResolver cr, long from, long to, int type, int[] otherTypes) {
		return getTotalPrice(carId, cr, from, to, type, otherTypes, false);
	}

    public static double getTotalPrice(long carId, ContentResolver cr, long from, long to, int type, int[] otherTypes, boolean skipFirst) {
        return getTotalPrice(carId, cr, from, to, type, otherTypes, skipFirst, -1);
    }

	public static double getTotalPrice(long carId, ContentResolver cr, long from, long to, int type, int[] otherTypes, boolean skipFirst, long otherTypeId) {
		double result = 0;

		String selection = buildCarDateSelection(from, to, type, otherTypes);
		String[] args = buildCarDateSelectionArgs(carId, cr, from, to, type);

		if (skipFirst) {
			int id = getLogIdItemFirstItemNoType(cr);
			selection += " and " + ProviderDescriptor.Log.Cols._ID + " != " + id;
		}

        if (otherTypeId != -1) {
            selection += " and " + ProviderDescriptor.Log.Cols.OTHER_TYPE_ID + " = " + otherTypeId;
        }

		Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
				new String[]{"sum(" + ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			result = getDoubleByName(c, "sum");
		}

		return result;
	}

	public static double getPricePer1km(long carId, ContentResolver cr, long from, long to) {
		double result = 0;

		int odometerCount = getOdometerCount(carId, cr, from, to, -1);

		if (odometerCount > 0) {
			double price = getTotalPrice(carId, cr, from, to, -1, null, true);

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

    public static long getDateLogFirstItem(long carId, ContentResolver cr) {
        long time = System.currentTimeMillis();

        String carSelection = ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

        Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
                .build(), null, carSelection, null, ProviderDescriptor.Log.Cols.DATE + " ASC");
        if (isCursorHasValue(c)) {
            time = getLongByName(c, ProviderDescriptor.Log.Cols.DATE);
        }


        return time;
    }

    public static long getDateLogLastItem(long carId, ContentResolver cr) {
        long time = System.currentTimeMillis();

        String carSelection = ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

        Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI
                .buildUpon()
                .appendQueryParameter(CarLogbookProvider.LIMIT_PARAM, "1")
                .build(), null, carSelection, null, ProviderDescriptor.Log.Cols.DATE + " DESC");
        if (isCursorHasValue(c)) {
            time = getLongByName(c, ProviderDescriptor.Log.Cols.DATE);
        }


        return time;
    }

    public static double getDayPassed(ContentResolver cr, long carId) {
        long startDate = getDateLogFirstItem(carId, cr);
//        long endDate = getDateLogLastItem(carId, cr);
        long endDate = System.currentTimeMillis();

        return calcDayPassed(startDate, endDate);
    }

    public  static double calcDayPassed(long start, long end) {
        double result = (end - start) / DAY;
        return result > 0 ? result : 1;
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
        return getLastEventDate(cr, type, otherTypeId, -1);
    }

	public static long getLastEventDate(ContentResolver cr, int type, int otherTypeId, long otherTypeDataValueId) {
		long result = 0;

		long carId = getActiveCarId(cr);
		String carSelection = " and " + ProviderDescriptor.Log.Cols.CAR_ID + " = " + carId;

        if (otherTypeDataValueId != -1) {
            carSelection += " and " + ProviderDescriptor.Log.Cols.OTHER_TYPE_ID + " = " + otherTypeDataValueId;
        }

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


	public static double getTotalFuel(long carId, ContentResolver cr, long from, long to, boolean skipFirst) {
        double result = 0;

		String selection = buildCarDateSelection(from, to, ProviderDescriptor.Log.Type.FUEL, null);

		if (skipFirst) {
			int id = getLogIdItemFirstItem(cr);
			selection += " and " + ProviderDescriptor.Log.Cols._ID + " != " + id;
		}

		String[] args = buildCarDateSelectionArgs(carId, cr, from, to, ProviderDescriptor.Log.Type.FUEL);
		Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
				new String[]{"sum(" + ProviderDescriptor.LogView.Cols.FUEL_VOLUME + ") as sum"},
				selection, args, null);

		if (isCursorHasValue(c)) {
			result = getDoubleByName(c, "sum");
		}


		return result;
	}

	public static double getAvgFuel(long carId, ContentResolver cr, long from, long to, UnitFacade unitFacade) {
		double result = 0;

		int odometerCount = getOdometerCount(carId, cr, from, to, ProviderDescriptor.Log.Type.FUEL);
		double allFuel = getTotalFuel(carId, cr, from, to, true);

		if (odometerCount > 0) {
//			result = (allFuel / odometerCount) * 100;
			result = unitFacade.getRate(allFuel, odometerCount);
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

	public static String[] buildCarDateSelectionArgs(long carId, ContentResolver cr, long from, long to, int type) {
		Collection<String> args = new ArrayList<String>();
		args.add(String.valueOf(carId));

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

	public static double getDoubleByName(Cursor c, String name) {
		return c.getDouble(c.getColumnIndex(name));
	}

	public static int getIntByName(Cursor c, String name) {
		return c.getInt(c.getColumnIndex(name));
	}

	public static boolean isCursorHasValue(Cursor c) {
		return c != null && c.moveToFirst();
	}
}

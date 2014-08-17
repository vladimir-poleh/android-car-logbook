package com.enadein.carlogbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.enadein.carlogbook.R;

public class DataBaseDefaulter {

	public void initDataBase(SQLiteDatabase db, Context ctx) {
		String[] fuelTypeList = ctx.getResources()
				.getStringArray(R.array.fuel_type_default);

		String[] stationTypeList = ctx.getResources()
				.getStringArray(R.array.station_type_default);

		db.beginTransaction();
		try {
			insertDataValue(db, ProviderDescriptor.DataValue.Type.FUEL, fuelTypeList[0], 1);
			for (int i = 1; i < fuelTypeList.length; i++) {
				insertDataValue(db, ProviderDescriptor.DataValue.Type.FUEL, fuelTypeList[i], 0);
			}

			insertDataValue(db, ProviderDescriptor.DataValue.Type.STATION, stationTypeList[0], 1);
			for (int i = 1; i < stationTypeList.length; i++) {
				insertDataValue(db, ProviderDescriptor.DataValue.Type.STATION, stationTypeList[i], 0);
			}

//			addTestDefaultCar(db);

			db.setTransactionSuccessful();
		}finally {
			db.endTransaction();
		}
	}

	protected void insertDataValue(SQLiteDatabase db, int type, String value, int defaultFlag) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.NAME, value);
		cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
		cv.put(ProviderDescriptor.DataValue.Cols.SYSTEM, 1);
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, defaultFlag);
		db.insert(ProviderDescriptor.DataValue.TABLE_NAME, null, cv);
	}

	private void addTestDefaultCar(SQLiteDatabase db) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, "TEST CAR");
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		db.insert(ProviderDescriptor.Car.TABLE_NAME, null, cv);
	}
}

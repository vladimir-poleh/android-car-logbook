package com.carlogbook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.carlogbook.R;

public class DataBaseDefaulter {

	public void initDataBase(SQLiteDatabase db, Context ctx) {
		String[] fuelTypeList = ctx.getResources()
				.getStringArray(R.array.fuel_type_default);

		String[] stationTypeList = ctx.getResources()
				.getStringArray(R.array.station_type_default);

		db.beginTransaction();
		try {
			insert(db, ProviderDescriptor.DataValue.Type.FUEL, fuelTypeList[0], 1);
			for (int i = 1; i < fuelTypeList.length; i++) {
				insert(db, ProviderDescriptor.DataValue.Type.FUEL, fuelTypeList[i], 0);
			}

			insert(db, ProviderDescriptor.DataValue.Type.STATION, stationTypeList[0], 1);
			for (int i = 1; i < stationTypeList.length; i++) {
				insert(db, ProviderDescriptor.DataValue.Type.STATION, stationTypeList[i], 0);
			}


			db.setTransactionSuccessful();
		}finally {
			db.endTransaction();
		}
	}

	protected void insert(SQLiteDatabase db, int type, String value, int defaultFlag) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.DataValue.Cols.NAME, value);
		cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);
		cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, defaultFlag);
		db.insert(ProviderDescriptor.DataValue.TABLE_NAME, null, cv);
	}
}

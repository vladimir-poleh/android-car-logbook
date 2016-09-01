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
package com.enadein.carlogbook.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class CarLogbookProvider extends ContentProvider {
	public static final String LIMIT_PARAM = "limit_param";
	public static final String UNKNOWN_URI = "Unknown URI ";
	protected DBOpenHelper dbHelper;

	protected HashMap<Integer, String> tables = new HashMap<Integer, String>();
	protected HashMap<Integer, String> types = new HashMap<Integer, String>();


	@Override
	public boolean onCreate() {
		dbHelper = new DBOpenHelper(getContext());

		tables.put(ProviderDescriptor.Car.PATH_TOKEN, ProviderDescriptor.Car.TABLE_NAME);
		tables.put(ProviderDescriptor.DataValue.PATH_TOKEN, ProviderDescriptor.DataValue.TABLE_NAME);
		tables.put(ProviderDescriptor.Log.PATH_TOKEN, ProviderDescriptor.Log.TABLE_NAME);
		tables.put(ProviderDescriptor.Notify.PATH_TOKEN, ProviderDescriptor.Notify.TABLE_NAME);
		tables.put(ProviderDescriptor.LogView.PATH_TOKEN, ProviderDescriptor.LogView.TABLE_NAME);
		tables.put(ProviderDescriptor.FuelRate.PATH_TOKEN, ProviderDescriptor.FuelRate.TABLE_NAME);
		tables.put(ProviderDescriptor.FuelRateView.PATH_TOKEN, ProviderDescriptor.FuelRateView.TABLE_NAME);
		tables.put(ProviderDescriptor.Sett.PATH_TOKEN, ProviderDescriptor.Sett.TABLE_NAME);

		types.put(ProviderDescriptor.Car.PATH_TOKEN, ProviderDescriptor.Car.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.Car.PATH_ID_TOKEN, ProviderDescriptor.Car.CONTENT_TYPE_ITEM);

		types.put(ProviderDescriptor.DataValue.PATH_TOKEN, ProviderDescriptor.DataValue.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.DataValue.PATH_ID_TOKEN, ProviderDescriptor.DataValue.CONTENT_TYPE_ITEM);

		types.put(ProviderDescriptor.Log.PATH_TOKEN, ProviderDescriptor.Log.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.Log.PATH_ID_TOKEN, ProviderDescriptor.Log.CONTENT_TYPE_ITEM);

		types.put(ProviderDescriptor.Notify.PATH_TOKEN, ProviderDescriptor.Notify.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.Notify.PATH_ID_TOKEN, ProviderDescriptor.Notify.CONTENT_TYPE_ITEM);

		types.put(ProviderDescriptor.FuelRate.PATH_TOKEN, ProviderDescriptor.FuelRate.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.FuelRate.PATH_ID_TOKEN, ProviderDescriptor.FuelRate.CONTENT_TYPE_ITEM);

		types.put(ProviderDescriptor.FuelRateView.PATH_TOKEN, ProviderDescriptor.FuelRateView.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.FuelRateView.PATH_ID_TOKEN, ProviderDescriptor.FuelRateView.CONTENT_TYPE_ITEM);



		types.put(ProviderDescriptor.Sett.PATH_TOKEN, ProviderDescriptor.Sett.CONTENT_TYPE_DIR);
		types.put(ProviderDescriptor.Sett.PATH_ID_TOKEN, ProviderDescriptor.Sett.CONTENT_TYPE_ITEM);

		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		Cursor result;

		String tableName = tables.get(token);
		boolean isPathId = false;
		if (tableName == null) {
			token++;
			isPathId = true;
			tableName = tables.get(token);
		}

		if (tableName == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(tableName);
		if (isPathId) {
			String id = uri.getLastPathSegment();
			result = builder.query(db, null, "_id = ?", new String[]{id}, null, null, null);
		} else {
			String limit = uri.getQueryParameter(LIMIT_PARAM);
			result = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
		}

		if (result != null) {
			result.setNotificationUri(getContext().getContentResolver(), uri);
			if (token == ProviderDescriptor.LogView.PATH_TOKEN) {
				result.setNotificationUri(getContext().getContentResolver(), ProviderDescriptor.Log.CONTENT_URI);
			}
		}
		return result;
	}

	@Override
	public String getType(Uri uri) {
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		String result = types.get(token);
		if (result == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		return result;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		Uri result;

		String tableName = tables.get(token);
		if (tableName == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		long id = db.insert(tableName, null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		result = uri.buildUpon().appendPath(String.valueOf(id)).build();
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		int result;

		String tableName = tables.get(token);
		if (tableName == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		result = db.delete(tableName, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		int result;

		String tableName = tables.get(token);
		if (tableName == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		result = db.update(tableName, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int token = ProviderDescriptor.URI_MATCHER.match(uri);

		String tableName = tables.get(token);
		if (tableName == null) {
			throw new IllegalArgumentException(UNKNOWN_URI + uri);
		}

		db.beginTransaction();
		try {
			for (ContentValues cv : values) {
				db.insert(tableName, null, cv);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return values.length;
	}

	public class DBOpenHelper extends SQLiteOpenHelper {
		private static final int CURRENT_DB_VERSION = 7; //Production 6
		private static final String DB_NAME = "com_carlogbook_v2.db";

//		private static final int CURRENT_DB_VERSION = 7; //test
//		private static final String DB_NAME = "com_carlogbook_test1a4.db";

		private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {0} ({1})";
		private static final String DROP_TABLE = "DROP TABLE IF EXISTS {0}";

		public DBOpenHelper(Context context) {
			super(context, DB_NAME, null, CURRENT_DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db, ProviderDescriptor.Car.TABLE_NAME,
					ProviderDescriptor.Car.CREATE_FIELDS);

			createTable(db, ProviderDescriptor.DataValue.TABLE_NAME,
					ProviderDescriptor.DataValue.CREATE_FIELDS);

			createTable(db, ProviderDescriptor.Log.TABLE_NAME,
					ProviderDescriptor.Log.CREATE_FIELDS);

			createTable(db, ProviderDescriptor.Notify.TABLE_NAME,
					ProviderDescriptor.Notify.CREATE_FIELDS);

			createTable(db, ProviderDescriptor.FuelRate.TABLE_NAME,
					ProviderDescriptor.FuelRate.CREATE_FIELDS);

			db.execSQL(ProviderDescriptor.LogView.CREATE_QUERY);
			db.execSQL(ProviderDescriptor.FuelRateView.CREATE_QUERY);

//			DataBaseDefaulter defaulter = new DataBaseDefaulter();
//			defaulter.initDataBase(db, getContext());
//			//updates
			upgradeFrom1to2(db);
			upgradeFrom2to3(db);
            upgradeFrom3to4(db);
            upgradeFrom4to5(db);
			upgradeFrom5to6(db);
			upgradeFrom6to7(db);
		}

		public void reset() {
			dropAllTables(getWritableDatabase());
			onCreate(getWritableDatabase());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			for (int i = oldVersion; i < newVersion; i++) {
				switch (i) {
					case 1: {
						upgradeFrom1to2(db);
						break;
					}
					case 2: {
						upgradeFrom2to3(db);
						break;
					}
                    case 3: {
                        upgradeFrom3to4(db);
                        break;
                    }
                    case 4: {
                        upgradeFrom4to5(db);
                        break;
                    }
					case 5: {
						upgradeFrom5to6(db);
						break;
					}
					case 6: {
						upgradeFrom6to7(db);
					}
				}
			}
		}

		private void upgradeFrom1to2(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE car ADD UUID TEXT");
			db.execSQL("ALTER TABLE notify ADD CREATE_DATE INTEGER");

			SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
			builder.setTables(ProviderDescriptor.Car.TABLE_NAME);

			Cursor c = builder.query(db, null, null, null, null, null, null);
			if (c != null) {
				while (c.moveToNext()) {
					ContentValues cv = new ContentValues();
					String uuid = UUID.randomUUID().toString();
					cv.put(ProviderDescriptor.Car.Cols.UUID, uuid);
					long id = c.getLong(0);
					int res = db.update(ProviderDescriptor.Car.TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
//				Log.d("DB UPDATE", ""+res );
				}
				c.close();
			}

			//notification
			builder = new SQLiteQueryBuilder();
			builder.setTables(ProviderDescriptor.Notify.TABLE_NAME);

			 c = builder.query(db, null, null, null, null, null, null);
			if (c != null) {
				while (c.moveToNext()) {
					ContentValues cv = new ContentValues();
					cv.put(ProviderDescriptor.Notify.Cols.CREATE_DATE, new Date().getTime());

					long id = c.getLong(0);
					int res = db.update(ProviderDescriptor.Notify.TABLE_NAME, cv, "_id = ?", new String[]{String.valueOf(id)});
//				Log.d("DB UPDATE", ""+res );
				}

				c.close();
			}
		}

		private void upgradeFrom2to3(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE car ADD UNIT_FUEL INTEGER");
			db.execSQL("ALTER TABLE car ADD UNIT_DIST INTEGER");
			db.execSQL("ALTER TABLE car ADD UNIT_CONSUM INTEGER");
			db.execSQL("ALTER TABLE car ADD UNIT_CURRENCY TEXT");
			//UnitFacade unitFacade = new UnitFacade(getContext());

			//ContentValues cv = new ContentValues();
			//cv.put(ProviderDescriptor.Car.Cols.UNIT_FUEL, unitFacade.getFuelValue());
			//cv.put(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION, unitFacade.getConsumptionValue());
			//cv.put(ProviderDescriptor.Car.Cols.UNIT_DISTANCE, unitFacade.getDistanceValue());
			//cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY, unitFacade.getCurrency());

		//	db.update(ProviderDescriptor.Car.TABLE_NAME, cv, null, null);
			//TODO Check

			createTable(db, ProviderDescriptor.Sett.TABLE_NAME, ProviderDescriptor.Sett.CREATE_FIELDS);
			ContentValues setCv = new ContentValues();
//			setCv.put(UnitFacade.SET_DATE_FORMAT, "0");
		//	setCv.put(ProviderDescriptor.Sett.Cols.KEY, UnitFacade.SET_DATE_FORMAT);
			setCv.put(ProviderDescriptor.Sett.Cols.VALUE, "0");
			db.insert(ProviderDescriptor.Sett.TABLE_NAME, null, setCv);

//			db.delete(ProviderDescriptor.FuelRate.TABLE_NAME, "id != -1", null);
		}

        private void upgradeFrom3to4(SQLiteDatabase db) {
            db.execSQL("ALTER TABLE rate ADD SUM_FUEL REAL");
            db.execSQL("ALTER TABLE rate ADD SUM_DIST REAL");
            db.execSQL("ALTER TABLE rate ADD AVG REAL");
        }

        private void upgradeFrom4to5(SQLiteDatabase db) {
            long ohterTypeId = 1L;

//	        DBUtils.createDataValue(db,
//			        getContext().getString(R.string.others)
//			        ,ProviderDescriptor.DataValue.Type.OTHERS );

            db.execSQL("ALTER TABLE log ADD OTHER_TYPE_ID INTEGER");

            ContentValues cv = new ContentValues();
            cv.put(ProviderDescriptor.Log.Cols.OTHER_TYPE_ID, ohterTypeId);
            db.update(ProviderDescriptor.Log.TABLE_NAME, cv, ProviderDescriptor.Log.Cols.TYPE_LOG + " = " + ProviderDescriptor.Log.Type.OTHER, null);


            db.execSQL("DROP VIEW IF EXISTS log_view");
            db.execSQL(ProviderDescriptor.LogView.CREATE_QUERY_V2);

            db.execSQL("ALTER TABLE car ADD MAKE TEXT");
            db.execSQL("ALTER TABLE car ADD MODEL TEXT");
            db.execSQL("ALTER TABLE car ADD MANUF TEXT");
            db.execSQL("ALTER TABLE car ADD CAR_COST TEXT");
            db.execSQL("ALTER TABLE car ADD PURCHASE INTEGER");
            db.execSQL("ALTER TABLE car ADD OPEN_MIL TEXT");
            db.execSQL("ALTER TABLE car ADD ID_NO TEXT");
            db.execSQL("ALTER TABLE car ADD REG_NUM TEXT");
            db.execSQL("ALTER TABLE car ADD FUEL_TYPE TEXT");
            db.execSQL("ALTER TABLE car ADD TYRE TEXT");
        }

		private void upgradeFrom5to6(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE notify ADD REPEAT INTEGER DEFAULT 0");
		}

		private void upgradeFrom6to7(SQLiteDatabase db) {
			db.execSQL("ALTER TABLE notify ADD REPEAT2 INTEGER DEFAULT 0");
			db.execSQL("ALTER TABLE notify ADD VALUE2 INTEGER DEFAULT 0");
			db.execSQL("ALTER TABLE notify ADD COMMENTS TEXT");
			//income
			db.execSQL("ALTER TABLE log ADD INCOME REAL");
			db.execSQL("DROP VIEW IF EXISTS log_view");
			db.execSQL(ProviderDescriptor.LogView.CREATE_VIEW_FUEL_LOG);
			db.execSQL(ProviderDescriptor.LogView.CREATE_VIEW_OTHER_LOG);
			db.execSQL(ProviderDescriptor.LogView.CREATE_VIEW_OTHER2_LOG);
			db.execSQL(ProviderDescriptor.LogView.CREATE_VIEW_OTHER3_LOG);
			db.execSQL(ProviderDescriptor.LogView.CREATE_QUERY_V3);

			//DEFAULT
			//TODO temp solution, refactor it
			ContentValues cv = new ContentValues();
//			cv.put(ProviderDescriptor.DataValue.Cols.NAME, getContext().getString(R.string.total_year_last_income_def));
			cv.put(ProviderDescriptor.DataValue.Cols.TYPE, ProviderDescriptor.DataValue.Type.INCOME);
			cv.put(ProviderDescriptor.DataValue.Cols.SYSTEM, 1);
			cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG, 1);
			db.insert(ProviderDescriptor.DataValue.TABLE_NAME, null, cv);

		}

		private void dropAllTables(SQLiteDatabase db) {
			db.execSQL("DROP VIEW IF EXISTS log_view");
			db.execSQL("DROP VIEW IF EXISTS rate_view");
			dropTable(db, ProviderDescriptor.Car.TABLE_NAME);
			dropTable(db, ProviderDescriptor.Log.TABLE_NAME);
			dropTable(db, ProviderDescriptor.DataValue.TABLE_NAME);
			dropTable(db, ProviderDescriptor.Notify.TABLE_NAME);
			dropTable(db, ProviderDescriptor.FuelRate.TABLE_NAME);
		}

		public void dropTable(SQLiteDatabase db, String name) {
			String query = MessageFormat.format(DBOpenHelper.DROP_TABLE, name);
			db.execSQL(query);
		}

		public void createTable(SQLiteDatabase db, String name, String fields) {
			String query = MessageFormat.format(DBOpenHelper.CREATE_TABLE, name, fields);
			db.execSQL(query);
		}

	}
}

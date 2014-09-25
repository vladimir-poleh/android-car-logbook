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

import android.content.UriMatcher;
import android.net.Uri;

public class ProviderDescriptor {
	public static final String AUTHORITY = "com.enadein.carlogbook";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(AUTHORITY, Car.PATH, Car.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Car.PATH_ID, Car.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, DataValue.PATH, DataValue.PATH_TOKEN);
		matcher.addURI(AUTHORITY, DataValue.PATH_ID, DataValue.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, Log.PATH, Log.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Log.PATH_ID, Log.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, Notify.PATH, Notify.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Notify.PATH_ID, Notify.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, LogView.PATH, LogView.PATH_TOKEN);
		matcher.addURI(AUTHORITY, LogView.PATH_ID, LogView.PATH_ID_TOKEN);


		matcher.addURI(AUTHORITY, FuelRate.PATH, FuelRate.PATH_TOKEN);
		matcher.addURI(AUTHORITY, FuelRate.PATH_ID, FuelRate.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, FuelRateView.PATH, FuelRateView.PATH_TOKEN);
		matcher.addURI(AUTHORITY, FuelRateView.PATH_ID, FuelRateView.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, Sett.PATH, Sett.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Sett.PATH_ID, Sett.PATH_ID_TOKEN);


		return matcher;
	}

	public static class Car {
		public static final String TABLE_NAME = "car";
		public static final String PATH = "car";
		public static final int PATH_TOKEN = 100;
		public static final String PATH_ID = "car/*";
		public static final int PATH_ID_TOKEN = 101;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, ACTIVE_FLAG INTEGER";

		public static class Cols {
			public static final String _ID = "_id";
			public static final String NAME = "NAME";
			public static final String ACTIVE_FLAG = "ACTIVE_FLAG";
			//1.1
			public static final String UUID = "UUID";
			//1.2
			public static final String UNIT_FUEL = "UNIT_FUEL";
			public static final String UNIT_DISTANCE = "UNIT_DIST";
			public static final String UNIT_CONSUMPTION = "UNIT_CONSUM";
			public static final String UNIT_CURRENCY = "UNIT_CURRENCY";
		}
	}

	public static class Log {
		public static final String TABLE_NAME = "log";
		public static final String PATH = "log";
		public static final int PATH_TOKEN = 200;
		public static final String PATH_ID = "log/*";
		public static final int PATH_ID_TOKEN = 201;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT,DATE INTEGER," +
				" ODOMETER INTEGER, PRICE REAL, COMMENT TEXT, CAR_ID INTEGER, FUEL_TYPE_ID INTEGER, " +
				"FUEL_STATION_ID INTEGER, FUEL_VOLUME REAL, TYPE_ID INTEGER, NAME TEXT, PLACE TEXT, TYPE_LOG INTEGER";

		public static class Type {
			public static final int FUEL = 0;
			public static final int OTHER = 1;
		}

		public static class Cols {
			public static final String _ID = "_id";
			public static final String DATE = "DATE";
			public static final String ODOMETER = "ODOMETER";
			public static final String PRICE = "PRICE";
			public static final String CMMMENT = "COMMENT";
			public static final String CAR_ID = "CAR_ID";
			public static final String TYPE_LOG = "TYPE_LOG";

			//FUEL LOG
			public static final String FUEL_TYPE_ID = "FUEL_TYPE_ID";
			public static final String FUEL_STATION_ID = "FUEL_STATION_ID";
			public static final String FUEL_VOLUME = "FUEL_VOLUME";

			//OTHER LOG
			public static final String TYPE_ID = "TYPE_ID";
			public static final String NAME = "NAME";
			public static final String PLACE = "PLACE";
		}
	}

	public static class LogView {
		public static final String TABLE_NAME = "log_view";
		public static final String PATH = "log_view";
		public static final int PATH_TOKEN = 500;
		public static final String PATH_ID = "log_view/*";
		public static final int PATH_ID_TOKEN = 501;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static class Cols extends Log.Cols {
			public static final String STATION_NAME = "STATION_NAME";
			public static final String FUEL_NAME = "FUEL_NAME";
			public static final String TOTAL_PRICE = "TOTAL_PRICE";
		}

		public static final String CREATE_QUERY = "CREATE VIEW IF NOT EXISTS log_view as select l.*, d.name as STATION_NAME, df.name as  FUEL_NAME, l.PRICE * l.FUEL_VOLUME  as TOTAL_PRICE  from log l inner join data_value d on l.FUEL_STATION_ID = d._id inner join data_value df on l.FUEL_TYPE_ID = df._id union select l.*, '' as STATION_NAME, '' as  FUEL_NAME, l.PRICE as TOTAL_PRICE from log l where type_log = 1";
	}

	public static class DataValue {
		public static final String TABLE_NAME = "data_value";
		public static final String PATH = "data_value";
		public static final int PATH_TOKEN = 300;
		public static final String PATH_ID = "data_value/*";
		public static final int PATH_ID_TOKEN = 301;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TYPE INTEGER, SYS INTEGER, DEFAULT_FLAG INTEGER";

		public static class Cols {
			public static final String _ID = "_id";
			public static final String NAME = "NAME";
			public static final String TYPE = "TYPE";
			public static final String SYSTEM = "SYS";
			public static final String DEFAULT_FLAG = "DEFAULT_FLAG";
		}

		public static class Type {
			public static final int FUEL = 0;
			public static final int STATION = 1;
		}
	}

	public static class Notify {
		public static final String TABLE_NAME = "notify";
		public static final String PATH = "notify";
		public static final int PATH_TOKEN = 400;
		public static final String PATH_ID = "notify/*";
		public static final int PATH_ID_TOKEN = 401;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TYPE INTEGER, VALUE INTEGER, CAR_ID INTEGER";

		public static class Cols {
			public static final String _ID = "_id";
			public static final String NAME = "NAME";
			public static final String TYPE = "TYPE";
			public static final String TRIGGER_VALUE = "VALUE";
			public static final String CAR_ID = "CAR_ID";
			public static final String CREATE_DATE = "CREATE_DATE";
		}

		public static class Type {
			public static final int ODOMETER = 0;
			public static final int DATE = 1;
		}

	}

	public static class FuelRate {
		public static final String TABLE_NAME = "rate";
		public static final String PATH = "rate";
		public static final int PATH_TOKEN = 700;
		public static final String PATH_ID = "rate/*";
		public static final int PATH_ID_TOKEN = 701;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT,  RATE REAL, MIN_RATE REAL, MAX_RATE REAL, STATION_ID INTEGER, FUEL_TYPE_ID INTEGER, CAR_ID INTEGER";

		public static class Cols {
			public static final String _ID = "_id";
			public static final String CAR_ID = "CAR_ID";
			public static final String RATE = "RATE";
			public static final String MAX_RATE = "MAX_RATE";
			public static final String MIN_RATE = "MIN_RATE";
			public static final String FUEL_TYPE_ID = "FUEL_TYPE_ID";
			public static final String STATION_ID = "STATION_ID";
			public static final String SUM_FUEL = "SUM_FUEL";
			public static final String SUM_DIST = "SUM_DIST";
			public static final String AVG = "AVG";
		}
	}


	public static class FuelRateView {
		public static final String TABLE_NAME = "rate_view";
		public static final String PATH = "rate_view";
		public static final int PATH_TOKEN = 710;
		public static final String PATH_ID = "rate_view/*";
		public static final int PATH_ID_TOKEN = 711;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_QUERY = "CREATE VIEW IF NOT EXISTS rate_view as select l.*, d.name as STATION_NAME, df.name as  FUEL_NAME from rate l inner join data_value d on l.STATION_ID = d._id inner join data_value df on l.FUEL_TYPE_ID = df._id";

		public static class Cols extends FuelRate.Cols {
			public static final String STATION_NAME = "STATION_NAME";
			public static final String FUEL_NAME = "FUEL_NAME";
		}
	}

	public static class Sett {
		public static final String TABLE_NAME = "sett";
		public static final String PATH = "sett";
		public static final int PATH_TOKEN = 800;
		public static final String PATH_ID = "sett/*";
		public static final int PATH_ID_TOKEN = 801;
		public static final Uri CONTENT_URI = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS = "KEY TEXT,VALUE TEXT";

		public static class Cols {
			public static final String KEY = "KEY";
			public static final String VALUE = "VALUE";
		}
	}
}

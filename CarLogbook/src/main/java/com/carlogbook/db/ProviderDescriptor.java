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

import android.content.UriMatcher;
import android.net.Uri;

public class ProviderDescriptor {
	public static final String AUTHORITY = "com.carlogbook";
	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(AUTHORITY, Car.PATH, Car.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Car.PATH_ID, Car.PATH_ID_TOKEN);

		matcher.addURI(AUTHORITY, DataValue.PATH, DataValue.PATH_TOKEN);
		matcher.addURI(AUTHORITY, DataValue.PATH_ID, DataValue.PATH_ID_TOKEN);

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

			public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT,DATE INTEGER";

			public static class Cols {
				public static final String _ID = "_id";
				public static final String DATE = "DATE";
				public static final String ODOMETER = "ODOMETER";
				public static final String PRICE = "PRICE";
				public static final String CMMMENT = "COMMENT";
				public static final String CAR_ID = "CAR_ID";

				//FUEL LOG
				public static final String FUEL_TYPE_ID = "FUEL_TYPE_ID";
				public static final String FUEL_STATION_ID = "FUEL_TYPE_ID";
				public static final String FUEL_VOLUME = "FUEL_VOLUME";

				//OTHER LOG
				public static final String TYPE_ID = "TYPE_ID";
				public static final String PLACE = "PLACE";
			}
		}
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

		public static final String CREATE_FIELDS = "_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TYPE INTEGER, DEFAULT_FLAG INTEGER";

		public static class Cols {
			public static final String _ID = "_id";
			public static final String NAME = "NAME";
			public static final String TYPE = "TYPE";
			public static final String DEFAULT_FLAG = "DEFAULT_FLAG";
		}

		public static class Type {
			public static final int FUEL = 0;
			public static final int STATION = 1;
		}
	}
}

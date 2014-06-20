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
	public static final String AUTHORITY  = "com.carlogbook";
	public static final Uri BASE_URI  = Uri.parse("content://" + AUTHORITY);
	public static final UriMatcher URI_MATCHER  = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(AUTHORITY, Car.PATH, Car.PATH_TOKEN);
		matcher.addURI(AUTHORITY, Car.PATH_ID, Car.PATH_ID_TOKEN);

		return matcher;
	}

	public static class Car {
		public static final String TABLE_NAME  = "car";
		public static final String PATH  = "car";
		public static final int PATH_TOKEN  = 100;
		public static final String PATH_ID  = "car/*";
		public static final int PATH_ID_TOKEN  = 101;
		public static final Uri CONTENT_URI  = ProviderDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

		public static final String CONTENT_TYPE_DIR  = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
		public static final String CONTENT_TYPE_ITEM  = "vnd.android.cursor.item/vnd."  + AUTHORITY + "." + PATH;

		public static final String CREATE_FIELDS  = "_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT";

		public static class Cols {
			public static final String _ID  = "_id";
			public static final String NAME  = "NAME";
		}
	}
}

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
import android.database.Cursor;
import android.net.Uri;

public class DBUtils {
	public static int getActiveCarId(ContentResolver cr) {
		int result = -1;

		Cursor c = cr.query(ProviderDescriptor.Car.CONTENT_URI, null, "active_flag = 1", null, null);

		if (c != null && c.moveToFirst()) {
			int idIdx = c.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
			result = c.getInt(idIdx);
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
}

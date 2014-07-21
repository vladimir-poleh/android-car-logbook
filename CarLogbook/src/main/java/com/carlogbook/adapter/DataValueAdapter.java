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
package com.carlogbook.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carlogbook.R;
import com.carlogbook.db.ProviderDescriptor;

public class DataValueAdapter extends CursorAdapter {
	public DataValueAdapter(Context context, Cursor c) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		DataValueHolder holder = new DataValueHolder();
		LayoutInflater inflater = LayoutInflater.from(context);
		View listItem = inflater.inflate(R.layout.data_value_item, null);

		holder.nameView = (TextView) listItem.findViewById(R.id.name);
		listItem.setTag(holder);

		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		DataValueHolder setsHolder = (DataValueHolder) view.getTag();

		int idIdx = cursor.getColumnIndex(ProviderDescriptor.DataValue.Cols._ID);
		int nameIdx = cursor.getColumnIndex(ProviderDescriptor.DataValue.Cols.NAME);

		String name = cursor.getString(nameIdx);
		int id = cursor.getInt(idIdx);

		setsHolder.nameView.setText(name);
		setsHolder.id = id;
	}

	public static class DataValueHolder {
		public int id;
		public TextView nameView;
	}
}

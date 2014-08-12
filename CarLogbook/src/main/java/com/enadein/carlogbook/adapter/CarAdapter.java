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
package com.enadein.carlogbook.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class CarAdapter  extends CursorAdapter {

	public CarAdapter(Context context, Cursor c) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		CarHolder holder = new CarHolder();
		LayoutInflater inflater = LayoutInflater.from(context);
		View listItem = inflater.inflate(R.layout.car_item, null);

		holder.nameView = (TextView) listItem.findViewById(R.id.carItem);
		holder.activeView = (ImageView) listItem.findViewById(R.id.activeItem);
		listItem.setTag(holder);

		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CarHolder setsHolder = (CarHolder) view.getTag();

		int idIdx = cursor.getColumnIndex(ProviderDescriptor.Car.Cols._ID);
		int nameIdx = cursor.getColumnIndex(ProviderDescriptor.Car.Cols.NAME);
		int activeFlagIdx = cursor.getColumnIndex(ProviderDescriptor.Car.Cols.ACTIVE_FLAG);

		String name = cursor.getString(nameIdx);
		int id = cursor.getInt(idIdx);
		boolean active = cursor.getInt(activeFlagIdx) > 0;

		setsHolder.activeView.setVisibility(active ? View.VISIBLE : View.INVISIBLE);

		setsHolder.nameView.setText(name);
		setsHolder.id = id;
	}

	public static class CarHolder {
		public int id;
		public TextView nameView;
		public ImageView activeView;
	}
}

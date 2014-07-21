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
import android.widget.ImageView;
import android.widget.TextView;

import com.carlogbook.R;
import com.carlogbook.db.CommonUtils;
import com.carlogbook.db.ProviderDescriptor;

import java.util.Date;

public class LogAdapter extends CursorAdapter {

	public LogAdapter(Context context, Cursor c) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		LogFuelHolder holder = new LogFuelHolder();
		LayoutInflater inflater = LayoutInflater.from(context);
		View listItem = inflater.inflate(R.layout.fuel_log_item, null);

		holder.odometerView = (TextView) listItem.findViewById(R.id.odometer);
		holder.dateView  = (TextView) listItem.findViewById(R.id.date);
		holder.fuelView  = (TextView) listItem.findViewById(R.id.fuel);
		holder.priceTotal  = (TextView) listItem.findViewById(R.id.priceTotal);
		holder.imgType = (ImageView) listItem.findViewById(R.id.imgType);

		listItem.setTag(holder);

		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		LogFuelHolder logFuelHolder = (LogFuelHolder) view.getTag();

		int idIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols._ID);
		int odometerIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.ODOMETER);
		int priceIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.PRICE);
		int dateIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.DATE);
		int fueldValueIdx = cursor.getColumnIndex(ProviderDescriptor.Log.Cols.FUEL_VOLUME);

		String odometer = cursor.getString(odometerIdx);
		int id = cursor.getInt(idIdx);

		String fuelValue = cursor.getString(fueldValueIdx);
		String date = CommonUtils.formatDate(new Date(cursor.getLong(dateIdx)));
		String price = cursor.getString(priceIdx);

		logFuelHolder.odometerView.setText(odometer);
		logFuelHolder.dateView.setText(date);
		logFuelHolder.fuelView.setText(fuelValue);
		logFuelHolder.priceTotal.setText(price);
		logFuelHolder.imgType.setBackgroundResource(R.drawable.abc_ic_voice_search);
		logFuelHolder.id = id;
	}

	public static class LogFuelHolder {
		public int id;
		public ImageView imgType;
		public TextView odometerView;
		public TextView fuelView;
		public TextView dateView;
		public TextView priceTotal;
	}
}

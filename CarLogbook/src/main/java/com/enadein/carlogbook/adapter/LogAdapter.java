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
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.core.FuelRateLoader;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Date;

public class LogAdapter extends CursorAdapter {
	private String[] types;
	private UnitFacade unitFacade;
	private int mlastPos = 1;
	private FuelRateLoader rateLoader;

	public LogAdapter(Context context, Cursor c, UnitFacade unitFacade) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
		types = context.getResources().getStringArray(R.array.log_type);
		this.unitFacade = unitFacade;
		rateLoader = new FuelRateLoader(unitFacade, context);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	private int getItemViewType(Cursor cursor) {
		return cursor.getInt(cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_LOG));
	}

	@Override
	public int getItemViewType(int position) {
		Cursor cursor = (Cursor) getItem(position);
		return getItemViewType(cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View listItem;

		int type = cursor.getInt(cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_LOG));


		if (type == ProviderDescriptor.Log.Type.FUEL) {
			listItem = inflater.inflate(R.layout.fuel_log_item, null);


			LogFuelHolder holder = new LogFuelHolder();
			holder.odometerView = (TextView) listItem.findViewById(R.id.odometer);
			holder.dateView = (TextView) listItem.findViewById(R.id.date);
			holder.fuelView = (TextView) listItem.findViewById(R.id.fuel);
			holder.fuelValueView = (TextView) listItem.findViewById(R.id.fuelValue);
			holder.rateView = (TextView) listItem.findViewById(R.id.rate);
			holder.priceTotal = (TextView) listItem.findViewById(R.id.priceTotal);
			holder.imgType = (ImageView) listItem.findViewById(R.id.imgType);

			listItem.setTag(holder);
		} else {
			listItem = inflater.inflate(R.layout.log_item, null);
			LogHolder holder = new LogHolder();

			holder.odometerView = (TextView) listItem.findViewById(R.id.odometer);
			holder.dateView = (TextView) listItem.findViewById(R.id.date);
			holder.imgType = (ImageView) listItem.findViewById(R.id.imgType);
			holder.nameView = (TextView) listItem.findViewById(R.id.name);
			holder.typeView = (TextView) listItem.findViewById(R.id.type);
			holder.priceTotal = (TextView) listItem.findViewById(R.id.priceTotal);

			listItem.setTag(holder);
		}

		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		Resources res = context.getResources();

		int type = cursor.getInt(cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_LOG));

		int idIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols._ID);
		int odometerIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.ODOMETER);
		int priceIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.PRICE);
		int dateIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.DATE);
		int incomeIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.INCOME);

		double price = cursor.getDouble(priceIdx);
		String date = CommonUtils.formatDate(new Date(cursor.getLong(dateIdx)));

		int odometer = cursor.getInt(odometerIdx);
		int id = cursor.getInt(idIdx);

		if (type == ProviderDescriptor.Log.Type.FUEL) {
			LogFuelHolder logFuelHolder = (LogFuelHolder) view.getTag();
			int fuelValueIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_VOLUME);
			double fuelValue = cursor.getDouble(fuelValueIdx);

			double priceTotalDouble = fuelValue * price;

			int stationNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.STATION_NAME);
			String stationName = cursor.getString(stationNameIdx);

			int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
			String fuelName = cursor.getString(fuelNameIdx);


			logFuelHolder.odometerView.setText(String.valueOf(odometer));
			unitFacade.appendDistUnit(logFuelHolder.odometerView, false);
			logFuelHolder.dateView.setText(date);
			logFuelHolder.fuelValueView.setText(CommonUtils.formatFuel(fuelValue, unitFacade));
			unitFacade.appendFuelUnit(logFuelHolder.fuelValueView, false);
			logFuelHolder.priceTotal.setText( CommonUtils.formatPriceNew(priceTotalDouble, unitFacade));
			unitFacade.appendCurrency(logFuelHolder.priceTotal,false, false);
			logFuelHolder.fuelView.setText(fuelName + "(" + stationName + ")");
			logFuelHolder.imgType.setBackgroundResource(R.drawable.fuel);
			logFuelHolder.id = id;
			rateLoader.calculateFuelRateAndPath(logFuelHolder.rateView, id);
			logFuelHolder.priceTotal.setTextColor(res.getColor(R.color.price));
		} else {
			int nameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.NAME);
			int typeIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_ID);

			String name = cursor.getString(nameIdx);
			int typeId = cursor.getInt(typeIdx);

			LogHolder logHolder = (LogHolder) view.getTag();
			logHolder.dateView.setText(date);
			logHolder.odometerView.setText(String.valueOf(odometer));
			unitFacade.appendDistUnit(logHolder.odometerView, false);
			logHolder.imgType.setBackgroundResource(DataInfo.images.get(typeId));




			unitFacade.appendCurrency(logHolder.priceTotal,false, false);
			logHolder.nameView.setText( name);

			logHolder.priceTotal.setTextColor(res.getColor(R.color.price));

            if (typeId == 0) {
                int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
                String fuelName = cursor.getString(fuelNameIdx);
                logHolder.typeView.setText(fuelName);
            } else if (typeId == 12) {
				int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
				String fuelName = cursor.getString(fuelNameIdx);
				logHolder.typeView.setText(fuelName);
				logHolder.priceTotal.setTextColor(res.getColor(R.color.income));
				price = cursor.getDouble(incomeIdx);
			} else {
                logHolder.typeView.setText(types[typeId]);
//                int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
//                String fuelName = cursor.getString(fuelNameIdx);
//                logHolder.typeView.setText(fuelName);
            }

			logHolder.priceTotal.setText(CommonUtils.formatPriceNew(price, unitFacade));
			if (name == null || name.trim().equals("")) {
				logHolder.nameView.setText(logHolder.typeView.getText());
			}

			logHolder.id = id;
		}

		int pos = cursor.getPosition();
		CommonUtils.runAnimation(mlastPos, pos, view, UnitFacade.animSize);
		mlastPos = pos;
	}

	public static class LogFuelHolder {
		public int id;
		public ImageView imgType;
		public TextView odometerView;
		public TextView fuelView;
		public TextView fuelValueView;
		public TextView dateView;
		public TextView priceTotal;
		public TextView rateView;
	}

	public static class LogHolder {
		public int id;
		public ImageView imgType;
		public TextView nameView;
		public TextView typeView;
		public TextView odometerView;
		public TextView dateView;
		public TextView priceTotal;
	}
}

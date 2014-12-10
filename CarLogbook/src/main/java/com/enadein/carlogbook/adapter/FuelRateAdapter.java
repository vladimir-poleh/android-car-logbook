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
import android.media.Image;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.FuelRateBean;
import com.enadein.carlogbook.bean.FuelRateViewBean;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;


public class FuelRateAdapter extends CursorAdapter {
	private final UnitFacade unitFacade;
	private int mlastPos = 1;

	public FuelRateAdapter(Context context, Cursor c, UnitFacade unitFacade) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
		this.unitFacade = unitFacade;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		FuelRateHolder holder = new FuelRateHolder();
		LayoutInflater inflater = LayoutInflater.from(context);
		View listItem = inflater.inflate(R.layout.report_rate_item, null);

		holder.nameView = (TextView) listItem.findViewById(R.id.name);
		holder.valueView = (TextView) listItem.findViewById(R.id.value);
		holder.valueMinView = (TextView) listItem.findViewById(R.id.valueMin);
		holder.valueMaxView = (TextView) listItem.findViewById(R.id.valueMax);
		holder.valueAvgView = (TextView) listItem.findViewById(R.id.valueAVG);
		holder.logo = (ImageView) listItem.findViewById(R.id.logo);
		listItem.setTag(holder);

		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		FuelRateViewBean bean = new FuelRateViewBean();
		bean.populate(cursor);

		FuelRateHolder holder = (FuelRateHolder) view.getTag();
		holder.logo.setBackgroundResource(R.drawable.fuel);
		holder.nameView.setText(bean.getStation() + "(" + bean.getFuelType() + ")\n" );
		unitFacade.appendConsumUnit(holder.nameView , true);

		int consum = unitFacade.getConsumptionValue();
		String min = (consum == 2) ? CommonUtils.formatDistance(bean.getMinRate()) : CommonUtils.formatFuel(bean.getMinRate(), unitFacade) ;
		String cur = (consum == 2) ? CommonUtils.formatDistance(bean.getRate()) : CommonUtils.formatFuel(bean.getRate(), unitFacade);
		String max = (consum == 2) ? CommonUtils.formatDistance(bean.getMaxRate()) : CommonUtils.formatFuel(bean.getMaxRate(), unitFacade);
		String avg =(consum == 2) ? CommonUtils.formatDistance(bean.getAvg()) :  CommonUtils.formatFuel(bean.getAvg(), unitFacade);

		holder.valueView.setText(cur + unitFacade.getConsumPostfix()+ " (" + context.getString(R.string.last) + ")");
		holder.valueMinView.setText(min + unitFacade.getConsumPostfix() +" (" + context.getString(R.string.min)+ ")");
		holder.valueMaxView.setText(max+ unitFacade.getConsumPostfix() +" (" + context.getString(R.string.max)+ ")");
        holder.valueAvgView.setText(avg + unitFacade.getConsumPostfix() + " (" + context.getString(R.string.avg)+ ")");

		int pos = cursor.getPosition();
		CommonUtils.runAnimation(mlastPos, pos, view, UnitFacade.animSize);
		mlastPos = pos;
	}

	public static class FuelRateHolder {
		public int id;
		public TextView nameView;
		public TextView valueView;
		public TextView valueMinView;
		public TextView valueMaxView;
		public TextView valueAvgView;
		public ImageView  logo;
	}
}

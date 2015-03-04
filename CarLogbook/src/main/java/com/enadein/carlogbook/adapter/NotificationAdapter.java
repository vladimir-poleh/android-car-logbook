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
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Date;

public class NotificationAdapter extends CursorAdapter {
	private int mlastPos = 1;
	private UnitFacade unitFacade;

	public NotificationAdapter(Context context, Cursor c, UnitFacade unitFacade) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
		this.unitFacade = unitFacade;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View listItem = inflater.inflate(R.layout.notif_item, null);
		return listItem;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int nameIdx = cursor.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME);
		int trigerIdx = cursor.getColumnIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE);
		int triger2Idx = cursor.getColumnIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE2);
		int typeIdx = cursor.getColumnIndex(ProviderDescriptor.Notify.Cols.TYPE);

		String name = cursor.getString(nameIdx);
		long trigerValue = cursor.getLong(trigerIdx);
		long trigerValue2 = cursor.getLong(triger2Idx);
		int type = cursor.getInt(typeIdx);

		ImageView imageView = (ImageView) view.findViewById(R.id.logo);
		TextView trigerView = (TextView) view.findViewById(R.id.value);
		TextView nameView = (TextView) view.findViewById(R.id.name);

		nameView.setText(name);
		if (type == ProviderDescriptor.Notify.Type.DATE) {
			imageView.setImageResource(R.drawable.date);
			trigerView.setText(CommonUtils.formatDate(new Date(trigerValue)));
		} else if (type == ProviderDescriptor.Notify.Type.ODOMETER) {
			imageView.setImageResource(R.drawable.odometer);
			trigerView.setText(unitFacade.appendDistUnit(false,String.valueOf(trigerValue)));
		} else {
			imageView.setImageResource(R.drawable.notify);
			String value = CommonUtils.formatDate(new Date(trigerValue));
			value += "\n" + unitFacade.appendDistUnit(false,String.valueOf(trigerValue2));
			trigerView.setText(value);

		}

		int pos = cursor.getPosition();
		CommonUtils.runAnimation(mlastPos, pos, view, UnitFacade.animSize);
		mlastPos = pos;
	}

}

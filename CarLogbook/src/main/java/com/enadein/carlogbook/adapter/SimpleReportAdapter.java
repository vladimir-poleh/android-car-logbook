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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.ReportItem;
import com.enadein.carlogbook.db.CommonUtils;

import java.util.Date;

public class SimpleReportAdapter extends ArrayAdapter<ReportItem> {
	private int resource;

	public SimpleReportAdapter(Context context, int resource, ReportItem[] objects) {
		super(context, resource, objects);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());

			convertView = inflater.inflate(resource, parent, false);
		}
		ReportItem item = getItem(position);

		ImageView icon = (ImageView) convertView.findViewById(R.id.logo);

		icon.setImageResource(item.getResId());

		TextView name = (TextView) convertView.findViewById(R.id.name);
		TextView value = (TextView) convertView.findViewById(R.id.value);

		name.setText(item.getName());
		if (item.getValue() > 0.) {
			value.setText(CommonUtils.formatPrice(item.getValue()));
		} else if (item.getValue2() > 0) {
			value.setText(CommonUtils.formatDate(new Date(item.getValue2())));
		} else {
			value.setText("");
		}

		return convertView;
	}
}

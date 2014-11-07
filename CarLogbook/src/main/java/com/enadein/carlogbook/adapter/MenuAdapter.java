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


public class MenuAdapter extends ArrayAdapter<MenuItem> {

	public MenuAdapter(Context context, int resource, MenuItem[] objects) {
		super(context, resource, objects);
	}

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuItem item = getItem(position);

        if (item.type == MenuItem.TYPE_ITEM) {
            convertView = getViewItem(convertView, item);
        } else if (item.type == MenuItem.HEADER) {
            convertView = getHeaderItem(convertView, item);
        }
		return convertView;
	}

    private View getHeaderItem(View convertView, MenuItem item) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.menu_header, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        if (item.name != null) {
            textView.setText(item.name);
        }
       return convertView;
    }

    private View getViewItem(View convertView, MenuItem item) {
        MenuItemHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.menu_item, null);

            holder = new MenuItemHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (MenuItemHolder) convertView.getTag();
        }

        holder.text.setText(item.name);
        holder.icon.setBackgroundResource(item.logoResId);
        return convertView;
    }

    public static class MenuItemHolder {
		public ImageView icon;
		public TextView text;
	}

	public static class MenuDescriptor {
		public static final int LOG_POSITION = 1;
		public static final int REPORTS_POSITION = 2;
		public static final int NOTIFICATIONS_POSITION = 3;
		public static final int MY_CARS_POSITION = 4;
		public static final int CALC = 5;
		public static final int IMPORT_EXPORT = 6;
		public static final int SETTINGS_POSITION = 7;
		public static final int ABOUT_POSITION = 8;
	}
}

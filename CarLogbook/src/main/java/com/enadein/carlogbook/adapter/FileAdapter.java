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
import android.widget.TextView;

import com.enadein.carlogbook.R;

import java.io.File;

public class FileAdapter  extends ArrayAdapter<File> {
	public FileAdapter(Context context, int resource,  File[] files) {
		super(context, resource, files);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		File file = getItem(position);

		FileHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.data_value_item, null);

			holder = new FileHolder();
			holder.text = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (FileHolder) convertView.getTag();
		}
		String fileName = file.getName();
		int xmlIdx = fileName.indexOf(".");
		if (xmlIdx > 0) {
			fileName = fileName.substring(0, xmlIdx);
		}
		holder.text.setText(fileName);

		return convertView;
	}

	public static class FileHolder {
		public TextView text;
	}


}

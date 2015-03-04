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
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FileAdapter  extends ArrayAdapter<File> {
	private int mlastPos = 1;

	public FileAdapter(Context context, int resource,  File[] files) {
		super(context, resource, new ArrayList<File>(Arrays.asList(files)));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		File file = getItem(position);

		FileHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.import_item, null);

			holder = new FileHolder();


			holder.text = (TextView) convertView.findViewById(R.id.name) ;
			holder.date = (TextView) convertView.findViewById(R.id.date) ;
			convertView.setTag(holder);
		} else {
			holder = (FileHolder) convertView.getTag();
		}
		String fileName = file.getName();
		int xmlIdx = fileName.indexOf(".");
		if (xmlIdx > 0) {
			fileName = fileName.substring(0, xmlIdx);
		}

		long timeMod =  file.lastModified();
		String timeModString =  CommonUtils.formatDate(new Date(timeMod));
		timeModString +=  " " + CommonUtils.formatDate(new Date(timeMod), "hh:mm");


		holder.text.setText(fileName);
		holder.date.setText(timeModString);

		int pos = position;
		CommonUtils.runAnimation(mlastPos, pos, convertView, UnitFacade.animSize);
		mlastPos = pos;

		return convertView;
	}

	public static class FileHolder {
		public TextView text;
		public TextView date;
	}


}

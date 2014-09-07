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
package com.enadein.carlogbook.bean;


import android.content.Context;

import com.enadein.carlogbook.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DataInfo {
	public static final int[] service = new int[] {5,6,7};
	public static final int[] other = new int[] {0, 1, 3, 4, 9, 10};
	public static final int[] parking = new int[] {2};
	public static final int[] parts = new int[] {8};

	public static final int COLOR_FUEL = 0xFF50CDF5;
	public static final int COLOR_SERVICE = 0xFFF4A113;
	public static final int COLOR_PARKING = 0xFF22C1BE;
	public static final int COLOR_PARTS = 0xFFC1A622;
	public static final int COLOR_OTHERS = 0xFF8E8244;

	public static final Map<Integer, Integer> images;
	static {
		Map<Integer, Integer> map =  new HashMap<Integer, Integer>();
		map.put(0, R.drawable.log);
		map.put(1, R.drawable.water);
		map.put(2, R.drawable.park);
		map.put(3, R.drawable.penalty);
		map.put(4, R.drawable.upgrade);
		map.put(5, R.drawable.serv);
		map.put(6, R.drawable.oil);
		map.put(7, R.drawable.oil);
		map.put(8, R.drawable.part);
		map.put(9, R.drawable.insurance);
		map.put(10, R.drawable.coint);

		images = Collections.unmodifiableMap(map);
	}

	public static  String getDescriptionByTypes(Context context, int resId, int[] types) {
		StringBuilder sb = new StringBuilder();
		sb.append(context.getString(resId)).append('\n');
		sb.append("(");

		String[] logTypes = context.getResources().getStringArray(R.array.log_type);

		for (int i = 0; i < types.length; i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(logTypes[types[i]]);
		}

		sb.append(")");

		return sb.toString();
	}

	private Dashboard dashboard = new Dashboard();
	private ArrayList<ReportItem> reportData;

	public Dashboard getDashboard() {
		return dashboard;
	}

	public ArrayList<ReportItem> getReportData() {
		return reportData;
	}

	public void setReportData(ArrayList<ReportItem> reportData) {
		this.reportData = reportData;
	}
}

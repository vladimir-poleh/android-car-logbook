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
package com.enadein.carlogbook.core;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.echo.holographlibrary.Bar;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.BarInfo;
import com.enadein.carlogbook.bean.Dashboard;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.bean.ReportItem;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class DataLoader extends AsyncTaskLoader<DataInfo> {
	public static final int DASHBOARD = 0xFF000;
	public static final int TYPE = 0x00FF00;
	public static final int LAST_EVENTS = 0x0000FF;

	public static final String FROM = "from";
	public static final String TO = "to";

	private DataInfo data;
	private int type;
	private Bundle params = null;
	private UnitFacade unitFacade;

	public DataLoader(Context context, int type, Bundle params, UnitFacade unitFacade) {
		this(context, type, unitFacade);
		this.params = params;

	}

	public DataLoader(Context context, int type, UnitFacade unitFacade) {
		super(context);
		this.type = type;
		this.unitFacade = unitFacade;
	}

	@Override
	public DataInfo loadInBackground() {
		data = new DataInfo();
		ContentResolver cr = getContext().getContentResolver();

		switch (type) {
			case DASHBOARD: {
				Dashboard dashboard = data.getDashboard();
				dashboard.setTotalOdometerCount(DBUtils.getOdometerCount(cr, 0, 0, -1));
				dashboard.setTotalPrice(DBUtils.getTotalPrice(cr));
				dashboard.setTotalFuelCount(DBUtils.getTotalFuel(cr, 0, 0, false));
				dashboard.setPricePer1(DBUtils.getPricePer1km(cr, 0, 0));
				dashboard.setFuelRateAvg(DBUtils.getAvgFuel(cr, 0, 0, unitFacade));

				dashboard.setTotalFuelPrice(DBUtils.getTotalPrice(cr, 0, 0,
						ProviderDescriptor.Log.Type.FUEL));

				dashboard.setTotalServicePrice(DBUtils.getTotalPrice(cr, 0, 0,
								ProviderDescriptor.Log.Type.OTHER, DataInfo.service));

				dashboard.setTotalOtherPrice(DBUtils.getTotalPrice(cr, 0, 0,
						ProviderDescriptor.Log.Type.OTHER, DataInfo.other));

				dashboard.setTotalParkingPrice(DBUtils.getTotalPrice(cr, 0, 0,
						ProviderDescriptor.Log.Type.OTHER, DataInfo.parking));

				dashboard.setTotalPartsPrice(DBUtils.getTotalPrice(cr, 0, 0,
						ProviderDescriptor.Log.Type.OTHER, DataInfo.parts));

				Calendar c = Calendar.getInstance();
				CommonUtils.trunkMonth(c);

				c.add(Calendar.MONTH, -3);
				ArrayList<BarInfo> bars = new ArrayList<BarInfo>();
				addMonthTotalPrice(c, cr, bars);
				addMonthTotalPrice(c, cr, bars);
				addMonthTotalPrice(c, cr, bars);
				addMonthTotalPrice(c, cr, bars);

				dashboard.setCostLast4Months(bars);

				 c = Calendar.getInstance();
				CommonUtils.trunkMonth(c);

				c.add(Calendar.MONTH, -3);
				bars = new ArrayList<BarInfo>();
				addMonthTotalRun(c, cr, bars);
				addMonthTotalRun(c, cr, bars);
				addMonthTotalRun(c, cr, bars);
				addMonthTotalRun(c, cr, bars);

				dashboard.setRunLast4Months(bars);
				break;
			}
			case TYPE: {
				long from = 0;
				long to = 0;

				if (params != null) {
					from = params.getLong(FROM);
					to = params.getLong(TO);
				}

				String[] logTypes = getContext().getResources().getStringArray(R.array.log_type);
				ArrayList<ReportItem> reportItems = new ArrayList<ReportItem>();

				double fuelTotal = DBUtils.getTotalPrice(cr, from, to, ProviderDescriptor.Log.Type.FUEL, null);

				if (fuelTotal > 0.) {
					ReportItem reportItem = new ReportItem();
					reportItem.setName(getContext().getString(R.string.total_fuel_cost));
					reportItem.setResId(R.drawable.fuel);
					reportItem.setValue(fuelTotal);

					reportItems.add(reportItem);
				}


				for (int i = 0; i < logTypes.length; i++) {
					double total = DBUtils.getTotalPrice(cr, from, to, ProviderDescriptor.Log.Type.OTHER, new int[] {i});

					if (total > 0) {
						ReportItem reportItem = new ReportItem();
						reportItem.setName(logTypes[i]);
						reportItem.setResId(DataInfo.images.get(i));
						reportItem.setValue(total);

						reportItems.add(reportItem);
					}
				}

				if (reportItems.size() > 0) {
					Collections.sort(reportItems, new Comparator<ReportItem>() {
						@Override
						public int compare(ReportItem reportItem, ReportItem reportItem2) {
							return reportItem.getValue() > reportItem2.getValue() ? -1: 0;
						}
					});
				} else {
					addNotFoundItem(reportItems);
				}

				data.setReportData(reportItems);
				break;
			}
			case LAST_EVENTS: {
				ArrayList<ReportItem> reportItems = new ArrayList<ReportItem>();

				String[] logTypes = getContext().getResources().getStringArray(R.array.log_type);

				long date = DBUtils.getLastEventDate(cr, ProviderDescriptor.Log.Type.FUEL, -1);

				if (date > 0) {
					ReportItem reportItem = new ReportItem();
					reportItem.setName(getContext().getString(R.string.total_fuel_cost));
					reportItem.setResId(R.drawable.fuel);
					reportItem.setValue2(date);

					reportItems.add(reportItem);
				}

				for (int i = 0; i < logTypes.length; i++) {
					date = DBUtils.getLastEventDate(cr, ProviderDescriptor.Log.Type.OTHER, i);

					if (date > 0) {
						ReportItem reportItem = new ReportItem();
						reportItem.setName(logTypes[i]);
						reportItem.setResId(DataInfo.images.get(i));
						reportItem.setValue2(date);

						reportItems.add(reportItem);
					}
				}

				if (reportItems.size() > 0) {
					Collections.sort(reportItems, new Comparator<ReportItem>() {
						@Override
						public int compare(ReportItem reportItem, ReportItem reportItem2) {
							return reportItem.getValue2() > reportItem2.getValue2() ? -1: 0;
						}
					});
				} else {
					addNotFoundItem(reportItems);
				}

				data.setReportData(reportItems);
				break;
			}
		}

		return data;
	}

	private void addNotFoundItem(ArrayList<ReportItem> reportItems) {
		ReportItem item = new ReportItem();
		item.setName(getContext().getString(R.string.not_found));
		item.setValue(0);
		item.setResId(R.drawable.clean);
		reportItems.add(item);
	}

	private void addMonthTotalPrice(Calendar c, ContentResolver cr, ArrayList<BarInfo> bars) {
		long from = c.getTimeInMillis();
		String name = CommonUtils.formatMonth(new Date(from));
		c.add(Calendar.MONTH, 1);
		long to = c.getTimeInMillis();
		double price = DBUtils.getTotalPrice(cr, from, to, -1);

		BarInfo barInfo = new BarInfo();
		barInfo.setValue((float) price);
		barInfo.setName(name);
		bars.add(barInfo);
	}

	private void addMonthTotalRun(Calendar c, ContentResolver cr, ArrayList<BarInfo> bars) {
		long from = c.getTimeInMillis();
		String name = CommonUtils.formatMonth(new Date(from));
		c.add(Calendar.MONTH, 1);
		long to = c.getTimeInMillis();
		double run = DBUtils.getOdometerCount(cr, from, to, -1);

		BarInfo barInfo = new BarInfo();
		barInfo.setValue((float) run);
		barInfo.setName(name);
		bars.add(barInfo);
	}

	@Override
	protected void onStartLoading() {
	   if (data != null) {
		   deliverResult(data);
	   } else {
		   forceLoad();
	   }
	}
}
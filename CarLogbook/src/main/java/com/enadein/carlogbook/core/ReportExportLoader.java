package com.enadein.carlogbook.core;


import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.Dashboard;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.bean.ReportItem;
import com.enadein.carlogbook.core.gen.GenWriter;
import com.enadein.carlogbook.core.gen.HtmlWriter;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;
import com.enadein.carlogbook.ui.CreateReportActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ReportExportLoader extends AsyncTaskLoader<File> {
	private final UnitFacade unitFacade;
	private boolean loaded = false;
	private Bundle bundle;
	private GenWriter writer;
	private Resources res;
	private ContentResolver cr;
	private String[] types;

	public ReportExportLoader(Context context, Bundle bundle, UnitFacade unitFacade) {
		super(context);
		types = context.getResources().getStringArray(R.array.log_type);
		res = context.getResources();
		this.unitFacade = unitFacade;
		cr = context.getContentResolver();
		this.bundle = bundle;
		writer = new HtmlWriter();
	}

	@Override
	public File loadInBackground() {
		loaded = false;


		ContentResolver cr = getContext().getContentResolver();

		boolean ok = writer.start();
		String carName = DBUtils.getActiveCarName(cr, DBUtils.getActiveCarId(cr));
		writer.writeCarName(carName);

		boolean info = bundle.getBoolean(CreateReportActivity.GEN_INFO);

		if (info) {
			DataLoader dl = new DataLoader(getContext(),
					DataLoader.DASHBOARD, null, unitFacade);
			DataInfo di = dl.loadInBackground();
			Dashboard b = di.getDashboard();
			writer.startTable();


			printLine(
					res.getString(R.string.total_cost),
					"black",
					unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getTotalPrice(), unitFacade)),
					"red");
			printLine(
					res.getString(R.string.total_run),
					"black",
					unitFacade.appendDistUnit(false, CommonUtils.formatDistance(b.getTotalOdometerCount())),
					"black");

			printLine(
					res.getString(R.string.total_fuel),
					"black",
					unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getTotalFuelCount(), unitFacade)),
					"blue");

			printLine(
					unitFacade.appendDistUnit(false, res.getString(R.string.cost_per1)),
					"black",
					unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPricePer1(), unitFacade)),
					"black");
			printLine(
					unitFacade.appendDistUnit(false, res.getString(R.string.cost1fuelper1)),
					"black",
					unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPriceFuelPer1(), unitFacade)),
					"red");
///---
			printLine(
					unitFacade.appendConsumUnit(true, res.getString(R.string.avg_fuel), 0),
					"black",
					unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getFuelRateAvg100(), unitFacade)),
					"blue");
			printLine(
					unitFacade.appendConsumUnit(true, res.getString(R.string.avg_fuel), 2),
					"black",
					unitFacade.appendDistUnit(false, CommonUtils.formatDistance(b.getFuelRateAvg())),
					"black");
			printLine(
					unitFacade.appendConsumUnit(true, res.getString(R.string.avg_fuel), 1),
					"black",
					unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getFuelRateAvg2(), unitFacade)),
					"blue");



			writer.endTable();
		}

		boolean cost = bundle.getBoolean(CreateReportActivity.GEN_COST);
		if (cost) {
			writer.newLine();
			writer.newLine();
			writer.startTable();

			DataLoader dl = new DataLoader(getContext(),
					DataLoader.TYPE, null, unitFacade);
			DataInfo di = dl.loadInBackground();
			ArrayList<ReportItem> items = di.getReportData();

			for (ReportItem item: items) {
				printLine(item.getName(),
						"black",
						unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(item.getValue(), unitFacade)),
						"red");
			}

			writer.endTable();
		}

		boolean logs = bundle.getBoolean(CreateReportActivity.GEN_LOGS);
		if (logs) {
			writer.newLine();
			writer.newLine();
			writer.startTable();

			Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI, null, ProviderDescriptor.Log.Cols.CAR_ID + " = ?", new String[]{String.valueOf(DBUtils.getActiveCarId(cr))}, ProviderDescriptor.Log.Cols.DATE + " DESC");


			if (c!= null) {
				while (c.moveToNext()) {
					writer.startRow();
					writeLog(c);
					writer.endRow();
				}

					c.close();
			}

			writer.endTable();
		}

		writer.end();

		return (ok) ? writer.getPath() : null;
	}

	private void writeLog(Cursor cursor) {
		int type = cursor.getInt(cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_LOG));

		int idIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols._ID);
		int odometerIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.ODOMETER);
		int priceIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.PRICE);
		int dateIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.DATE);

		double price = cursor.getDouble(priceIdx);
		String date = CommonUtils.formatDate(new Date(cursor.getLong(dateIdx)));

		int odometer = cursor.getInt(odometerIdx);
		int id = cursor.getInt(idIdx);

		if (type == ProviderDescriptor.Log.Type.FUEL) {
			int fuelValueIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_VOLUME);
			double fuelValue = cursor.getDouble(fuelValueIdx);
			double priceTotalDouble = fuelValue * price;

			int stationNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.STATION_NAME);
			String stationName = cursor.getString(stationNameIdx);

			int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
			String fuelName = cursor.getString(fuelNameIdx);

			printCell(date, "black");
			String odometerValue = unitFacade.appendDistUnit(false, String.valueOf(odometer));
			printCell(odometerValue, "black");
			String fuelValueString = unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(fuelValue, unitFacade));
			printCell(fuelValueString, "black");

			String stationAndFuel = fuelName + "(" + stationName + ")";
			printCell(stationAndFuel, "black");
			printCell(getFuelRate(id), "black");

			String priceStringValue = unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(priceTotalDouble, unitFacade));
			printCell(priceStringValue, "red");




//			rateLoader.calculateFuelRate(logFuelHolder.rateView, id);
		} else {
			int nameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.NAME);
			int typeIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.TYPE_ID);

			String name = cursor.getString(nameIdx);
			int typeId = cursor.getInt(typeIdx);

			printCell(date, "black");
			String odometerValue = unitFacade.appendDistUnit(false, String.valueOf(odometer));
			printCell(odometerValue, "black");

			printCell(name, "black");

			String nameString;
			if (typeId == 0) {
				int fuelNameIdx = cursor.getColumnIndex(ProviderDescriptor.LogView.Cols.FUEL_NAME);
				nameString = cursor.getString(fuelNameIdx);

			} else {
				nameString = types[typeId];
			}

			printCell(nameString, "black");
			printCell( "&nbsp", "black");
			String priceStringValue = unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(price, unitFacade));
			printCell(priceStringValue, "red");
		}
	}

	private void printCell(String text, String color) {
		writer.startCell();
		writer.writeText(text, color);
		writer.endCell();
	}

	private void printLine(String text, String colorText, String value, String colorValue) {
		writer.startRow();

		writer.startCell();
		writer.writeText(text, colorText);
		writer.endCell();

		writer.startCell();
		writer.writeText(value, colorValue);
		writer.endCell();

		writer.endRow();
	}

	private String getFuelRate(long id) {
		double rate = DBUtils.getFuelRateFromCurrentLogId(unitFacade, id, getContext().getContentResolver());


		int consum = unitFacade.getConsumptionValue();
		String rateString = (consum == 2) ? CommonUtils.formatDistance(rate) :
				CommonUtils.formatFuel(rate, unitFacade);

		rateString = unitFacade.appendConsumUnit(true, rateString);

		if (rate == 0) {
			rateString = "";
		}

		return rateString;
	}

	@Override
	protected void onStartLoading() {
		if (loaded) {
			deliverResult(null);
		} else {
			forceLoad();
		}
	}
}

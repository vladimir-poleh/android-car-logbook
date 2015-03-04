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
package com.enadein.carlogbook.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.BarInfo;
import com.enadein.carlogbook.bean.Dashboard;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.core.BaseReportFragment;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.DataLoader;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;

import java.util.ArrayList;

public class ReportsFramgent extends BaseReportFragment implements LoaderManager.LoaderCallbacks<DataInfo>, CarChangedListener {
	private TextView totalCost;
	private TextView totalRun;
	private TextView totalFuelValume;
	private TextView cost1;
	private TextView fuelAvg;
	private TextView fuelAvg100;
	private TextView fuelAvg2;

	private TextView totalServiceView;
	private TextView totalFuelView;
	private TextView totalPartsView;
	private TextView totalParkingView;
	private TextView totalOtherView;

	private AQuery a;
	private PieChart pie;
	private BarChart costChart;
	private BarChart incomeChart;
	private BarChart runChart;
	private BarChart cost1Chart;
	private BarChart fuelCost1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.reports_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		a = new AQuery(view);

		totalCost = (TextView) view.findViewById(R.id.totalcost);
		totalRun = (TextView) view.findViewById(R.id.totalrun);
		totalFuelValume = (TextView) view.findViewById(R.id.total_fuel);
		cost1 = (TextView) view.findViewById(R.id.cost_per1);
		fuelAvg = (TextView) view.findViewById(R.id.fuel_avg);
		fuelAvg2 = (TextView) view.findViewById(R.id.fuel_avg2);
		fuelAvg100 = (TextView) view.findViewById(R.id.fuel_avg100);

		totalFuelView = (TextView) view.findViewById(R.id.cost_fuel);
		totalServiceView = (TextView) view.findViewById(R.id.total_service_cost);
		totalPartsView = (TextView) view.findViewById(R.id.total_service_part_cost);
		totalParkingView = (TextView) view.findViewById(R.id.total_parking_cost);
		totalOtherView = (TextView) view.findViewById(R.id.total_other_cost);
		TextView per1View = (TextView) view.findViewById(R.id.label_per1);
		TextView avgLabelView = (TextView) view.findViewById(R.id.label_avg);
		TextView avg100LabelView = (TextView) view.findViewById(R.id.label_avg100);
		TextView avg2LabelView = (TextView) view.findViewById(R.id.label_avg2);

		pie = (PieChart) view.findViewById(R.id.pie); //new
		pie.setHoleRadius(60f);
		pie.setDescription("");
		pie.setNoDataText("");
		pie.setTransparentCircleRadius(65f);
		pie.setDrawCenterText(true);
		pie.setDrawHoleEnabled(true);
		pie.setRotationAngle(0);
		pie.setDrawXValues(false);
		pie.setRotationEnabled(true);
		pie.setUsePercentValues(true);
		pie.setDrawLegend(false);

		ValueFormatter costF = new ValueFormatter() {

			@Override
			public String getFormattedValue(float v) {
				return CommonUtils.formatPriceNew(v, getMediator().getUnitFacade());
			}
		};

		costChart = (BarChart) view.findViewById(R.id.cost_chart);
		costChart.setUnit(" "+ getMediator().getUnitFacade().getCurrency());
		initBar(costChart);
		costChart.setValueFormatter(costF);
		costChart.getYLabels().setFormatter(costF);

		//
		cost1Chart = (BarChart) view.findViewById(R.id.cost1_chart);
		cost1Chart.setUnit(" "+ getMediator().getUnitFacade().getCurrency());
		initBar(cost1Chart);
		cost1Chart.setValueFormatter(costF);
		cost1Chart.getYLabels().setFormatter(costF);

		fuelCost1 = (BarChart) view.findViewById(R.id.fuel_cost1);
		fuelCost1.setUnit(" "+ getMediator().getUnitFacade().getCurrency());
		initBar(fuelCost1);
		fuelCost1.setValueFormatter(costF);
		fuelCost1.getYLabels().setFormatter(costF);
		//


		incomeChart = (BarChart) view.findViewById(R.id.incomeChart);
		incomeChart.setUnit(" "+ getMediator().getUnitFacade().getCurrency());
		initBar(incomeChart);
		incomeChart.setValueFormatter(costF);
		incomeChart.getYLabels().setFormatter(costF);


		runChart = (BarChart) view.findViewById(R.id.run_chart);
		runChart.setUnit(" " + getMediator().getUnitFacade().getDistUnit());
		initBar(runChart);
		ValueFormatter runF = new ValueFormatter() {

			@Override
			public String getFormattedValue(float v) {
				return CommonUtils.formatDistance(v);
			}
		};

		runChart.setValueFormatter(runF);
		runChart.getYLabels().setFormatter(runF);

		UnitFacade unitFacade = getMediator().getUnitFacade();

		unitFacade.appendDistUnit(per1View, true);
		unitFacade.appendDistUnit(a.id(R.id.label_fuel_per1).getTextView(), true);
		unitFacade.appendConsumUnit(avg100LabelView, true, 0);
		unitFacade.appendConsumUnit(avgLabelView, true, 2);
		unitFacade.appendConsumUnit(avg2LabelView, true, 1);
	}

	private void initBar(BarChart bar) {
		bar.setDescription("");
		bar.setDrawYValues(true);
		bar.setPinchZoom(false);
		bar.setDrawBarShadow(false);
		bar.setDrawVerticalGrid(false);
		bar.setDrawHorizontalGrid(false);
		bar.setDrawGridBackground(false);
		bar.setDrawYLabels(true);
		bar.setDrawLegend(false);
		bar.setNoDataText("");
//		bar.setPinchZoom(false);
//		bar.setScaleEnabled(false);
//		bar.setEnable;

		XLabels xLabels = bar.getXLabels();
		xLabels.setPosition(XLabels.XLabelPosition.BOTTOM);
		xLabels.setCenterXLabelText(true);
		xLabels.setSpaceBetweenLabels(0);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (DBUtils.hasReports(getActivity().getContentResolver())) {
			getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_DASHBOARD_ID, null, this);
		} else {
			getView().findViewById(R.id.report).setVisibility(View.GONE);
			getView().findViewById(R.id.no_reports).setVisibility(View.VISIBLE);

		}
		getMediator().showCarSelection(this);
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}


	@Override
	public android.support.v4.content.Loader<DataInfo> onCreateLoader(int id, Bundle args) {
		return new DataLoader(getActivity(), DataLoader.DASHBOARD, getMediator().getUnitFacade());
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<DataInfo> loader, DataInfo data) {
		Dashboard b = data.getDashboard();
		UnitFacade unitFacade = getMediator().getUnitFacade();

		totalRun.setText(unitFacade.appendDistUnit(false, CommonUtils.formatDistance(b.getTotalOdometerCount())));
		totalCost.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getTotalPrice(), unitFacade)));
		cost1.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPricePer1(), unitFacade)));

		a.id(R.id.cost_fuel_per1).text(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPriceFuelPer1(), unitFacade)));
		unitFacade.appendDistUnit(a.id(R.id.lb_cost1).getTextView(), false);
		unitFacade.appendDistUnit(a.id(R.id.lb_fuelcost1).getTextView(), false);

		fuelAvg.setText(CommonUtils.formatDistance(b.getFuelRateAvg()));
		fuelAvg2.setText(CommonUtils.formatFuel(b.getFuelRateAvg2(), unitFacade));
		fuelAvg100.setText(CommonUtils.formatFuel(b.getFuelRateAvg100(), unitFacade));

		unitFacade.appendDistUnit(fuelAvg, false);
		unitFacade.appendFuelUnit(fuelAvg2, false);
		unitFacade.appendFuelUnit(fuelAvg100, false);

		totalFuelValume.setText(unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getTotalFuelCount(), unitFacade)));

		double totalFuelPrice = b.getTotalFuelPrice();
		double totalServicePrice = b.getTotalServicePrice();
		double totalPartsPrice = b.getTotalPartsPrice();
		double totalParkingPrice = b.getTotalParkingPrice();
		double totalOtherPrice = b.getTotalOtherPrice();
		totalFuelView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalFuelPrice, unitFacade)));
		totalServiceView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalServicePrice, unitFacade)));
		totalPartsView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalPartsPrice, unitFacade)));
		totalParkingView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalParkingPrice, unitFacade)));
		totalOtherView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalOtherPrice, unitFacade)));

		ArrayList<Integer> colors = new ArrayList<Integer>();
		ArrayList<Entry> yValues = new ArrayList<Entry>();
		ArrayList<String> xValues = new ArrayList<String>();

		int idx = 0;
		if (totalFuelPrice > 0) {
			xValues.add("");
			colors.add(DataInfo.COLOR_FUEL);
			yValues.add(new Entry((float) totalFuelPrice, idx));
			idx++;
		}

		if (totalServicePrice > 0) {
			xValues.add("");
			colors.add(DataInfo.COLOR_SERVICE);
			yValues.add(new Entry((float) totalServicePrice, idx));
			idx++;
		}

		if (totalPartsPrice > 0) {
			xValues.add("");
			colors.add(DataInfo.COLOR_PARTS);
			yValues.add(new Entry((float) totalPartsPrice, idx));
			idx++;
		}
		if (totalParkingPrice > 0) {
			xValues.add("");
			colors.add(DataInfo.COLOR_PARKING);
			yValues.add(new Entry((float) totalParkingPrice, idx));
			idx++;
		}

		if (totalOtherPrice > 0) {
			xValues.add("");
			colors.add(DataInfo.COLOR_OTHERS);
			yValues.add(new Entry((float) totalOtherPrice, idx));
		}


		PieDataSet pieDataSet = new PieDataSet(yValues, "");
		pieDataSet.setColors(colors);
		pieDataSet.setSliceSpace(3f);

		PieData pieData = new PieData(xValues, pieDataSet);
		pie.setData(pieData);
		pie.invalidate();
		pie.animateXY(900, 900);

		//COST BAR

		ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();
		ArrayList<String> mounthXVal = new ArrayList<String>();

		idx = 0;
		for (BarInfo bi : b.getCostLast4Months()) {
			barEntries.add(new BarEntry(bi.getValue(), idx));
			mounthXVal.add(bi.getName());
			idx++;
		}
		BarDataSet barSet = new BarDataSet(barEntries, "");
		barSet.setColor(0xFFff5722);
		BarData costData = new BarData(mounthXVal, barSet);
		costChart.setData(costData);
		costChart.invalidate();
		costChart.animateY(3500);

		//INCOME

		{
			 barEntries = new ArrayList<BarEntry>();
			mounthXVal = new ArrayList<String>();

			idx = 0;

			boolean showIncome = false;
			for (BarInfo bi : b.getIncomeLast4Months()) {
				barEntries.add(new BarEntry(bi.getValue(), idx));
				if (bi.getValue() > 0) {
					showIncome = true;
				}
				mounthXVal.add(bi.getName());
				idx++;
			}
			 barSet = new BarDataSet(barEntries, "");
			barSet.setColor(getResources().getColor(R.color.income));
			 costData = new BarData(mounthXVal, barSet);

			if (showIncome) {
				a.id(R.id.id_income).visible();
				a.id(R.id.view_income).visible();
				incomeChart.setData(costData);
				incomeChart.invalidate();
				incomeChart.animateY(3500);
			}
		}

		{
			barEntries = new ArrayList<BarEntry>();
			mounthXVal = new ArrayList<String>();
			idx = 0;
			for (BarInfo bi : b.getRunLast4Months()) {
				barEntries.add(new BarEntry(bi.getValue(), idx));
				mounthXVal.add(bi.getName());
				idx++;
			}

			barSet = new BarDataSet(barEntries, "");
			barSet.setColor(0xff8BC34A);
			BarData runData = new BarData(mounthXVal, barSet);
			runChart.setData(runData);
			runChart.invalidate();
			runChart.animateY(4500);
		}

		{
			barEntries = new ArrayList<BarEntry>();
			mounthXVal = new ArrayList<String>();
			idx = 0;
			for (BarInfo bi : b.getCostPer1()) {
				barEntries.add(new BarEntry(bi.getValue(), idx));
				mounthXVal.add(bi.getName());
				idx++;
			}

			barSet = new BarDataSet(barEntries, "");
			barSet.setColor(0xFFff5722);
			BarData runData = new BarData(mounthXVal, barSet);
			cost1Chart.setData(runData);
			cost1Chart.invalidate();
			cost1Chart.animateY(4500);
		}

		{
			barEntries = new ArrayList<BarEntry>();
			mounthXVal = new ArrayList<String>();
			idx = 0;
			for (BarInfo bi : b.getFuelCostPer1()) {
				barEntries.add(new BarEntry(bi.getValue(), idx));
				mounthXVal.add(bi.getName());
				idx++;
			}

			barSet = new BarDataSet(barEntries, "");
			barSet.setColor(0xFFff5722);
			BarData runData = new BarData(mounthXVal, barSet);
			fuelCost1.setData(runData);
			fuelCost1.invalidate();
			fuelCost1.animateY(4500);
		}
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<DataInfo> loader) {

	}

	@Override
	public void selectMenuItem(Menu menu) {
		menu.findItem(R.id.menu_dashboard).setIcon(R.drawable.stat);
	}

	@Override
	public void onCarChanged(long id) {
		getMediator().showReports();
	}
}

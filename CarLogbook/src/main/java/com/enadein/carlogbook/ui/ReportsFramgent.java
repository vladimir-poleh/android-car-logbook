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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.BarInfo;
import com.enadein.carlogbook.bean.Dashboard;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.DataLoader;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;

import java.util.ArrayList;

public class ReportsFramgent extends BaseFragment implements LoaderManager.LoaderCallbacks<DataInfo> {
	private TextView totalCost;
	private TextView totalRun;
	private TextView totalFuelValume;
	private TextView cost1;
	private TextView fuelAvg;

	private PieGraph pieGraph;
	private BarGraph costMonth;
	private BarGraph runMonth;

	private TextView totalServiceView;
	private TextView totalFuelView;
	private TextView totalPartsView;
	private TextView totalParkingView;
	private TextView totalOtherView;

	private TextView per1View;
	private TextView avgLabelView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.reports_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		totalCost = (TextView) view.findViewById(R.id.totalcost);
		totalRun = (TextView) view.findViewById(R.id.totalrun);
		totalFuelValume = (TextView) view.findViewById(R.id.total_fuel);
		cost1 = (TextView) view.findViewById(R.id.cost_per1);
		fuelAvg = (TextView) view.findViewById(R.id.fuel_avg);

		totalFuelView = (TextView) view.findViewById(R.id.cost_fuel);
		totalServiceView = (TextView) view.findViewById(R.id.total_service_cost);
		totalPartsView = (TextView) view.findViewById(R.id.total_service_part_cost);
		totalParkingView = (TextView) view.findViewById(R.id.total_parking_cost);
		totalOtherView = (TextView) view.findViewById(R.id.total_other_cost);
		per1View = (TextView) view.findViewById(R.id.label_per1);
		avgLabelView = (TextView) view.findViewById(R.id.label_avg);

		pieGraph = (PieGraph) view.findViewById(R.id.graph);

		costMonth = (BarGraph)view.findViewById(R.id.cost_month);
		runMonth = (BarGraph)view.findViewById(R.id.run_month);


        UnitFacade unitFacade = getMediator().getUnitFacade();

        unitFacade.appendConsumUnit(avgLabelView,true);
        unitFacade.appendDistUnit(per1View,true);

//		addSlice(0.01f, 0xFFEEEEEE);
//		addSlice(0.01f, 0xFFEEEEEE);


		getLoaderManager().initLoader(CarLogbook.LoaderDesc.REP_DASHBOARD_ID, null, this);
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

//		unitFacade.appendConsumUnit(avgLabelView,true);
//		unitFacade.appendDistUnit(per1View,true);

		totalRun.setText(unitFacade.appendDistUnit(false,CommonUtils.formatPrice(b.getTotalOdometerCount())));
		totalCost.setText(unitFacade.appendCurrency(false, CommonUtils.formatPrice(b.getTotalPrice())));
		cost1.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(b.getPricePer1())));
		fuelAvg.setText(CommonUtils.formatPrice(b.getFuelRateAvg()));
		unitFacade.appendConsumValue(fuelAvg, false);
		totalFuelValume.setText(unitFacade.appendFuelUnit(false, CommonUtils.formatPrice(b.getTotalFuelCount())));

//		pieGraph.setDuration(2000);
//		pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
//		pieGraph.animateToGoalValues();
		float totalFuelPrice = Math.round(b.getTotalFuelPrice());
		addSlice(totalFuelPrice, DataInfo.COLOR_FUEL);
		totalFuelView.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(totalFuelPrice)));

		if (totalFuelPrice > 0) {
			float vl = (float)(totalFuelPrice * 0.01) / 100;
			addSlice(vl, 0xFFEEEEEE);
			addSlice(vl, 0xFFEEEEEE);

		} else {
			addSlice(0.001f, 0xFFEEEEEE);
			addSlice(0.001f, 0xFFEEEEEE);
		}

		float totalServicePrice = Math.round(b.getTotalServicePrice());
		addSlice(totalServicePrice, DataInfo.COLOR_SERVICE);
		totalServiceView.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(totalServicePrice)));

		float totalPartsPrice = Math.round(b.getTotalPartsPrice());
		addSlice(totalPartsPrice, DataInfo.COLOR_PARTS);
		totalPartsView.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(totalPartsPrice)));

		float totalParkingPrice = Math.round(b.getTotalParkingPrice());
		totalParkingView.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(totalParkingPrice)));
		addSlice(totalParkingPrice, DataInfo.COLOR_PARKING);

		float totalOtherPrice = Math.round(b.getTotalOtherPrice());
		totalOtherView.setText(unitFacade.appendCurrency(false,CommonUtils.formatPrice(totalOtherPrice)));
		addSlice(totalOtherPrice, DataInfo.COLOR_OTHERS);

		ArrayList<Bar> points = new ArrayList<Bar>();

		for (BarInfo bi : b.getCostLast4Months()) {
			Bar d = new Bar();
			d.setName(bi.getName());
			d.setValue(bi.getValue());
			d.setValueString(unitFacade.appendCurrency(false, "" + CommonUtils.formatPrice(bi.getValue())));
			points.add(d);
		}
		costMonth.setBars(points);
		points = new ArrayList<Bar>();

		for (BarInfo bi : b.getRunLast4Months()) {
			Bar d = new Bar();
			d.setName(bi.getName());
			d.setColor(DataInfo.COLOR_SERVICE);
			d.setValue(bi.getValue());
			d.setValueString(unitFacade.appendDistUnit(false, CommonUtils.formatPrice(bi.getValue())));
			points.add(d);
		}
		runMonth.setBars(points);
	}


	public void addSlice(float value, int color) {
		PieSlice slice = new PieSlice();
		slice.setColor(color);
		slice.setValue(value);
		pieGraph.addSlice(slice);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<DataInfo> loader) {

	}
}

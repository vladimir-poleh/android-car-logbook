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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
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
    private TextView fuelAvg100;
    private TextView fuelAvg2;

    private PieGraph pieGraph;
    private BarGraph costMonth;
    private BarGraph runMonth;

    private TextView totalServiceView;
    private TextView totalFuelView;
    private TextView totalPartsView;
    private TextView totalParkingView;
    private TextView totalOtherView;

	private PieSlice fuelSlice;
	private PieSlice serviceSlice;
	private PieSlice partsSlice;
	private PieSlice parkingSlice;
	private PieSlice otherSlice;

	private int sliceCount = 0;
	private PieSlice activeSlice;
	private AQuery a;

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

        pieGraph = (PieGraph) view.findViewById(R.id.graph);

        costMonth = (BarGraph) view.findViewById(R.id.cost_month);
        runMonth = (BarGraph) view.findViewById(R.id.run_month);
        UnitFacade unitFacade = getMediator().getUnitFacade();

        unitFacade.appendDistUnit(per1View, true);
		unitFacade.appendDistUnit(a.id(R.id.label_fuel_per1).getTextView(), true);
        unitFacade.appendConsumUnit(avg100LabelView, true, 0);
        unitFacade.appendConsumUnit(avgLabelView, true, 2);
        unitFacade.appendConsumUnit(avg2LabelView, true, 1);

//        FloatingActionButton fabButton = new FloatingActionButton.Builder(getActivity())
//                .withDrawable(getResources().getDrawable(R.drawable.fuel))
//                .withButtonColor(Color.WHITE)
//                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
//                .withMargins(0, 0, 16, 16)
//                .create();


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

        totalRun.setText(unitFacade.appendDistUnit(false, CommonUtils.formatDistance(b.getTotalOdometerCount())));
        totalCost.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getTotalPrice(), unitFacade)));
        cost1.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPricePer1(), unitFacade)));

		a.id(R.id.cost_fuel_per1).text(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPriceFuelPer1(), unitFacade)));


		fuelAvg.setText(CommonUtils.formatDistance(b.getFuelRateAvg()));
        fuelAvg2.setText(CommonUtils.formatFuel(b.getFuelRateAvg2(), unitFacade));
        fuelAvg100.setText(CommonUtils.formatFuel(b.getFuelRateAvg100(), unitFacade));

        unitFacade.appendDistUnit(fuelAvg, false);
        unitFacade.appendFuelUnit(fuelAvg2, false);
        unitFacade.appendFuelUnit(fuelAvg100, false);

        totalFuelValume.setText(unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getTotalFuelCount(), unitFacade)));

//		pieGraph.setDuration(2000);
//		pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
//		pieGraph.animateToGoalValues();
		fuelSlice = addSlice(1, DataInfo.COLOR_FUEL);
		serviceSlice = addSlice(10, DataInfo.COLOR_SERVICE);
		partsSlice = addSlice(100, DataInfo.COLOR_PARTS);
		parkingSlice = addSlice(1000, DataInfo.COLOR_PARKING);
		otherSlice = addSlice(10000, DataInfo.COLOR_OTHERS);

		sliceCount = 0;


		float totalFuelPrice = Math.round(b.getTotalFuelPrice());
		totalFuelView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalFuelPrice, unitFacade)));
		fuelSlice.setGoalValue(totalFuelPrice);
		updateSliceCount(fuelSlice);

        float totalServicePrice = Math.round(b.getTotalServicePrice());
        totalServiceView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalServicePrice, unitFacade)));
		serviceSlice.setGoalValue(totalServicePrice);
		updateSliceCount(serviceSlice);

        float totalPartsPrice = Math.round(b.getTotalPartsPrice());
        totalPartsView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalPartsPrice, unitFacade)));
		partsSlice.setGoalValue(totalPartsPrice);
		updateSliceCount(partsSlice);

        float totalParkingPrice = Math.round(b.getTotalParkingPrice());
        totalParkingView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalParkingPrice, unitFacade)));
		parkingSlice.setGoalValue(totalParkingPrice);
		updateSliceCount( parkingSlice);

        float totalOtherPrice = Math.round(b.getTotalOtherPrice());
        totalOtherView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalOtherPrice, unitFacade)));

		if (sliceCount > 1) {
			otherSlice.setGoalValue(totalOtherPrice);
		} else {
			if (activeSlice == null) {
				activeSlice = fuelSlice;
				activeSlice.setColor(DataInfo.COLOR_OTHERS);
				activeSlice.setGoalValue(totalOtherPrice);
			}
			otherSlice.setColor(activeSlice.getColor());
			otherSlice.setGoalValue(activeSlice.getGoalValue() * (float)0.001);
		}

		pieGraph.setDuration(700);
		pieGraph.animateToGoalValues();

        ArrayList<Bar> points = new ArrayList<Bar>();

        for (BarInfo bi : b.getCostLast4Months()) {
            Bar d = new Bar();
            d.setName(bi.getName());
            d.setValue(bi.getValue());
            d.setValueString(unitFacade.appendCurrency(false, "" + CommonUtils.formatPriceNew(bi.getValue(), unitFacade)));
            points.add(d);
        }
        costMonth.setBars(points);
        points = new ArrayList<Bar>();

        for (BarInfo bi : b.getRunLast4Months()) {
            Bar d = new Bar();
            d.setName(bi.getName());
            d.setColor(DataInfo.COLOR_SERVICE);
			d.setValue(bi.getValue());
            d.setValueString(unitFacade.appendDistUnit(false, CommonUtils.formatDistance(bi.getValue())));
            points.add(d);
        }
        runMonth.setBars(points);
    }

	private void updateSliceCount(PieSlice slice) {
		if (slice.getGoalValue() > 0) {
			sliceCount++;
			if (activeSlice == null) {
				activeSlice = slice;
			} else if (slice.getValue() > activeSlice.getGoalValue() ){
				activeSlice = slice;
			}

		}
	}


    public PieSlice addSlice(float value, int color) {
        PieSlice slice = new PieSlice();
        slice.setColor(color);
        slice.setValue(value);
        pieGraph.addSlice(slice);
		return slice;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<DataInfo> loader) {

    }
}

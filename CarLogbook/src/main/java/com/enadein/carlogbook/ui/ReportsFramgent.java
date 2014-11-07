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
import com.enadein.carlogbook.core.FloatingActionButton;
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

    private TextView per1View;
    private TextView avgLabelView;
    private TextView avg100LabelView;
    private TextView avg2LabelView;


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
        fuelAvg2 = (TextView) view.findViewById(R.id.fuel_avg2);
        fuelAvg100 = (TextView) view.findViewById(R.id.fuel_avg100);

        totalFuelView = (TextView) view.findViewById(R.id.cost_fuel);
        totalServiceView = (TextView) view.findViewById(R.id.total_service_cost);
        totalPartsView = (TextView) view.findViewById(R.id.total_service_part_cost);
        totalParkingView = (TextView) view.findViewById(R.id.total_parking_cost);
        totalOtherView = (TextView) view.findViewById(R.id.total_other_cost);
        per1View = (TextView) view.findViewById(R.id.label_per1);
        avgLabelView = (TextView) view.findViewById(R.id.label_avg);
        avg100LabelView = (TextView) view.findViewById(R.id.label_avg100);
        avg2LabelView = (TextView) view.findViewById(R.id.label_avg2);

        pieGraph = (PieGraph) view.findViewById(R.id.graph);

        costMonth = (BarGraph) view.findViewById(R.id.cost_month);
        runMonth = (BarGraph) view.findViewById(R.id.run_month);
        UnitFacade unitFacade = getMediator().getUnitFacade();

        unitFacade.appendDistUnit(per1View, true);
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

        totalRun.setText(unitFacade.appendDistUnit(false, String.valueOf(b.getTotalOdometerCount())));
        totalCost.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getTotalPrice(), unitFacade)));
        cost1.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(b.getPricePer1(), unitFacade)));

        fuelAvg.setText(CommonUtils.formatPriceNew(b.getFuelRateAvg(), unitFacade));
        fuelAvg2.setText(CommonUtils.formatFuel(b.getFuelRateAvg2(), unitFacade));
        fuelAvg100.setText(CommonUtils.formatFuel(b.getFuelRateAvg100(), unitFacade));

        unitFacade.appendDistUnit(fuelAvg, false);
        unitFacade.appendFuelUnit(fuelAvg2, false);
        unitFacade.appendFuelUnit(fuelAvg100, false);

        totalFuelValume.setText(unitFacade.appendFuelUnit(false, CommonUtils.formatFuel(b.getTotalFuelCount(), unitFacade)));

//		pieGraph.setDuration(2000);
//		pieGraph.setInterpolator(new AccelerateDecelerateInterpolator());
//		pieGraph.animateToGoalValues();
        float totalFuelPrice = Math.round(b.getTotalFuelPrice());
        addSlice(totalFuelPrice, DataInfo.COLOR_FUEL);
        totalFuelView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalFuelPrice, unitFacade)));

        if (totalFuelPrice > 0) {
            float vl = (float) (totalFuelPrice * 0.01) / 100;
            addSlice(vl, 0xFFEEEEEE);
            addSlice(vl, 0xFFEEEEEE);

        } else {
            addSlice(0.001f, 0xFFEEEEEE);
            addSlice(0.001f, 0xFFEEEEEE);
        }

        float totalServicePrice = Math.round(b.getTotalServicePrice());
        addSlice(totalServicePrice, DataInfo.COLOR_SERVICE);
        totalServiceView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalServicePrice, unitFacade)));

        float totalPartsPrice = Math.round(b.getTotalPartsPrice());
        addSlice(totalPartsPrice, DataInfo.COLOR_PARTS);
        totalPartsView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalPartsPrice, unitFacade)));

        float totalParkingPrice = Math.round(b.getTotalParkingPrice());
        totalParkingView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalParkingPrice, unitFacade)));
        addSlice(totalParkingPrice, DataInfo.COLOR_PARKING);

        float totalOtherPrice = Math.round(b.getTotalOtherPrice());
        totalOtherView.setText(unitFacade.appendCurrency(false, CommonUtils.formatPriceNew(totalOtherPrice, unitFacade)));
        addSlice(totalOtherPrice, DataInfo.COLOR_OTHERS);

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
            d.setValueString(unitFacade.appendDistUnit(false, CommonUtils.formatPriceNew(bi.getValue(), unitFacade)));
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

package com.enadein.carlogbook.ui;


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.bean.Dashboard;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.bean.XReport;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.BaseReportFragment;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.DataLoader;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.CommonUtils;

public class DetailReportFragment  extends BaseReportFragment implements LoaderManager.LoaderCallbacks<DataInfo>, SwipeRefreshLayout.OnRefreshListener, CarChangedListener {
    private TextView totalFuelValume;
    private TextView fillupCount;
    private TextView fillupMin;
    private TextView fillupMax;
    private TextView fillupAvg;
    private TextView fuelVolumeCurrentMonth;
    private TextView fuelVolumeLastMonth;
    private TextView fuelVolumeCurrentYear;
    private TextView fuelVolumeLastYear;
    private AQuery aq ;

	private SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.detail_report, container, false);
    }

    @Override
    public void onResume() {
        super.onPause();
//        showProgress(true);
        getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_DETAILED, null, this);
		getMediator().showCarSelection(this);
	}

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        aq = new AQuery(view);

        totalFuelValume = (TextView) view.findViewById(R.id.totalFuel);
        fillupCount = (TextView) view.findViewById(R.id.fillupCount);
        fillupMin = (TextView) view.findViewById(R.id.fillupMin);
        fillupMax = (TextView) view.findViewById(R.id.fillupMax);
        fillupAvg = (TextView) view.findViewById(R.id.fillupAvg);
        fuelVolumeCurrentMonth = (TextView) view.findViewById(R.id.fuel_volume_month);
        fuelVolumeLastMonth = (TextView) view.findViewById(R.id.fuel_volume_last_month);
        fuelVolumeCurrentYear = (TextView) view.findViewById(R.id.fuel_volume_year);
        fuelVolumeLastYear = (TextView) view.findViewById(R.id.fuel_volume_last_year);

        UnitFacade unitFacade = getMediator().getUnitFacade();
        TextView per1Label = aq.id(R.id.cost_per1label).getTextView();
        unitFacade.appendDistUnit(per1Label, true);

		TextView perfuel1Label = aq.id(R.id.cost_fuel_per1label).getTextView();
		unitFacade.appendDistUnit(perfuel1Label, true);

        unitFacade.appendConsumUnit(aq.id(R.id.avg100Label).getTextView(), true, 0);
        unitFacade.appendConsumUnit(aq.id(R.id.lperkmLabel).getTextView(), true, 1);
        unitFacade.appendConsumUnit(aq.id(R.id.kmperlLabel).getTextView(), true, 2);

		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.progress1,
				R.color.progress2,
				R.color.progress3,
				R.color.progress4);
		swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public String getSubTitle() {
        return getString(R.string.menu_item_reports);
    }


    @Override
    public Loader<DataInfo> onCreateLoader(int i, Bundle bundle) {
//		swipeLayout.setRefreshing(true);

		swipeLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
			}
		});
        return new DataLoader(getActivity(), DataLoader.DETAILED, getMediator().getUnitFacade());
    }

    @Override
    public void onLoadFinished(Loader<DataInfo> dataInfoLoader, DataInfo dataInfo) {
        XReport x = dataInfo.getxReport();
        UnitFacade unitFacade = getMediator().getUnitFacade();

        totalFuelValume.setText(CommonUtils.formatFuel(x.fuelCountTotal, unitFacade));
        unitFacade.appendFuelUnit(totalFuelValume, false);

        fillupCount.setText(String.valueOf(x.fillupCount));

        fillupMin.setText(CommonUtils.formatFuel(x.minFillupVolume, unitFacade));
        unitFacade.appendFuelUnit(fillupMin, false);
        fillupMax.setText(CommonUtils.formatFuel(x.maxFillupVolume, unitFacade));
        unitFacade.appendFuelUnit(fillupMax, false);
        fillupAvg.setText(CommonUtils.formatFuel(x.avgFillupVolume, unitFacade));
        unitFacade.appendFuelUnit(fillupAvg, false);
        fuelVolumeCurrentMonth.setText(CommonUtils.formatFuel(x.fuelVolumeCurrentMonth, unitFacade));
        unitFacade.appendFuelUnit(fuelVolumeCurrentMonth, false);
        fuelVolumeLastMonth.setText(CommonUtils.formatFuel(x.fuelVolumeLastMonth, unitFacade));
        unitFacade.appendFuelUnit(fuelVolumeLastMonth, false);
        fuelVolumeCurrentYear.setText(CommonUtils.formatFuel(x.fuelVolumeCurrentYear, unitFacade));
        unitFacade.appendFuelUnit(fuelVolumeCurrentYear, false);
        fuelVolumeLastYear.setText(CommonUtils.formatFuel(x.fuelVolumeLastYear, unitFacade));
        unitFacade.appendFuelUnit(fuelVolumeLastYear, false);

        //DIST
        unitFacade.appendDistUnit(aq.id(R.id.totalDist)
                .text(CommonUtils.formatDistance(x.totalDist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.odometer_count)
                .text(CommonUtils.formatDistance(x.odometer_count)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.month_dist)
                .text(CommonUtils.formatDistance(x.month_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.last_month_dist)
                .text(CommonUtils.formatDistance(x.last_month_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.year_dist)
                .text(CommonUtils.formatDistance(x.year_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.last_year_dist)
                .text(CommonUtils.formatDistance(x.last_year_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.per_day_dist)
                .text(CommonUtils.formatDistance(x.per_day_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.per_month_dist)
                .text(CommonUtils.formatDistance(x.per_month_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.per_year_dist)
                .text(CommonUtils.formatDistance(x.per_year_dist)).getTextView(), false);
///
        unitFacade.appendCurrency(aq.id(R.id.cost_total)
                .text(CommonUtils.formatPriceNew(x.cost_total, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_month)
                .text(CommonUtils.formatPriceNew(x.cost_total_month, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_last_month)
                .text(CommonUtils.formatPriceNew(x.cost_total_last_month, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_year)
                .text(CommonUtils.formatPriceNew(x.cost_total_year, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_last_year)
                .text(CommonUtils.formatPriceNew(x.cost_total_last_year, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_price_min)
                .text(CommonUtils.formatPriceNew(x.cost_price_min, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_price_max)
                .text(CommonUtils.formatPriceNew(x.cost_price_max, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_price_avg)
                .text(CommonUtils.formatPriceNew(x.cost_price_avg, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_fillup_min)
                .text(CommonUtils.formatPriceNew(x.cost_fillup_min, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_fillup_max)
                .text(CommonUtils.formatPriceNew(x.cost_fillup_max, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_fillup_avg)
                .text(CommonUtils.formatPriceNew(x.cost_fillup_avg, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_day)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_day, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_month)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_month, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_year)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_year, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_day_fuel)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_day_fuel, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_month_fuel)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_month_fuel, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_year_fuel)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_year_fuel, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_day_other)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_day_other, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_month_other)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_month_other, unitFacade)).getTextView(), false);
        unitFacade.appendCurrency(aq.id(R.id.cost_total_per_year_other)
                .text(CommonUtils.formatPriceNew(x.cost_total_per_year_other, unitFacade)).getTextView(), false);

        unitFacade.appendCurrency(aq.id(R.id.cost_per1)
                .text(CommonUtils.formatPriceNew(x.cost_per1, unitFacade)).getTextView(), false);

		unitFacade.appendCurrency(aq.id(R.id.cost_fuel_per1)
				.text(CommonUtils.formatPriceNew(x.cost_fuel_per1, unitFacade)).getTextView(), false);

		unitFacade.appendCurrency(aq.id(R.id.total_income)
				.text(CommonUtils.formatPriceNew(x.total_income, unitFacade))
				.getTextView(), false);
		unitFacade.appendCurrency(aq.id(R.id.total_month_income)
				.text(CommonUtils.formatPriceNew(x.total_month_income, unitFacade))
				.getTextView(), false);
		unitFacade.appendCurrency(aq.id(R.id.total_month_last_income)
				.text(CommonUtils.formatPriceNew(x.total_month_last_income, unitFacade))
				.getTextView(), false);
		unitFacade.appendCurrency(aq.id(R.id.total_year_income)
				.text(CommonUtils.formatPriceNew(x.total_year_income, unitFacade))
				.getTextView(), false);
		unitFacade.appendCurrency(aq.id(R.id.total_year_last_income)
				.text(CommonUtils.formatPriceNew(x.total_year_last_income, unitFacade))
				.getTextView(), false);

		//consum
        unitFacade.appendFuelUnit(aq.id(R.id.avg100).text(CommonUtils.formatFuel(x.avg100, unitFacade)).getTextView(), false);
        unitFacade.appendFuelUnit(aq.id(R.id.lperkm).text(CommonUtils.formatFuel(x.avglperkm, unitFacade)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.kmperl).text(CommonUtils.formatDistance(x.avgkmperl)).getTextView(), false);

        //others
        unitFacade.appendDistUnit(aq.id(R.id.min_fillup_dist)
                .text(CommonUtils.formatDistance(x.min_fillup_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.max_fillup_dist)
                .text(CommonUtils.formatDistance(x.max_fillup_dist)).getTextView(), false);
        unitFacade.appendDistUnit(aq.id(R.id.avg_fillup_dist)
                .text(CommonUtils.formatDistance(x.avg_fillup_dist)).getTextView(), false);

        aq.id(R.id.min_days_fillups)
                .text(CommonUtils.formatDistance(x.min_days_fillups));
        aq.id(R.id.max_days_fillups)
                .text(CommonUtils.formatDistance(x.max_days_fillups));



        aq.id(R.id.avg_days_fillups)
                .text(CommonUtils.formatDistance(x.avg_days_fillups));

//        showProgress(false);
		swipeLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<DataInfo> dataInfoLoader) {

    }

	@Override
	public void onRefresh() {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_DETAILED, null, this);
	}

	@Override
	public void selectMenuItem(Menu menu) {
		menu.findItem(R.id.menu_detail).setIcon(R.drawable.stat);
	}

	@Override
	public void onCarChanged(long id) {
		getMediator().showDetailedReport();
//		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_DETAILED, null, this);
	}
}

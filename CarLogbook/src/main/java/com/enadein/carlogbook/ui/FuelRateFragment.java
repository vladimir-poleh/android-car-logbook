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


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.FuelRateAdapter;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.BaseReportFragment;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.DataLoader;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class FuelRateFragment extends BaseReportFragment implements
		LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener, CarChangedListener {

	private FuelRateAdapter adapter;
	private SwipeRefreshLayout swipeLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.rate_report, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		adapter = new FuelRateAdapter(getActivity(), null, getMediator().getUnitFacade());
		ListView carListView = (ListView) view.findViewById(R.id.list);
		carListView.setAdapter(adapter);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeLayout.setColorSchemeResources(R.color.progress1,
				R.color.progress2,
				R.color.progress3,
				R.color.progress4);
		swipeLayout.setOnRefreshListener(this);
	}

	@Override
	public void onResume() {
		super.onPause();

//		getLoaderManager().initLoader(CarLogbook.LoaderDesc.REP_FUEL_RATE_ID,
//				null, this);

//        showProgress(true);
        getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_CALC_FUEL_RATE, null, new LoaderCalculate());
//		swipeLayout.setRefreshing(true);
		getMediator().showCarSelection(this);

	}

	@Override
	public void onRefresh() {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_CALC_FUEL_RATE, null, new LoaderCalculate());
	}

	@Override
	public void selectMenuItem(Menu menu) {
		menu.findItem(R.id.menu_fuel_rate).setIcon(R.drawable.stat);
	}

	@Override
	public void onCarChanged(long id) {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_CALC_FUEL_RATE, null, new LoaderCalculate());
	}

	private class LoaderCalculate implements LoaderManager.LoaderCallbacks<DataInfo> {

        @Override
        public Loader<DataInfo> onCreateLoader(int id, Bundle bundle) {
//			swipeLayout.setRefreshing(true);
			swipeLayout.post(new Runnable() {
				@Override
				public void run() {
					swipeLayout.setRefreshing(true);
				}
			});
             return new DataLoader(getActivity(), DataLoader.CALC_RATE, null, getMediator().getUnitFacade());
        }

        @Override
        public void onLoadFinished(Loader<DataInfo> dataInfoLoader, DataInfo dataInfo) {
    		getLoaderManager().initLoader(CarLogbook.LoaderDesc.REP_FUEL_RATE_ID,
				null, FuelRateFragment.this);

        }

        @Override
        public void onLoaderReset(Loader<DataInfo> dataInfoLoader) {

        }
    }

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Long carId = DBUtils.getActiveCarId(getActivity().getContentResolver());
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ProviderDescriptor.FuelRateView.CONTENT_URI, null, DBUtils.CAR_SELECTION_RATE,
				new String[] {String.valueOf(carId)}, null);

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        showProgress(false);
		adapter.swapCursor(data);
        showNoItems(data.getCount() == 0);
		swipeLayout.setRefreshing(false);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

}


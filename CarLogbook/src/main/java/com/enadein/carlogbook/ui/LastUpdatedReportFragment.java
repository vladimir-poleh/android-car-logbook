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
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.SimpleReportAdapter;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.bean.ReportItem;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.BaseReportFragment;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.DataLoader;

import java.util.ArrayList;

public class LastUpdatedReportFragment extends BaseReportFragment implements LoaderManager.LoaderCallbacks<DataInfo>,CarChangedListener {
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.updates_report, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView) view.findViewById(R.id.list);

	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}


	@Override
	public void onResume() {
		super.onResume();
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_LAST_EVENTS_ID, null, this);
		getMediator().showCarSelection(this);
	}


	@Override
	public Loader<DataInfo> onCreateLoader(int id, Bundle args) {
		return new DataLoader(getActivity(), DataLoader.LAST_EVENTS, getMediator().getUnitFacade());
	}

	@Override
	public void onLoadFinished(Loader<DataInfo> loader, DataInfo data) {
		ArrayList<ReportItem> items = data.getReportData();
		SimpleReportAdapter adapter = new SimpleReportAdapter(getActivity(),
				R.layout.report_item_simple, items.toArray(new ReportItem[] {}), getMediator().getUnitFacade());
		listView.setAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<DataInfo> loader) {
		listView.setAdapter(null);
	}

	@Override
	public void selectMenuItem(Menu menu) {
		menu.findItem(R.id.menu_last_events).setIcon(R.drawable.stat);
	}

	@Override
	public void onCarChanged(long id) {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_LAST_EVENTS_ID, null, this);
	}
}

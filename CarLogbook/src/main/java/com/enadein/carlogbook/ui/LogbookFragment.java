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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.LogAdapter;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.MenuEnabler;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;


public class LogbookFragment extends BaseFragment  implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private LogAdapter adapter;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.logbook_fragment, container, false);
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		adapter = new LogAdapter(getActivity(), null, getMediator().getUnitFacade());

		ListView carListView = (ListView) view.findViewById(R.id.list);
		carListView.setAdapter(adapter);

		carListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				int type = DBUtils.getLogTypeById(getActivity().getContentResolver(), id);
				getMediator().showModifyLog(type, id);
			}
		});

		getLoaderManager().initLoader(CarLogbook.LoaderDesc.LOG_ID, null, this);
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_log);
	}

	@Override
	public MenuEnabler getMenuEnabler() {
		MenuEnabler menuEnabler = new MenuEnabler();
		menuEnabler.setAddLog(true);
		menuEnabler.setAddFuelLog(true);

		return menuEnabler;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		long carId  = DBUtils.getActiveCarId(getActivity().getContentResolver());
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ProviderDescriptor.LogView.CONTENT_URI, null, ProviderDescriptor.Log.Cols.CAR_ID + " = ?",  new String[] {String.valueOf(carId)}, ProviderDescriptor.Log.Cols.DATE + " DESC, " + ProviderDescriptor.Log.Cols.ODOMETER + " DESC");

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}
}

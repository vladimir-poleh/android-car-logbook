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
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.LogAdapter;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


public class LogbookFragment extends BaseFragment  implements
		LoaderManager.LoaderCallbacks<Cursor>,CarChangedListener {
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
				hideMultiAction();
				getMediator().showModifyLog(type, id);
			}
		});


		final View floatingAdd = view.findViewById(R.id.add);

		if (floatingAdd != null) {
			floatingAdd.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					View groupView = getView().findViewById(R.id.add_group);
					if (groupView.getVisibility() == View.GONE) {
						groupView.setVisibility(View.VISIBLE);
					} else {
						groupView.setVisibility(View.GONE);
					}
				}
			});
			view.findViewById(R.id.add_fuel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getMediator().showAddFuelLog();
					hideMultiAction();
				}
			});

			view.findViewById(R.id.add_other).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getMediator().showAddLog();
					hideMultiAction();
				}
			});
		} else {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			view.findViewById(R.id.add_fuel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getMediator().showAddFuelLog();
					hideMultiAction();
				}
			});

			view.findViewById(R.id.add_other).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					getMediator().showAddLog();
					hideMultiAction();
				}
			});
		}

	}

	private void hideMultiAction() {
		if (getView().findViewById(R.id.add) == null) {
			FloatingActionsMenu fam = (FloatingActionsMenu) getView().findViewById(R.id.multiple_actions);
			fam.collapse();
		} else {
			getView().findViewById(R.id.add_group).setVisibility(View.GONE);
		}
	}

	@Override
    public void onResume() {
        super.onResume();

		getMediator().showCarSelection(this);
        getLoaderManager().restartLoader(CarLogbook.LoaderDesc.LOG_ID, null, this);
    }

    @Override
	public String getSubTitle() {
		return getString(R.string.menu_item_log);
	}

//	@Override
//	public MenuEnabler getMenuEnabler() {
//		MenuEnabler menuEnabler = new MenuEnabler();
//
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//			menuEnabler.setAddLog(true);
//			menuEnabler.setAddFuelLog(true);
//		}
//
//		return menuEnabler;
//	}

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
        showNoItems(data.getCount() == 0);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
	}

	@Override
	public void onCarChanged(long id) {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.LOG_ID, null, this);
	}
}

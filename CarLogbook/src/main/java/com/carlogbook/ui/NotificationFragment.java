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
package com.carlogbook.ui;

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

import com.carlogbook.CarLogbook;
import com.carlogbook.R;
import com.carlogbook.adapter.NotificationAdapter;
import com.carlogbook.core.BaseFragment;
import com.carlogbook.core.MenuEnabler;
import com.carlogbook.db.CommonUtils;
import com.carlogbook.db.ProviderDescriptor;

public class NotificationFragment extends BaseFragment implements
		LoaderManager.LoaderCallbacks<Cursor>  {
	private NotificationAdapter notificationAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.notification_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		notificationAdapter = new NotificationAdapter(getActivity(), null);
		ListView notifyListView = (ListView) view.findViewById(R.id.list);
		notifyListView.setAdapter(notificationAdapter);


		notifyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				getMediator().showEditNotifcation(id);
//				CommonUtils.createNotify(getActivity(),id);
			}
		});

		getLoaderManager().initLoader(CarLogbook.LoaderDesc.NOTIFY_ID, null, this);
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_notifications);
	}

	@Override
	public MenuEnabler getMenuEnabler() {
		MenuEnabler menuEnabler = new MenuEnabler();
		menuEnabler.setNotification(true);

		return menuEnabler;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ProviderDescriptor.Notify.CONTENT_URI, null, null, null, null);

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		notificationAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		notificationAdapter.swapCursor(null);
	}
}

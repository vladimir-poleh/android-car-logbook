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

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.carlogbook.R;
import com.carlogbook.core.BaseFragment;

public class ReportsFramgent extends BaseFragment implements ActionBar.OnNavigationListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.reports_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		SpinnerAdapter reportItemsAdapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.action_list, android.R.layout.simple_spinner_dropdown_item);

		getMediator().setListNavigationCallbacks(reportItemsAdapter, this);
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}

	@Override
	public boolean onNavigationItemSelected(int i, long l) {
		return false;
	}
}

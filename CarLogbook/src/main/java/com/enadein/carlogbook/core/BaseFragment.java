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
package com.enadein.carlogbook.core;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.enadein.carlogbook.R;

public class BaseFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	public CarLogbookMediator getMediator() {
		return ((BaseActivity)getActivity()).getMediator();
	}

	public String getSubTitle() {
		return null; /* default */
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getMediator().setBarSubTitle(getSubTitle());

	}


    public void showNoItems(boolean show) {
        getView().findViewById(R.id.no_items).setVisibility(show ? View.VISIBLE : View.GONE);
    }

}

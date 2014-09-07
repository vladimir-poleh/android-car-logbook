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


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseFragment;

public class ImportExportFragment extends BaseFragment {


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.import_export, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.exportBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showExport();
			}
		});

		view.findViewById(R.id.importBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showImport();
			}
		});

		view.findViewById(R.id.backUp).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showBackup();
			}
		});
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_import_export);
	}
}

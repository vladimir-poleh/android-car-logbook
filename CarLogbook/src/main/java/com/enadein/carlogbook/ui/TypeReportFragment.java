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

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.SimpleReportAdapter;
import com.enadein.carlogbook.bean.DataInfo;
import com.enadein.carlogbook.bean.ReportItem;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.DataLoader;
import com.enadein.carlogbook.db.CommonUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TypeReportFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<DataInfo> {
	public static final String DATE_PICKER = "date_picker";
	private long from = 0;
	private long to = 0;

	private ListView listView;
	private TextView fromLabel;
	private TextView toLabel;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		return inflater.inflate(R.layout.type_report, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listView = (ListView) view.findViewById(R.id.list);
		view.findViewById(R.id.dateFrom).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showFrom();
			}
		});


		view.findViewById(R.id.dateTo).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showTo();
			}
		});

		view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				from = 0;
				to = 0;
				updateViews();
				restart();
			}
		});

		fromLabel = (TextView) view.findViewById(R.id.fromLabel);
		toLabel = (TextView) view.findViewById(R.id.toLabel);
		updateViews();
	}

	private void updateViews() {
		String fromStr = getString(R.string.not_set);
		String toStr =  getString(R.string.not_set);
		if (from > 0) {
			fromStr = CommonUtils.formatDate(new Date(from));
		}
		fromLabel.setText(fromStr);

		if (to > 0) {
			toStr = CommonUtils.formatDate(new Date(to));
		}
		toLabel.setText(toStr);
	}

	public void restart() {
		getLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_BY_TYPE_ID, getRangeParams(), this);
	}


	@Override
	public void onResume() {
		super.onResume();
		Bundle params = getRangeParams();
		getLoaderManager().initLoader(CarLogbook.LoaderDesc.REP_BY_TYPE_ID, params, this);
	}

	private Bundle getRangeParams() {
		Bundle params = new Bundle();
		params.putLong(DataLoader.FROM, from);
		params.putLong(DataLoader.TO, to);
		return params;
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}

	@Override
	public Loader<DataInfo> onCreateLoader(int id, Bundle args) {
		return new DataLoader(getActivity(), DataLoader.TYPE, args);
	}

	@Override
	public void onLoadFinished(Loader<DataInfo> loader, DataInfo data) {
		ArrayList<ReportItem> items = data.getReportData();
		SimpleReportAdapter adapter = new SimpleReportAdapter(getActivity(),
				R.layout.report_item_simple, items.toArray(new ReportItem[] {}));
		listView.setAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<DataInfo> loader) {
		listView.setAdapter(null);
	}

	private void showFrom() {
			DatePickerFragment datePickerFragment = new DatePickerFragment();

		datePickerFragment.setListener(new Date(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				from = convertDateToLong(year, month, day, true);
				updateViews();
				restart();
			}
		});



			datePickerFragment.show(getFragmentManager(), DATE_PICKER);
	}

	private void showTo() {
		DatePickerFragment datePickerFragment = new DatePickerFragment();

		datePickerFragment.setListener(new Date(), new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int day) {
				to = convertDateToLong(year, month, day, false);
				updateViews();
				restart();
			}
		});
		datePickerFragment.show(getFragmentManager(), DATE_PICKER);
	}


	private long convertDateToLong( int year, int month, int day, boolean trunk) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);

		if (trunk) {
			CommonUtils.trunkDay(c);
		} else {
			c.set(Calendar.AM_PM, 1);
			c.set(Calendar.HOUR, 11);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
		}

		return  c.getTime().getTime();
	}
}

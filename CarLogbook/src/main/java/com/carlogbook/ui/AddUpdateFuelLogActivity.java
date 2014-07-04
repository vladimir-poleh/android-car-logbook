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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;
import com.carlogbook.db.DBUtils;
import com.carlogbook.db.ProviderDescriptor;

public class AddUpdateFuelLogActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>  {
	public static final int LOADER_TYPE = 102;
	public static final int LOADER_STATION = 103;

	private SimpleCursorAdapter fuelAdapter;
	private SimpleCursorAdapter stationAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_fuel_log);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		Spinner fuelTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		Spinner stationSpinner = (Spinner) findViewById(R.id.stationSpinner);
		String[] adapterCols = new String[]{ProviderDescriptor.DataValue.Cols.NAME};
		int[] adapterRowViews = new int[]{android.R.id.text1};
		fuelAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
				null, adapterCols, adapterRowViews, 0);
		fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fuelTypeSpinner.setAdapter(fuelAdapter);

		stationAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item,
				null, adapterCols, adapterRowViews, 0);
		stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stationSpinner.setAdapter(stationAdapter);

		getSupportLoaderManager().initLoader(LOADER_STATION, null, this);
		getSupportLoaderManager().initLoader(LOADER_TYPE, null, this);
	}



	@Override
	public String getSubTitle() {
		return getString(R.string.log_fuel_title);
	}

	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "date_picker");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] queryCols = new String[]{ProviderDescriptor.DataValue.Cols._ID,
				ProviderDescriptor.DataValue.Cols.NAME};

		CursorLoader cursorLoader = null;

		switch (id) {
			case LOADER_STATION: {
				cursorLoader = new CursorLoader(this,
						ProviderDescriptor.DataValue.CONTENT_URI,  queryCols,
						"TYPE = ?", new String[] {String.valueOf(ProviderDescriptor.DataValue.Type.STATION)}, null);
				break;
			}
			case LOADER_TYPE: {
				cursorLoader = new CursorLoader(this,
						ProviderDescriptor.DataValue.CONTENT_URI,  queryCols,
						"TYPE = ?", new String[] {String.valueOf(ProviderDescriptor.DataValue.Type.FUEL)}, null);

				break;
			}
		}

		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		//TODO need to refactor 'set default value'
		if (loader.getId() == LOADER_TYPE) {
			fuelAdapter.swapCursor(data);
			long defaultId = DBUtils.getDefaultId(getContentResolver(),
					ProviderDescriptor.DataValue.Type.FUEL);

			Spinner fuelTypeSpinner = (Spinner) findViewById(R.id.typeSpinner);
			fuelTypeSpinner.setSelection(getPositionFromAdapterById(fuelAdapter, defaultId));

		} else {
			stationAdapter.swapCursor(data);
			long defaultId = DBUtils.getDefaultId(getContentResolver(),
					ProviderDescriptor.DataValue.Type.STATION);

			Spinner stationSpinner = (Spinner) findViewById(R.id.stationSpinner);
			stationSpinner.setSelection(getPositionFromAdapterById(stationAdapter, defaultId));
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == LOADER_TYPE) {
			fuelAdapter.swapCursor(null);
		} else {
			stationAdapter.swapCursor(null);
		}
	}

	private int getPositionFromAdapterById(SimpleCursorAdapter adapter, long id) {
		int position = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			long currentId = adapter.getItemId(i);
			if (currentId == id) {
				position = i;
				break;
			}
		}
		return position;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_save: {

				break;
			}

			case R.id.action_delete: {
				break;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}
}

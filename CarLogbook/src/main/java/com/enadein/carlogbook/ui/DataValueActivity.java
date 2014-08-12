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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.DataValueAdapter;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class DataValueActivity extends BaseActivity  implements
		LoaderManager.LoaderCallbacks<Cursor>  {

	private int type = ProviderDescriptor.DataValue.Type.FUEL;

	private DataValueAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_value);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		adapter = new DataValueAdapter(this, null);

		ListView valueListView = (ListView) findViewById(R.id.list);
		valueListView.setAdapter(adapter);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			type = params.getInt(BaseActivity.TYPE_KEY);
		}

		valueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				getMediator().showUpdateDataValue(type, id);
			}
		});


		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_create: {
				getMediator().showAddDataValue(type);
				break;
			}

			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}

	@Override
	public String getSubTitle() {
		return (type == ProviderDescriptor.DataValue.Type.FUEL) ?
				getString(R.string.sett_fuel_type):
				getString(R.string.sett_stations);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = new CursorLoader(this,
				ProviderDescriptor.DataValue.CONTENT_URI, null, "type = ?",  new String[] {String.valueOf(type)}, null);

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


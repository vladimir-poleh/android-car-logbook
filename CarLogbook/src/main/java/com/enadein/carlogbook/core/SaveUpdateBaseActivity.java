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
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.ui.ConfirmDialog;

abstract public class SaveUpdateBaseActivity extends BaseActivity {
	public static final String ID_PARAM = "_id = ?";
	protected int mode = -1;
	protected long id = -1;


	@Override
	public void setContent() {
		setContentView(getContentLayout());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle params = getIntent().getExtras();
		if (params != null) {
			mode = params.getInt(BaseActivity.MODE_KEY);
			id = params.getLong(BaseActivity.ENTITY_ID);
			populateExtraParams(params);
		}



		ActionBar actionBar = getSupportActionBar();

		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
		}
		postCreate();
		populateEntity();
		postPopulate();
	}

	protected void postPopulate() {

	}

	protected void populateExtraParams(Bundle params) {

	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_save: {
				if (validateEntity()) {
					createOrUpdateEntity();
					upToParent();
				}
				break;
			}

			case R.id.action_delete: {
				preDelete();
				break;
			}
			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}

	protected void createOrUpdateEntity() {
		if (mode == PARAM_EDIT) {
			updateEntity();
		} else {
			createEntity();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.save_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_delete).setVisible(mode == BaseActivity.PARAM_EDIT);
		return super.onPrepareOptionsMenu(menu);
	}

	protected void upToParent() {
		NavUtils.navigateUpFromSameTask(this);
	}

	protected  void populateEntity() {
		if (mode == PARAM_EDIT) {
			populateEditEntity();
		} else {
			populateCreateEntity();
		}
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (ConfirmDialog.REQUEST_CODE_CONFIRM == requestCode) {
			deleteEntity();
		}
	}

	protected abstract boolean validateEntity();

	protected abstract void createEntity();
	protected abstract void updateEntity();

	protected abstract void preDelete();
	protected abstract void deleteEntity();

	protected abstract void populateEditEntity();

	protected abstract void populateCreateEntity();

	protected abstract void postCreate();

	protected abstract int getContentLayout();
}

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
package com.carlogbook.core;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;

import com.carlogbook.ui.DialogListener;

public class BaseActivity extends ActionBarActivity implements DialogListener {
	public static final String MODE_KEY = "mode";
	public static final String ENTITY_ID = "entity_id";

	protected CarLogbookMediator mediator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mediator = new CarLogbookMediator(this);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		String subTitle = getSubTitle();
		setSubTitle(subTitle);
	}

	public void setSubTitle(String subTitle) {
		if (subTitle != null) {
			getSupportActionBar().setSubtitle(subTitle);
		}
	}

	public CarLogbookMediator getMediator() {
		return mediator;
	}

	public String getSubTitle() {
		return null;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home: {
				Intent upIntent = NavUtils.getParentActivityIntent(this);
				if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
					TaskStackBuilder.create(this)
							.addNextIntentWithParentStack(upIntent)
							.startActivities();
				} else {
					//upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					NavUtils.navigateUpTo(this, upIntent);
				}
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {

	}
}

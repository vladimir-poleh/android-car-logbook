package com.enadein.carlogbook.core;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;

import com.enadein.carlogbook.R;

abstract public class BaseReportFragment extends BaseFragment {

	public abstract void selectMenuItem(Menu menu);

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
		selectMenuItem(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_dashboard: {
				getMediator().clearCarSelection();
				getMediator().showReports();
				break;
			}
			case R.id.menu_by_type: {
				getMediator().clearCarSelection();
				getMediator().showByTypeReport();
				break;
			}
			case R.id.menu_fuel_rate: {
				getMediator().clearCarSelection();
				getMediator().showFuelRate();
				break;
			}
			case R.id.menu_last_events: {
				getMediator().clearCarSelection();
				getMediator().showLastUpdate();
				break;
			}
			case R.id.menu_detail: {
				getMediator().clearCarSelection();
				getMediator().showDetailedReport();
				break;
			}
			case android.R.id.home: {
				Intent upIntent = NavUtils.getParentActivityIntent(getActivity());
				if (NavUtils.shouldUpRecreateTask(getActivity(), upIntent)) {
					TaskStackBuilder.create(getActivity())
							.addNextIntentWithParentStack(upIntent)
							.startActivities();
				} else {
					//upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
					NavUtils.navigateUpTo(getActivity(), upIntent);
				}
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

}

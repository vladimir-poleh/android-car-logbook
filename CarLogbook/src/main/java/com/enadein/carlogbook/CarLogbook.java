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
package com.enadein.carlogbook;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.enadein.carlogbook.adapter.MenuAdapter;
import com.enadein.carlogbook.adapter.MenuItem;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.Logger;
import com.enadein.carlogbook.db.CommonUtils;

import java.util.ArrayList;


public class CarLogbook extends BaseActivity implements BillingProcessor.IBillingHandler {
	public static final String VERSION = "2.5.0";

	public static final String ROTATE = "rotate";
	private Logger log = Logger.createLogger(getClass());
	private DrawerLayout drawer;

	private ListView menuList;
	private ActionBarDrawerToggle toggle;
	private boolean rotate = false;

	///In-App Billing v3
	private static final String LIC_KEY = "";
	public static final String PRODUCT_1 = "";
	public static final String PRODUCT_2 = "";
	public static final String PRODUCT_3 = "";


	private BillingProcessor bp;
	///In-App Billing v3

	private MenuItem carName;
	private ArrayList<MenuItem> items = new ArrayList<MenuItem>();
//	private Spinner reports;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		menuList = (ListView) findViewById(R.id.menu);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		if (savedInstanceState != null) {
			rotate = savedInstanceState.getBoolean(ROTATE);
		}

		MenuAdapter menuAdapter = new MenuAdapter(this, 0, buildMenu());
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new DrawerItemClickListener());
		initDrawer();


		if (!rotate) {
			mediator.showLogbook();
		}


		ArrayAdapter reportItemsAdapter = ArrayAdapter
				.createFromResource(CarLogbook.this,
						R.array.action_list, android.R.layout.simple_spinner_item);
		reportItemsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);


		rotate = false;
//		CommonUtils.createNotify(this, 3, R.drawable.notify);
//		CommonUtils.createNotify(this, 2, R.drawable.not_date);
//		CommonUtils.createNotify(this, 4, R.drawable.notify);
		//TODO
		//initInAppBuilingV3();
	}

	@Override
	public void setContent() {
		setContentView(R.layout.main);
	}

	private void initDrawer() {
		toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		toggle.setDrawerIndicatorEnabled(true);
		drawer.setDrawerListener(toggle);
	}

	private void initInAppBuilingV3() {
		///In-App Billing v3
		bp = new BillingProcessor(this, LIC_KEY, this);
		getMediator().setBp(bp);
		///In-App Billing v3
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(ROTATE, true);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.syncState();
	}


	public MenuItem[] buildMenu() {
		Resources res = getResources();
		String name = getMediator().getUnitFacade().getCarName();
		carName = new MenuItem(0, name, MenuItem.HEADER);
		items.add(carName);
		items.add(new MenuItem(R.drawable.log, res.getString(R.string.menu_item_log)));
		items.add(new MenuItem(R.drawable.stat, res.getString(R.string.menu_item_reports)));
		items.add(new MenuItem(R.drawable.notify, res.getString(R.string.menu_item_notifications)));
		items.add(new MenuItem(R.drawable.cars, res.getString(R.string.menu_item_my_cars)));
		items.add(new MenuItem(R.drawable.calc, res.getString(R.string.calc)));
		items.add(new MenuItem(R.drawable.backup, res.getString(R.string.menu_item_import_export)));
		items.add(new MenuItem(R.drawable.sett, res.getString(R.string.menu_item_settings)));
		items.add(new MenuItem(R.drawable.info, res.getString(R.string.menu_item_about)));

		return items.toArray(new MenuItem[]{});

	}


	class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			selectItem(position);
			menuList.setItemChecked(position, true);

			drawer.closeDrawer(menuList);
		}
	}

	private void selectItem(int position) {
		getMediator().hideCarSelection();

		switch (position) {

			case MenuAdapter.MenuDescriptor.LOG_POSITION: {
				mediator.showLogbook();
				break;
			}
			case MenuAdapter.MenuDescriptor.REPORTS_POSITION: {
				mediator.showReports();

				break;
			}
			case MenuAdapter.MenuDescriptor.MY_CARS_POSITION: {
				mediator.showMyCars();
				break;
			}
			case MenuAdapter.MenuDescriptor.NOTIFICATIONS_POSITION: {
				mediator.showNotifications();
				break;
			}
			case MenuAdapter.MenuDescriptor.CALC: {
				mediator.showCalc();
				break;
			}
			case MenuAdapter.MenuDescriptor.IMPORT_EXPORT: {
				mediator.showImportExport();
				break;
			}
			case MenuAdapter.MenuDescriptor.SETTINGS_POSITION: {
				mediator.showSettings();
				break;
			}
			case MenuAdapter.MenuDescriptor.ABOUT_POSITION: {
				mediator.showAbout();
				break;
			}
		}

	}


	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		boolean result = false;
		int action = item.getItemId();

		switch (action) {
			case android.R.id.home: {
//				NavUtils.navigateUpFromSameTask(this);
				toggleNavigationDrawer(!drawer.isDrawerOpen(menuList));
				result = true;
				break;
			}
		}

		if (action != android.R.id.home) {
			drawer.closeDrawer(menuList);
		}
		return result;
	}


	private void toggleNavigationDrawer(final boolean show) {
		if (show) {
			drawer.openDrawer(menuList);
		} else {
			drawer.closeDrawer(menuList);
		}
	}

	public static class LoaderDesc {
		public static final int CAR_ID = 0;
		public static final int LOG_ID = 1;
		public static final int NOTIFY_ID = 2;

		public static final int REP_DASHBOARD_ID = 10;
		public static final int REP_BY_TYPE_ID = 11;
		public static final int REP_FUEL_RATE_ID = 12;
		public static final int REP_LAST_EVENTS_ID = 13;
		public static final int REP_CALC_FUEL_RATE = 14;
		public static final int REP_DETAILED = 15;
		public static final int CREATE_REPORT = 16;
		public static final int CARS_LOADER = 17;
		public static final int INCOME_LOADER = 18;
		public static final int OTHERS_LOADER = 19;

		private LoaderDesc() {
		}
	}

	///In-App Billing v3
	@Override
	public void onProductPurchased(String productId) {
		getMediator().nofifyPurchased(productId);
		log.debug("Purchased");
	}

	@Override
	public void onPurchaseHistoryRestored() {
		log.debug("Restored");
	}

	@Override
	public void onBillingError(int errorCode, Throwable throwable) {
		log.debug("error " + errorCode);
		getMediator().nofifyBillingError();
	}

	@Override
	public void onBillingInitialized() {
		log.debug("Billing is ok");
		bp.loadOwnedPurchasesFromGoogle();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		log.debug("ON RESULT");
		try {
			if (!bp.handleActivityResult(requestCode, resultCode, data))
				super.onActivityResult(requestCode, resultCode, data);
		} catch (Throwable t) {
			//todo nothing
		}
	}

	@Override
	public void onDestroy() {
		//TODO
		if (bp != null)
			bp.release();
//
		super.onDestroy();
	}
	///In-App Billing v3


	@Override
	public void onCarChanged(long id) {
		String name = getMediator().getUnitFacade().getCarName();
		carName.setName(name);
		((ArrayAdapter) menuList.getAdapter()).notifyDataSetChanged();
	}
}

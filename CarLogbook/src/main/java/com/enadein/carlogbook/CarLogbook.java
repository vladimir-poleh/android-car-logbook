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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.enadein.carlogbook.adapter.MenuAdapter;
import com.enadein.carlogbook.adapter.MenuItem;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.CarChangeListener;
import com.enadein.carlogbook.core.Logger;
import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.ArrayList;
import java.util.Collection;


public class CarLogbook extends BaseActivity implements ActionBar.OnNavigationListener, BillingProcessor.IBillingHandler, CarChangeListener {
	public static final String VERSION = "1.4.1";

    public static final int DASHBOARD_MENU = 0;
	public static final int BY_TYPE_MENU = 1;
	public static final int FUEL_RATE_MENU = 2;
	public static final int LAST_UPDATE_MENU = 3;
	public static final int DETAILED_MENU = 4;

	public static final String ROTATE = "rotate";
	public static final String NAV_MODE = "nav_mode";
	public static final String NAV_REP_POS = "nav_rep_pos";
	private DrawerLayout drawer;
	private ListView menuList;

	private ActionBarDrawerToggle mDrawerToggle;
	private boolean rotate = false;
	private int navMode = ActionBar.NAVIGATION_MODE_STANDARD;
	private int repoNavPos = 0;

	private Logger log = Logger.createLogger(getClass());

	///In-App Billing v3
    private static final String LIC_KEY = "";
    public static final String PRODUCT_1 = "";
    public static final String PRODUCT_2 = "";
    public static final String PRODUCT_3 = "";


	private BillingProcessor bp;
	///In-App Billing v3

	private boolean isDrawerLocked = false;
    MenuItem carName;
    ArrayList<MenuItem> items = new ArrayList<MenuItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSupportProgressBarIndeterminateVisibility(true);
//        setSupportProgressBarIndeterminate(true);
		setContentView(R.layout.main);
		menuList = (ListView) findViewById(R.id.menu);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

//        getMediator().runReportService();

		FrameLayout frameLayout = (FrameLayout)findViewById(R.id.content_frame);
		if(((ViewGroup.MarginLayoutParams)frameLayout.getLayoutParams()).leftMargin == (int)getResources().getDimension(R.dimen.drawer_size)) {
			drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, menuList);
			drawer.setScrimColor(Color.TRANSPARENT);
			isDrawerLocked = true;
		}


		if (savedInstanceState != null) {
			rotate = savedInstanceState.getBoolean(ROTATE);
			navMode = savedInstanceState.getInt(NAV_MODE);

			repoNavPos = savedInstanceState.getInt(NAV_REP_POS);
		}


		MenuAdapter menuAdapter = new MenuAdapter(this, 0, buildMenu());
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new DrawerItemClickListener());
		initDrawer();


		if (!rotate) {
			mediator.showLogbook();
		}

		setupNavMode();

		SpinnerAdapter reportItemsAdapter = ArrayAdapter.createFromResource(this,
				R.array.action_list,R.layout.nav_item);

		getMediator().setListNavigationCallbacks(reportItemsAdapter, this);

		if (navMode == ActionBar.NAVIGATION_MODE_LIST) {
			getSupportActionBar().setSelectedNavigationItem(repoNavPos);
		}

		rotate = false;

		getSupportActionBar().setDisplayHomeAsUpEnabled(!isDrawerLocked);
		getSupportActionBar().setHomeButtonEnabled(!isDrawerLocked);

		initInAppBuilingV3();


        //TEMP
//        requestWindowFeature(Window.FEATURE_PROGRESS);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);


//        setProgressBarIndeterminate(true);
	}

	private void initDrawer() {
		mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.menu_item_log, R.string.menu_item_settings) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getMediator().setDrawerOpenned(false);
				ActivityCompat.invalidateOptionsMenu(CarLogbook.this);
				supportInvalidateOptionsMenu();
				setupNavMode();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getMediator().setDrawerOpenned(true);
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
				ActivityCompat.invalidateOptionsMenu(CarLogbook.this);
				supportInvalidateOptionsMenu();
			}
		};

		if (!isDrawerLocked) {
			drawer.setDrawerListener(mDrawerToggle);
		}
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
		outState.putInt(NAV_MODE, navMode);
		outState.putInt(NAV_REP_POS, getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
			mDrawerToggle.onConfigurationChanged(newConfig);
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
		items.add(new MenuItem(R.drawable.backup, res.getString(R.string.menu_item_import_export)));
		items.add(new MenuItem(R.drawable.sett, res.getString(R.string.menu_item_settings)));
		items.add(new MenuItem(R.drawable.info, res.getString(R.string.menu_item_about)));

        getMediator().getUnitFacade().setCarChangeListener(this);
        return items.toArray(new MenuItem[]{});

	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if (rotate) {
			return true;
		}
		switch (itemPosition) {
			case DASHBOARD_MENU: {
				getMediator().showReports();
				break;
			}
			case BY_TYPE_MENU: {
				getMediator().showByTypeReport();
				break;
			}
			case FUEL_RATE_MENU: {
				getMediator().showFuelRate();
				break;
			}
			case LAST_UPDATE_MENU: {
				getMediator().showLastUpdate();
				break;
			}
            case DETAILED_MENU: {
                getMediator().showDetailedReport();
                break;
            }
		}
		return true;
	}

    @Override
    public void onCarChangeChanged(String car) {
        carName.setName(car);
        MenuAdapter menuAdapter = (MenuAdapter) menuList.getAdapter();
        menuAdapter.notifyDataSetChanged();
//        drawer.invalidate();
    }


    class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			selectItem(position);
			menuList.setItemChecked(position, true);

			if (!isDrawerLocked) {
				drawer.closeDrawer(menuList);
			}
		}
	}

	private void selectItem(int position) {
		navMode = ActionBar.NAVIGATION_MODE_STANDARD;

		switch (position) {
			case MenuAdapter.MenuDescriptor.LOG_POSITION: {
				mediator.showLogbook();
				break;
			}
			case MenuAdapter.MenuDescriptor.REPORTS_POSITION: {
				long carId = DBUtils.getActiveCarId(getContentResolver());
				long logCount = DBUtils.getCount(getContentResolver(), ProviderDescriptor.Log.CONTENT_URI, DBUtils.CAR_SELECTION,
						new String[]{String.valueOf(carId)});

				if (carId != -1 && logCount > 0) {
					navMode = ActionBar.NAVIGATION_MODE_LIST;
					getSupportActionBar().setNavigationMode(navMode);
					repoNavPos = 0;
					getSupportActionBar().setSelectedNavigationItem(repoNavPos);
					mediator.showReports();
				} else {
					mediator.showNoReports();
				}

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

		setupNavMode();

	}

	private void setupNavMode() {
		ActionBar actionBar = getSupportActionBar();

		if (actionBar.getNavigationMode() != navMode) {
			actionBar.setNavigationMode(navMode);
		}
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int action = item.getItemId();

		switch (action) {
			case android.R.id.home: {
//				NavUtils.navigateUpFromSameTask(this);
				toggleNavigationDrawer(!drawer.isDrawerOpen(menuList));
				break;
			}
			case R.id.action_add_car: {
				mediator.showAddCar();
				break;
			}
			case R.id.action_log_fuel: {
				mediator.showAddFuelLog();
				break;
			}
			case R.id.action_log: {
				mediator.showAddLog();
				break;
			}
			case R.id.action_add_notify: {
				mediator.showAddNotification();
				break;
			}
		}

		if (action != android.R.id.home) {
			if (!isDrawerLocked) {
				drawer.closeDrawer(menuList);
			}
		}
		return true;
	}


	private void toggleNavigationDrawer(final boolean show) {
		if (show) {
			drawer.openDrawer(menuList);
		} else {
			drawer.closeDrawer(menuList);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		android.view.MenuItem shareItem = menu.findItem(R.id.action_share);
		ShareActionProvider shareActionProvider = (ShareActionProvider)
				MenuItemCompat.getActionProvider(shareItem);
		shareActionProvider.setShareIntent(getTextShareIntent());

		return super.onCreateOptionsMenu(menu);
	}

	private Intent getTextShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));

		return intent;
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
		if (bp != null)
			bp.release();

		super.onDestroy();
	}
	///In-App Billing v3
}

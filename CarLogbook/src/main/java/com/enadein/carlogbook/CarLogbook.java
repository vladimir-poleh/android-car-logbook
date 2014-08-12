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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.enadein.carlogbook.adapter.MenuAdapter;
import com.enadein.carlogbook.adapter.MenuItem;
import com.enadein.carlogbook.core.BaseActivity;

import java.util.ArrayList;
import java.util.Collection;


public class CarLogbook extends BaseActivity implements ActionBar.OnNavigationListener {
	private DrawerLayout drawer;
	private ListView menuList;

	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.e("xxxx", "x" + savedInstanceState);

		FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

		MenuAdapter menuAdapter = new MenuAdapter(this, 0,  buildMenu());
		menuList = (ListView) findViewById(R.id.menu);
		menuList.setAdapter(menuAdapter);
		menuList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.menu_item_log, R.string.menu_item_settings) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getMediator().setDrawerOpenned(false);
				ActivityCompat.invalidateOptionsMenu(CarLogbook.this);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getMediator().setDrawerOpenned(true);
				ActivityCompat.invalidateOptionsMenu(CarLogbook.this);
			}
		};

		drawer.setDrawerListener(mDrawerToggle);
		mediator.showLogbook();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.e("xxxx", "222x" );
		outState.putString("xxx", "Jeee");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		String xx = savedInstanceState.getString("xx");
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
		Collection<MenuItem> items = new ArrayList<MenuItem>();

		Resources res = getResources();

		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_log)));
		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_reports)));
		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_notifications)));
		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_my_cars)));
		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_settings)));
		items.add(new MenuItem(R.drawable.ic_launcher, res.getString(R.string.menu_item_about)));

		return items.toArray(new MenuItem[] {});
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return true;
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
		int navMode = ActionBar.NAVIGATION_MODE_STANDARD;

		switch (position) {
			case MenuAdapter.MenuDescriptor.LOG_POSITION: {
				mediator.showLogbook();
				break;
			}
			case MenuAdapter.MenuDescriptor.REPORTS_POSITION: {
				navMode = ActionBar.NAVIGATION_MODE_LIST;
				getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
				SpinnerAdapter reportItemsAdapter = ArrayAdapter.createFromResource(this,
						R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
				getMediator().setListNavigationCallbacks(reportItemsAdapter, this);
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
			case MenuAdapter.MenuDescriptor.SETTINGS_POSITION: {
				mediator.showSettings();
				break;
			}
			case MenuAdapter.MenuDescriptor.ABOUT_POSITION: {
				mediator.showAbout();
				break;
			}
		}

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
			case  R.id.action_add_car: {
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
			drawer.closeDrawer(menuList);
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
	}
}

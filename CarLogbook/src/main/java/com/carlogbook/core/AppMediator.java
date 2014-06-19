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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.widget.SpinnerAdapter;

import com.carlogbook.R;

public class AppMediator {
	protected ActionBarActivity activity;
	protected FragmentManager fragmentManager;
	protected ActionBar actionBar;

	public static boolean transitionsEnable = false;

	public AppMediator(ActionBarActivity activity) {
		this.activity = activity;
		fragmentManager = activity.getSupportFragmentManager();
		actionBar = activity.getSupportActionBar();
	}

	public static void setTransitionsEnable(boolean transitionsEnable) {
		AppMediator.transitionsEnable = transitionsEnable;
	}

	private void setUpAnimation(FragmentTransaction transaction) {
		if (AppMediator.transitionsEnable) {
			transaction.setCustomAnimations(R.anim.in_left, R.anim.out_left, R.anim.in_right, R.anim.out_right);
		}
	}

	public void addFragment(int containterViewId, BaseFragment fragment, String tag, boolean backStack, String backStackName) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		setUpAnimation(transaction);

		if (tag != null) {
			transaction.add(containterViewId, fragment, tag);
		} else {
			transaction.add(containterViewId, fragment);
		}

		if (backStack) {
			transaction.addToBackStack(backStackName);
		}

		transaction.commit();
		ActivityCompat.invalidateOptionsMenu(activity);
	}

	public void addFragment(int containterViewId, String tag, BaseFragment fragment, boolean backStack) {
		addFragment(containterViewId, fragment, tag, backStack,  null);
	}

	public void addFragment(int containterViewId, String tag, BaseFragment fragment) {
		addFragment(containterViewId, fragment, tag, false,  null);
	}

	public void addFragment(int containterViewId, BaseFragment fragment) {
		addFragment(containterViewId, fragment, null, false,  null);
	}

	public void replaceFragment(int containterViewId, BaseFragment fragment, String tag, boolean backStack, String backStackName) {
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		setUpAnimation(transaction);

		if (tag != null) {
			transaction.replace(containterViewId, fragment, tag);
		} else {
			transaction.replace(containterViewId, fragment);
		}

		if (backStack) {
			transaction.addToBackStack(backStackName);
		}

		transaction.commit();
		ActivityCompat.invalidateOptionsMenu(activity);
	}

	public void replaceFragment(int containterViewId, String tag, BaseFragment fragment, boolean backStack) {
		replaceFragment(containterViewId, fragment, tag, backStack, null);
	}

	public void replaceFragment(int containterViewId, String tag, BaseFragment fragment) {
		replaceFragment(containterViewId, fragment, tag, false, null);
	}

	public void replaceFragment(int containterViewId, BaseFragment fragment) {
		replaceFragment(containterViewId, fragment, null, false, null);
	}

	public void replaceMainContainter(BaseFragment fragment) {
		fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		replaceFragment(getMainContainerId(), fragment);
	}

	public int getMainContainerId() {
		return R.id.content_frame;
	}

	public void setBarSubTitle(String title) {
		actionBar.setSubtitle(title);
	}

	protected void startActivity(Class<?> cls){
		startActivity(cls, null);
	}

	protected void startActivity(Class<?> cls, Bundle extras){
		Intent intent = new Intent(activity, cls);

		if (extras != null) {
			intent.replaceExtras(extras);
		}

		activity.startActivity(intent);
	}

	protected void startActivityForResult(Class<?> cls, int requestCode) {
		startActivityForResult(cls, null, requestCode);
	}

	protected void startActivityForResult(Class<?> cls, Bundle extras, int requestCode){
		Intent intent = new Intent(activity, cls);

		if (extras != null) {
			intent.replaceExtras(extras);
		}

		(activity).startActivityForResult(intent, requestCode);
	}

	public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener listener) {
		actionBar.setListNavigationCallbacks(adapter, listener);
	}

}

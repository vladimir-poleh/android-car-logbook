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

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;

public class LicActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lic);
		setSubTitle(getString(R.string.license_title));

		WebView wv = (WebView) findViewById(R.id.webview);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setUseWideViewPort(true);
		wv.loadUrl("file:///android_asset/lic.html");

	}

	public void setSubTitle(String subTitle) {
		if (subTitle != null) {
			getSupportActionBar().setSubtitle(subTitle);
		}
	}

}

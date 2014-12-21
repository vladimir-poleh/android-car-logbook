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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseFragment;
import com.enadein.carlogbook.core.PurchasedListener;

public class AboutFragment extends BaseFragment implements PurchasedListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.about_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView linkSrc = (TextView) view.findViewById(R.id.link_src);
		linkSrc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openUrl(getString(R.string.open_src_url));
			}
		});

		view.findViewById(R.id.lib1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openUrl(getString(R.string.lib1_val_url));
			}
		});

		view.findViewById(R.id.link_ask).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendEmail(getString(R.string.email_subject_ask));
			}
		});

		view.findViewById(R.id.link_provide).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sendEmail(getString(R.string.email_subject_provide));
			}
		});

		view.findViewById(R.id.lic).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMediator().showLic();
			}
		});

		view.findViewById(R.id.link_rate).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				rateApp();
			}
		});
		view.findViewById(R.id.don1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				donate(CarLogbook.PRODUCT_1, AboutFragment.this);
			}
		});
		view.findViewById(R.id.don2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				donate(CarLogbook.PRODUCT_2, AboutFragment.this);
			}
		});

		view.findViewById(R.id.don3).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				donate(CarLogbook.PRODUCT_3, AboutFragment.this);
			}
		});

        TextView versionView = (TextView)view.findViewById(R.id.ver);
        versionView.setText(versionView.getText() + ": " + CarLogbook.VERSION);
	}

	private void donate(String prodId, PurchasedListener listener) {
		getMediator().consumePurchase(prodId, listener);
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_about);
	}

	private void openUrl(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	public void sendEmail(String subject) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto",getString(R.string.email), null));
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);

		startActivity(Intent.createChooser(intent, "Send Email"));
	}

	public void rateApp() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.enadein.carlogbook"));
		startActivity(intent);
	}

	@Override
	public void onProductPurchased(String productId) {
		//getMediator().showAlert(getString(R.string.thanks));
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.share, menu);

		android.view.MenuItem shareItem = menu.findItem(R.id.action_share);
		ShareActionProvider shareActionProvider = (ShareActionProvider)
				MenuItemCompat.getActionProvider(shareItem);
		shareActionProvider.setShareIntent(getTextShareIntent());
		super.onCreateOptionsMenu(menu, inflater);
	}

	private Intent getTextShareIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);

		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_title));
		intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));

		return intent;
	}

	@Override
	public void onError() {
		Log.e("ERORR", "EROOR BILLING");
	}
}

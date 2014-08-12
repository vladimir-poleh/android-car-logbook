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
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.Logger;

public class AlertDialog extends DialogFragment {
	private Logger log = Logger.createLogger(AlertDialog.class);
	public static final String TEXT = "text_key";
	private String text = "Emtpy";


	@Override
	public void onSaveInstanceState(Bundle outState) {
		log.debug("Save state for text " + text);
		outState.putString(TEXT, text);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		log.debug("Restore state for text " + savedInstanceState);
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState == null) {
			return;
		}
		text = savedInstanceState.getString(TEXT);

	}

	public static AlertDialog newInstance() {
		return new AlertDialog();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ConfirmDelete);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		log.debug("saved state " + savedInstanceState);
		View view = inflater.inflate(R.layout.alert_notice, container, false);
		TextView textView = (TextView) view.findViewById(R.id.text);
		if (savedInstanceState != null) {
			text = savedInstanceState.getString(TEXT);
		}
		textView.setText(text);
		Button ok = (Button) view.findViewById(R.id.ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
		return view;
	}
}
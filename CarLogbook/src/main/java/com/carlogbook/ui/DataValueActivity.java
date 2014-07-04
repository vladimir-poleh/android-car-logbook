package com.carlogbook.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;

import com.carlogbook.R;
import com.carlogbook.core.BaseActivity;

public class DataValueActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_no_drawer); //TODO stab
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.create_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {

		int action = item.getItemId();

		switch (action) {
			case R.id.action_create: {

				break;
			}

			default: {
				return super.onOptionsItemSelected(item);
			}
		}

		return true;
	}

}


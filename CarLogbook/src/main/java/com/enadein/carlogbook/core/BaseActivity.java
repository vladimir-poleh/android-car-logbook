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
package com.enadein.carlogbook.core;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;
import com.enadein.carlogbook.ui.DialogListener;

public class BaseActivity extends ActionBarActivity implements DialogListener, QuickCarSelectionAware {
	public static final String MODE_KEY = "mode";
	public static final String TYPE_KEY = "type";
	public static final String ENTITY_ID = "entity_id";
	public static final int PARAM_EDIT = 1;

	public static final String SELECTION_ID_FILTER = "_id = ?";

	protected CarLogbookMediator mediator;

    public long carId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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

	protected boolean validateView(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		double valueDouble = CommonUtils.getRawDouble(editView.getText().toString());
		boolean result = valueDouble > 0.0009 && 1000000.d >= valueDouble;

	errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
	return result;
}

	protected boolean validateFuelVavlView(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		double valueDouble =  CommonUtils.getRawDouble((editView.getText().toString()));
		boolean result = valueDouble > 0.0009 && 1000.d >= valueDouble;

		errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
		return result;
	}


	protected boolean validateOdometer(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		int value = CommonUtils.getOdometerInt(editView);
		boolean result = value > 0 && value < 1000000;

		errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
		return result;
	}

	protected boolean validateTextView(int errorViewId, EditText editView) {
		TextView errorView = (TextView) findViewById(errorViewId);
		boolean result = CommonUtils.isNotEmpty(editView.getText().toString());

		errorView.setVisibility((!result)? View.VISIBLE : View.GONE);
		return result;
	}

	protected void showErrorLayout(int errorViewId, boolean show) {
		TextView errorView = (TextView) findViewById(errorViewId);
		errorView.setVisibility((show)? View.VISIBLE : View.GONE);
	}



	public void showError(int errorViewId, boolean show) {
		TextView errorView = (TextView) findViewById(errorViewId);
		errorView.setVisibility((show)? View.VISIBLE : View.GONE);
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

    @Override
    public void onCarChanged(long carId) {

    }

    ///CAR SELECTION
    public void showSelectCars(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Cursor c = getContentResolver().query(ProviderDescriptor.Car.CONTENT_URI, null, null, null, null);

        builder.setTitle(R.string.select_car_dialog)
                .setCursor(c, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        c.moveToPosition(pos);
                        long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols._ID));
                        onCarChanged(id);
                    }
                }, ProviderDescriptor.Car.Cols.NAME);

         builder.create().show();
    }

    public void updateCarName(long carId) {
        String carName = DBUtils.getActiveCarName(getContentResolver(), carId);
        ((TextView)findViewById(R.id.carName)).setText(carName);
    }

    public void showCarSelection() {
        String enableCarSelection = getMediator().getUnitFacade().getSetting(UnitFacade.SET_CAR_SELECTION, "1");
        boolean show = "1".equals(enableCarSelection);

        long defaultCarId = DBUtils.getActiveCarId(getContentResolver());
        setCarId(defaultCarId);
        updateCarName(defaultCarId);

        findViewById(R.id.carSelection).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public long getCarId() {
        return (carId == -1) ? DBUtils.getActiveCarId(getContentResolver()) : carId;
    }

    public void setCarId(long id) {
        this.carId = id;
    }

    public void updateActiveCar() {
        long activeCarId = DBUtils.getActiveCarId(getContentResolver());
        boolean newCar = activeCarId != carId;

        if (carId != -1 && newCar) {
            DBUtils.selectActivCar(getContentResolver(), carId);
            getMediator().getUnitFacade().reload(carId);

            getMediator().showToast(getString(R.string.car_was_changed) +
                    getMediator().getUnitFacade().getCarName());
        }
    }
}

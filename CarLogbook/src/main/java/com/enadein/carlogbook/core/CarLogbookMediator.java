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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;
import com.enadein.carlogbook.service.ReportCalculationService;
import com.enadein.carlogbook.ui.AboutFragment;
import com.enadein.carlogbook.ui.AddUpdateCarActivity;
import com.enadein.carlogbook.ui.AddUpdateDataValue;
import com.enadein.carlogbook.ui.AddUpdateFuelLogActivity;
import com.enadein.carlogbook.ui.AddUpdateLogActivity;
import com.enadein.carlogbook.ui.AddUpdateNotificationActivity;
import com.enadein.carlogbook.ui.AlertDialog;
import com.enadein.carlogbook.ui.CalcFragment;
import com.enadein.carlogbook.ui.ConfirmDialog;
import com.enadein.carlogbook.ui.CreateReportActivity;
import com.enadein.carlogbook.ui.DataValueActivity;
import com.enadein.carlogbook.ui.DetailReportFragment;
import com.enadein.carlogbook.ui.ExportActivty;
import com.enadein.carlogbook.ui.FuelRateFragment;
import com.enadein.carlogbook.ui.GoogleBackupActivity;
import com.enadein.carlogbook.ui.ImportActivity;
import com.enadein.carlogbook.ui.ImportDialog;
import com.enadein.carlogbook.ui.ImportExportFragment;
import com.enadein.carlogbook.ui.LastUpdatedReportFragment;
import com.enadein.carlogbook.ui.LicActivity;
import com.enadein.carlogbook.ui.LogbookFragment;
import com.enadein.carlogbook.ui.MyCarsFragment;
import com.enadein.carlogbook.ui.NoReportsFragment;
import com.enadein.carlogbook.ui.NotificationFragment;
import com.enadein.carlogbook.ui.ReportsFramgent;
import com.enadein.carlogbook.ui.SettingsFragment;
import com.enadein.carlogbook.ui.TypeReportFragment;

import java.io.File;

public class CarLogbookMediator extends AppMediator {
	public static final String ALERT = "alert";
	public static final String CONFIRM_DELETE = "confirm_delete";
	private boolean drawerOpenned;
	private BillingProcessor bp;
	private PurchasedListener purchasedListener = null;


	public CarLogbookMediator(ActionBarActivity activity) {
		super(activity);

	}

	public CarlogbookApplication getApplication() {
		return (CarlogbookApplication) activity.getApplication();
	}

	public UnitFacade getUnitFacadeDefault() {
		return getApplication().getUnitFacadeDefault();
	}

	public UnitFacade getUnitFacade() {
		return getApplication().getUnitFacade();
	}

	public void showLogbook() {
		replaceMainContainter(new LogbookFragment());
	}


	public void showImportExport() {
		replaceMainContainter(new ImportExportFragment());
	}

    public void showCalc() {
        replaceMainContainter(new CalcFragment());
    }

	public void showReports() {
		replaceMainContainter(new ReportsFramgent());
	}

	public void showByTypeReport() {
		replaceMainContainter(new TypeReportFragment());
	}

	public void showFuelRate() {
		replaceMainContainter(new FuelRateFragment());
	}

	public void showLastUpdate() {
		replaceMainContainter(new LastUpdatedReportFragment());
	}

    public void showDetailedReport() {
        replaceMainContainter(new DetailReportFragment());
    }


	public void showMyCars() {
		replaceMainContainter(new MyCarsFragment());
	}

	public void showNotifications() {
		replaceMainContainter(new NotificationFragment());
	}

	public void showSettings() {
		replaceMainContainter(new SettingsFragment());
	}

	public void showAbout() {
		replaceMainContainter(new AboutFragment());
	}

	public void showAddCar() {
		startActivity(AddUpdateCarActivity.class);
	}


	public void showLic() {
		startActivity(LicActivity.class);
	}

	public void showImport() {
		startActivity(ImportActivity.class);
	}

	public void showBackup() {
		startActivity(GoogleBackupActivity.class);
	}

	public void showExport() {
		startActivity(ExportActivty.class);
	}

	public void showViewCar(long id) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.MODE_KEY, AddUpdateCarActivity.PARAM_EDIT);
		params.putLong(BaseActivity.ENTITY_ID, id);
		startActivity(AddUpdateCarActivity.class, params);
	}

	public void showAddNotification() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateNotificationActivity.class);
		}
	}

	public void showEditNotifcation(long id) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.MODE_KEY, BaseActivity.PARAM_EDIT);
		params.putLong(BaseActivity.ENTITY_ID, id);
		startActivity(AddUpdateNotificationActivity.class, params);
	}

	public void showAddFuelLog() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateFuelLogActivity.class);
		}
	}

	public void showAddLog() {
		if (DBUtils.getActiveCarId(activity.getContentResolver()) == -1) {
			showAlert(activity.getString(R.string.value_car_error));
		} else {
			startActivity(AddUpdateLogActivity.class);
		}
	}

	public void showCreateReport() {
		startActivity(CreateReportActivity.class);
	}

	public void showModifyLog(int type, long id) {
		Bundle params = new Bundle();
		params.putLong(BaseActivity.ENTITY_ID, id);
		params.putInt(BaseActivity.MODE_KEY, AddUpdateFuelLogActivity.PARAM_EDIT);
		Class clazz = (type == ProviderDescriptor.Log.Type.OTHER) ? AddUpdateLogActivity.class : AddUpdateFuelLogActivity.class;
		startActivity(clazz, params);
	}

	public void showConfirmDeleteView() {
		DialogFragment confirmDeleteDialog = ConfirmDialog.newInstance();
		confirmDeleteDialog.show(activity.getSupportFragmentManager(), CONFIRM_DELETE);
	}

	public void showConfirmImport() {
		DialogFragment importDlg = ImportDialog.newInstance();
		importDlg.show(activity.getSupportFragmentManager(), "import");
	}

    public void showToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
    }

	public void showAlert(String text) {
		AlertDialog alertDialog = AlertDialog.newInstance();
		alertDialog.setText(text);
		alertDialog.show(activity.getSupportFragmentManager(), ALERT);
	}

	public void showDataValues(int type) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.TYPE_KEY, type);
		startActivity(DataValueActivity.class, params);
	}

	public void openUrl(File file) {
		if (file != null) {
			Uri uri = Uri.fromFile(file);
//			Uri uri = Uri.parse("http://localhost" + file.getPath());
			Intent intent = new Intent(Intent.ACTION_VIEW);
//			intent.setType("html");
//			intent.putExtra(Intent.EXTRA_MIME_TYPES, "text/html");
//			intent.setData(uri);
			intent.setData(uri);
			intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
//			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			try {
				activity.startActivity(intent);
			}  catch (ActivityNotFoundException e) {
				System.out.println(e);
			}
		} else {
			Toast.makeText(activity, "error", Toast.LENGTH_LONG);
		}
	}

	public void showAddDataValue(int type) {
		Bundle params = new Bundle();
		params.putInt(BaseActivity.TYPE_KEY, type);
		startActivity(AddUpdateDataValue.class, params);
	}

	public void showUpdateDataValue(int type, long id) {
//		if (DBUtils.isDataValueIsSystemById(activity.getContentResolver(), id)) {
//			showAlert(activity.getString(R.string.value_sys_error));
//		} else {
			Bundle params = new Bundle();
			params.putInt(BaseActivity.TYPE_KEY, type);
			params.putInt(BaseActivity.MODE_KEY, AddUpdateDataValue.PARAM_EDIT);
			params.putLong(BaseActivity.ENTITY_ID, id);
			startActivity(AddUpdateDataValue.class, params);
//		}
	}

	public boolean isDrawerOpenned() {
		return drawerOpenned;
	}

	public void setDrawerOpenned(boolean drawerOpenned) {
		this.drawerOpenned = drawerOpenned;
	}

	public void showNoReports() {
		replaceMainContainter(new NoReportsFragment());
	}

	public void setBp(BillingProcessor bp) {
		this.bp = bp;
	}

	public void consumePurchase(String productId, PurchasedListener listener) {
		purchasedListener = listener;
		boolean result = bp.consumePurchase(productId);
		if (result) {
			purchasedListener.onProductPurchased(productId);
		} else {
			bp.purchase(productId);
		}
	}

	public void nofifyPurchased(String productId) {
		if (purchasedListener != null) {
			purchasedListener.onProductPurchased(productId);
		}
	}


	public void nofifyBillingError() {
		if (purchasedListener != null) {
			purchasedListener.onError();
		}
	}

    public void runReportService() {
        Intent intent = new Intent(activity, ReportCalculationService.class);
        activity.startService(intent);
    }
}

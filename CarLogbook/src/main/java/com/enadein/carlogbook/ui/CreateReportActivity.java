package com.enadein.carlogbook.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.androidquery.AQuery;
import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.CarChangedListener;
import com.enadein.carlogbook.core.ReportExportLoader;
import com.enadein.carlogbook.core.gen.GenWriter;

import java.io.File;

public class CreateReportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<File>  {
	public static final String GEN_LOGS = "logs";
	public static final String GEN_INFO = "info";
	public static final String GEN_COST = "cost";
	private ProgressDialog progress;
	private boolean genLogs;
	private boolean genInfo;
	private boolean genCost;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final AQuery a = new AQuery(this);
		a.id(R.id.create).getView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (validate()) {
					startExport();
					a.id(R.id.error).gone();
				} else {
					a.id(R.id.error).visible();
				}
			}
		});


		getMediator().showCarSelection(new CarChangedListener() {
			@Override
			public void onCarChanged(long id) {

			}
		});
	}

	@Override
	public void setContent() {
		setContentView(R.layout.create_report);
	}


	@Override
	public int getCarSelectorViewId() {
		return R.id.carsAdd;
	}

	private boolean validate () {
		AQuery a = new AQuery(this);
		genLogs = a.id(R.id.logs).isChecked();
		genInfo = a.id(R.id.info).isChecked();
		genCost = a.id(R.id.costs).isChecked();

		boolean result = genLogs || genInfo || genCost;

		if (!result) {
			//error
		}

		return result;
	}

	private void startExport() {
		progress = ProgressDialog.show(this, getString(R.string.progress_title),
				getString(R.string.progress_content), true);
		Bundle bundle = new Bundle();
		bundle.putBoolean(GEN_LOGS, genLogs);
		bundle.putBoolean(GEN_INFO, genInfo);
		bundle.putBoolean(GEN_COST, genCost);
		getSupportLoaderManager().restartLoader(CarLogbook.LoaderDesc.REP_DASHBOARD_ID, bundle, this);
	}

	@Override
	public String getSubTitle() {
		return getResources().getString(R.string.create_report);
	}

	@Override
	public Loader<File> onCreateLoader(int i, Bundle bundle) {
		 return new ReportExportLoader(this, bundle, getMediator().getUnitFacade());
	}

	@Override
	public void onLoadFinished(Loader<File> loader, File f) {
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
		getSupportLoaderManager().destroyLoader(CarLogbook.LoaderDesc.REP_DASHBOARD_ID);
		getMediator().openUrl(f);
	}

	@Override
	public void onLoaderReset(Loader<File> loader) {
		if (progress != null) {
			progress.dismiss();
			progress = null;
		}
	}
}

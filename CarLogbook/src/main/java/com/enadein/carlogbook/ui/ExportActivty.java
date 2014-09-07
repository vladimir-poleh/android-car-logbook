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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.backup.ExportImportLoader;
import com.enadein.carlogbook.core.backup.FileUtils;
import com.enadein.carlogbook.core.backup.ImportExportResult;
import com.enadein.carlogbook.core.backup.XMLImportExportStrategy;

import java.util.logging.Handler;

public class ExportActivty extends BaseActivity implements LoaderManager.LoaderCallbacks<ImportExportResult>{
	private EditText nameEditView;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_lw);
		setSubTitle(getString(R.string.export));

		nameEditView = (EditText) findViewById(R.id.name);

		findViewById(R.id.exportBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				validateAndSave();
			}
		});
	}

	public boolean checkExist() {
		return FileUtils.exist(nameEditView.getText().toString(), XMLImportExportStrategy.EXTENSION);
	}

	private void validateAndSave() {
		if (validateTextView(R.id.errorName, nameEditView)) {
			boolean exists = checkExist();
			showErrorLayout(R.id.errorExists, exists);

			if (!exists) {
				doExport();
			}
		}
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (AlertDialog.ALERT_OK == requestCode) {
			NavUtils.navigateUpFromSameTask(this);
		}
	}

	private void doExport() {
		Bundle args = new  Bundle();
		args.putInt(ExportImportLoader.TYPE_KEY, ExportImportLoader.TYPE_EXPORT);
		args.putString(ExportImportLoader.NAME_KEY, nameEditView.getText().toString());
//		getSupportLoaderManager().initLoader(0, args, this);
		progress = ProgressDialog.show(this, getString(R.string.progress_title),
				getString(R.string.progress_content), true);

		getSupportLoaderManager().restartLoader(0, args, this);
	}


	@Override
	public Loader<ImportExportResult> onCreateLoader(int id, Bundle args) {

		return new ExportImportLoader(ExportActivty.this, args);
	}

	@Override
	public void onLoadFinished(Loader<ImportExportResult> loader, ImportExportResult data) {
	    if (data.isDone()) {


		    new android.os.Handler().post(new Runnable() {
			    @Override
			    public void run() {
				    progress.dismiss();
				    getMediator().showAlert(getString(R.string.export_ok));
			    }
		    });
	    } else {
		    new android.os.Handler().post(new Runnable() {
			    @Override
			    public void run() {
				    progress.dismiss();
				    Toast.makeText(getBaseContext(), getString(R.string.export_import_error), Toast.LENGTH_SHORT).show();
			    }
		    });
	    }
	}

	@Override
	public void onLoaderReset(Loader<ImportExportResult> loader) {

	}
}

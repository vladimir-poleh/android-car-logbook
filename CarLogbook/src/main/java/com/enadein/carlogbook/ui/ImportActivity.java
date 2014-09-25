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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.adapter.FileAdapter;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.backup.ExportImportLoader;
import com.enadein.carlogbook.core.backup.FileUtils;
import com.enadein.carlogbook.core.backup.ImportExportResult;
import com.enadein.carlogbook.db.DBUtils;

import java.io.File;

public class ImportActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<ImportExportResult> {
	private ListView list;
	private FileAdapter fileAdapter;
	private File selectedFile = null;

	private CheckBox deleteView;
	private ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.import_lw);
		setSubTitle(getString(R.string.imp));

		list = (ListView) findViewById(R.id.list);
		deleteView = (CheckBox) findViewById(R.id.importDeleteCB);

		File[] files = FileUtils.getFiles();
		if (files != null && files.length == 0) {
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
		}


		fileAdapter = new FileAdapter(this, 0, files);
		list.setAdapter(fileAdapter);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
				selectedFile = fileAdapter.getItem(pos);
				getMediator().showConfirmImport();
			}
		});

        //TODO need to move into strings
        Toast.makeText(this, "Dir: " +
                FileUtils.getBackupDirectory().getAbsolutePath(), Toast.LENGTH_LONG).show();
	}

	public void onDialogEvent(int requestCode, int responseCode, Bundle params) {
		if (AlertDialog.ALERT_OK == requestCode) {
			NavUtils.navigateUpFromSameTask(this);
		} else if (ConfirmDialog.REQUEST_CODE_CONFIRM == requestCode) {
			doImport();
		}
	}

	private void doImport() {
		if (selectedFile == null) {
			return;
		}

		Bundle args = new Bundle();
		args.putInt(ExportImportLoader.TYPE_KEY, ExportImportLoader.TYPE_IMPORT);
		String fileName = selectedFile.getName();
		int xmlIdx = fileName.indexOf(".");
		if (xmlIdx > 0) {
			fileName = fileName.substring(0, xmlIdx);
		}
		args.putString(ExportImportLoader.NAME_KEY, fileName);
		args.putBoolean(ExportImportLoader.TYPE_DELETE_KEY, deleteView.isChecked());

		progress = ProgressDialog.show(this, getString(R.string.progress_title),
				getString(R.string.progress_content), true);

		getSupportLoaderManager().restartLoader(0, args, this);
	}

	@Override
	public Loader<ImportExportResult> onCreateLoader(int id, Bundle args) {
		return new ExportImportLoader(ImportActivity.this, args);
	}

	@Override
	public void onLoadFinished(Loader<ImportExportResult> loader, ImportExportResult data) {
		getMediator().getUnitFacade().reload(DBUtils.getActiveCarId(getContentResolver()));
		if (data.isDone()) {
			new android.os.Handler().post(new Runnable() {
				@Override
				public void run() {
					getMediator().showAlert(getString(R.string.import_ok));
					progress.dismiss();
				}
			});
		} else {
			new android.os.Handler().post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getBaseContext(), getString(R.string.export_import_error), Toast.LENGTH_SHORT).show();
					progress.dismiss();
				}
			});
		}
	}

	@Override
	public void onLoaderReset(Loader<ImportExportResult> loader) {

	}
}

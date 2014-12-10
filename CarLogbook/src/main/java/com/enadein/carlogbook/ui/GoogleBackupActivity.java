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
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.backup.FileUtils;
import com.enadein.carlogbook.core.backup.XMLImportExportStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GoogleBackupActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
	private GoogleApiClient mGoogleApiClient;
	private static final int REQUEST_CODE_RESOLUTION = 1;
	protected static final int REQUEST_CODE_CREATOR = 2;
	protected static final int REQUEST_CODE_OPENER = 3;

	private static final String TAG = "GoogleBackupActivity";

//	DriveId driveFileId = null;

	private Button restoreBtn;
	private Button backupBtn;

	private DriveState state;

	ProgressDialog progress;

	private boolean cancel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.googlebackup);
		setSubTitle(getString(R.string.google_backup));

		restoreBtn = (Button) findViewById(R.id.restore);
		backupBtn = (Button) findViewById(R.id.backUp);

		restoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				state = new RestoreState();
				linkToDrive();
			}
		});

		backupBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				state = new BackupState();
				linkToDrive();
			}
		});

		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (code == ConnectionResult.SUCCESS) {
			restoreBtn.setEnabled(true);
			backupBtn.setEnabled(true);
		} else {
			GooglePlayServicesUtil.getErrorDialog(code, this, 0).show();
		}
	}


	private void linkToDrive() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
			mGoogleApiClient = null;
		}
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.addScope(Drive.SCOPE_APPFOLDER)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();


		mGoogleApiClient.connect();
	}


	private interface DriveState {
		public void connected();
	}

	private class BackupState implements DriveState {

		@Override
		public void connected() {
			upload();
		}
	}

	private class RestoreState implements DriveState {

		@Override
		public void connected() {
			download();
		}
	}

	private class BackupStateReady implements DriveState {
		private DriveId driveFileId = null;

		private BackupStateReady(DriveId driveFileId) {
			this.driveFileId = driveFileId;
		}

		@Override
		public void connected() {
			if (driveFileId != null) {
				cancel = false;
				DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveFileId);
				new UploadAsyncTask().execute(file);
			}
		}
	}

	private class RestoreStateReady implements DriveState {
		private DriveId driveFileId = null;

		private RestoreStateReady(DriveId driveFileId) {
			this.driveFileId = driveFileId;
		}

		@Override
		public void connected() {
			if (driveFileId != null) {
				cancel = false;
				DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, driveFileId);
				new DownloadAsyncTask().execute(file);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onPause() {
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
		super.onPause();
	}

	@Override
	public void onConnected(Bundle bundle) {
		if (state != null) {
			state.connected();
		}

		state = null;
	}


	private void upload() {
		Drive.DriveApi.newContents(mGoogleApiClient)
				.setResultCallback(contentsCallback);
	}

	private void download() {
		IntentSender intentSender = Drive.DriveApi
				.newOpenFileActivityBuilder()
				.setMimeType(new String[]{"text/xml"})
				.build(mGoogleApiClient);
		try {
			startIntentSenderForResult(
					intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
		} catch (IntentSender.SendIntentException e) {
			Log.w(TAG, "Unable to send intent", e);
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.i(TAG, "GoogleApiClient suspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
			} catch (IntentSender.SendIntentException e) {
				// Unable to resolve, message user appropriately
			}
		} else {
			GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
			mGoogleApiClient.connect();
		} else if (requestCode == REQUEST_CODE_CREATOR && resultCode == RESULT_OK) {
			DriveId driveFileId = data.getParcelableExtra(
					OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
			state = new BackupStateReady(driveFileId);
		} else if (requestCode == REQUEST_CODE_OPENER && resultCode == RESULT_OK) {
			DriveId driveFileId = data.getParcelableExtra(
					OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
			state = new RestoreStateReady(driveFileId);
		} else {
			mGoogleApiClient.disconnect();
			mGoogleApiClient = null;
		}
	}

	final ResultCallback<DriveApi.ContentsResult> contentsCallback = new ResultCallback<DriveApi.ContentsResult>() {
		@Override
		public void onResult(DriveApi.ContentsResult result) {
			MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
					.setMimeType("text/xml").setTitle("CarLogbook_backup").build();

			IntentSender intentSender = Drive.DriveApi
					.newCreateFileActivityBuilder()
					.setInitialMetadata(metadataChangeSet)
					.setInitialContents(result.getContents())
					.build(mGoogleApiClient);
			try {
				startIntentSenderForResult(
						intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
			} catch (IntentSender.SendIntentException e) {
				Log.w(TAG, "Unable to send intent", e);
			}
		}
	};

	public class UploadAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = ProgressDialog.show(GoogleBackupActivity.this, getString(R.string.progress_title),
                    getString(R.string.progress_content), true);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progress.dismiss();

			if (!result) {
				showMessage(getString(R.string.export_import_error));
				return;
			}
			showMessage(getString(R.string.export_ok));
		}

		@Override
		protected Boolean doInBackground(DriveFile... args) {
			DriveFile file = args[0];
			try {
				DriveApi.ContentsResult contentsResult = file.openContents(
						mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
				if (!contentsResult.getStatus().isSuccess()) {
					return false;
				}
				OutputStream outputStream = contentsResult.getContents().getOutputStream();
				String localFileName = "drive" + System.currentTimeMillis();
				boolean ok = new XMLImportExportStrategy(GoogleBackupActivity.this).exportData(localFileName);
				if (ok) {
					InputStream is = FileUtils.openIn(localFileName);
					FileUtils.copyStrem(is, outputStream);
					FileUtils.closeIn(is);
				}

				FileUtils.deleteFile(localFileName);

				com.google.android.gms.common.api.Status status = file.commitAndCloseContents(
						mGoogleApiClient, contentsResult.getContents()).await();
				return status.getStatus().isSuccess();
			} catch (IOException e) {
				Log.e(TAG, "IOException while appending to the output stream", e);
			}
			return false;
		}

	}

	public class DownloadAsyncTask extends AsyncTask<DriveFile, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = ProgressDialog.show(GoogleBackupActivity.this, getString(R.string.progress_title),
                    getString(R.string.progress_content), true);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			cancel = true;
			progress.dismiss();
		}

		@Override
		protected Boolean doInBackground(DriveFile... args) {
			DriveFile file = args[0];
			try {
				DriveApi.ContentsResult contentsResult = file.openContents(
						mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
				if (!contentsResult.getStatus().isSuccess()) {
					return false;
				}
				InputStream is = contentsResult.getContents().getInputStream();
				String localFileName = "GDrive" + System.currentTimeMillis();
				FileUtils.createFile(localFileName);
				OutputStream out = FileUtils.openOut(localFileName);
				FileUtils.copyStrem(is, out);
				FileUtils.closeOut(out);

				if (!cancel) {
					new XMLImportExportStrategy(GoogleBackupActivity.this).importData(localFileName, false);
					FileUtils.deleteFile(localFileName);
				}
//				contentsResult.getContents().com;

//				file.discardContents(mGoogleApiClient, contentsResult.getContents()).await()
//				com.google.android.gms.common.api.Status status = file.commitAndCloseContents(
//						mGoogleApiClient, contentsResult.getContents()).await();
//				return status.getStatus().isSuccess();
				file.discardContents(mGoogleApiClient, contentsResult.getContents()).await();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "IOException while appending to the output stream", e);
			}
			return false;
		}


		@Override
		protected void onPostExecute(Boolean result) {
			progress.dismiss();

			if (!result) {
				showMessage(getString(R.string.export_import_error));
				return;
			}
			showMessage(getString(R.string.import_ok));

		}
	}


	/////////////////////////////////
	public void showMessage(String message) {
		Toast.makeText(GoogleBackupActivity.this, message, Toast.LENGTH_LONG).show();
	}

}
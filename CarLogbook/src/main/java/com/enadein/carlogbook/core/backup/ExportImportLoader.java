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
package com.enadein.carlogbook.core.backup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

public class ExportImportLoader  extends AsyncTaskLoader<ImportExportResult> {
	public static final int TYPE_IMPORT = 1;
	public static final int TYPE_EXPORT = 2;
	public static final String TYPE_KEY = "type";
	public static final String TYPE_DELETE_KEY = "delete";
	public static final String NAME_KEY = "name";

	public String name = null;

	private int type = -1;
	private ImportExportResult data = null;
	private boolean reset = false;

	public ExportImportLoader(Context context, Bundle bundle) {
		super(context);
		this.type = bundle.getInt(TYPE_KEY, -1);
		this.name = bundle.getString(NAME_KEY);
		this.reset = bundle.getBoolean(TYPE_DELETE_KEY);
	}

	@Override
	public ImportExportResult loadInBackground() {
		ImportExportStrategy strategy = new XMLImportExportStrategy(getContext());
		data = new ImportExportResult();
		if (type == TYPE_EXPORT) {
			data.setDone(strategy.exportData(name));
		} else if (type == TYPE_IMPORT) {
			data.setDone(strategy.importData(name, reset));
		}
		return data;
	}

	@Override
	protected void onStartLoading() {
		if (data != null) {
			deliverResult(data);
		} else {
			forceLoad();
		}
	}
}

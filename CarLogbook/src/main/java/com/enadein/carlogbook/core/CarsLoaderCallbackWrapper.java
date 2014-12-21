package com.enadein.carlogbook.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.enadein.carlogbook.bean.DataInfo;

abstract public class CarsLoaderCallbackWrapper implements LoaderManager.LoaderCallbacks<DataInfo>{
	private Activity activity;
	private CarLogbookMediator mediator;

	public CarsLoaderCallbackWrapper(Activity activity, CarLogbookMediator mediator) {
		this.activity = activity;
		this.mediator = mediator;
	}

	@Override
	public Loader<DataInfo> onCreateLoader(int id, Bundle bundle) {
		return new DataLoader(activity, DataLoader.CARS, mediator.getUnitFacade());
	}

	@Override
	public void onLoadFinished(Loader<DataInfo> dataInfoLoader, DataInfo dataInfo) {
		onLoaded(dataInfo.getCarsDataInfo());
	}

	@Override
	public void onLoaderReset(Loader<DataInfo> dataInfoLoader) {
		onReset();
	}

	abstract public void onReset();
	abstract public void onLoaded(CarsDataInfo cdi);
}

package com.enadein.carlogbook.core;


import android.app.Application;

import com.enadein.carlogbook.db.DBUtils;

public class CarlogbookApplication extends Application{
	private UnitFacade unitFacadeDefault;
	private UnitFacade unitFacade;
    private ReportFacade reportFacade;

	@Override
	public void onCreate() {
		super.onCreate();

		unitFacadeDefault = new UnitFacade(this);
		unitFacade = new UnitFacade(this);

        reportFacade = new ReportFacade(this);

		unitFacade.reload(DBUtils.getActiveCarId(getContentResolver()));
	}

	public UnitFacade getUnitFacadeDefault() {
		return unitFacadeDefault;
	}

	public UnitFacade getUnitFacade() {
		return unitFacade;
	}
}

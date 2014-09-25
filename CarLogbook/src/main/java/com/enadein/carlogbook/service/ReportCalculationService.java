package com.enadein.carlogbook.service;

import android.app.IntentService;
import android.content.Intent;

import com.enadein.carlogbook.core.CarlogbookApplication;
import com.enadein.carlogbook.core.ReportFacade;

public class ReportCalculationService extends IntentService {
    public ReportCalculationService() {
        super("REP_CALC");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CarlogbookApplication app = (CarlogbookApplication) getApplication();
        ReportFacade reportFacade = new ReportFacade(getApplicationContext());
        reportFacade.calculateFuelRate(app.getUnitFacade());
    }
}

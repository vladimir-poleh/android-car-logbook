package com.enadein.carlogbook.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class ReportFacade {
    private ContentResolver cr;
    private Context ctx;

    public ReportFacade(Context ctx) {
        this.cr = ctx.getContentResolver();
        this.ctx = ctx;
    }

    public void calculateFuelRate() {
        long carId = DBUtils.getActiveCarId(cr);
        Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI, null,
                ProviderDescriptor.Log.Cols.CAR_ID + " = ? and "
                        + ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?",
                new String[] {String.valueOf(carId), String.valueOf(ProviderDescriptor.Log.Type.FUEL)},
                null);
        if (DBUtils.isCursorHasValue(c)) {


            c.close();
        }
    }
}

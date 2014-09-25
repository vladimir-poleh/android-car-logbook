package com.enadein.carlogbook.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.enadein.carlogbook.bean.FuelRateBean;
import com.enadein.carlogbook.bean.XReport;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.util.Calendar;

public class ReportFacade {
    private ContentResolver cr;
    private Context ctx;

    public ReportFacade(Context ctx) {
        this.cr = ctx.getContentResolver();
        this.ctx = ctx;
    }

    public void calculateFuelRate(UnitFacade unitFacade) {
        cr.delete(ProviderDescriptor.FuelRate.CONTENT_URI, null, null);

        long carId = DBUtils.getActiveCarId(cr);
        Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI, null,
                ProviderDescriptor.Log.Cols.CAR_ID + " = ? and "
                        + ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?",
                new String[]{String.valueOf(carId), String.valueOf(ProviderDescriptor.Log.Type.FUEL)},
                ProviderDescriptor.Log.Cols.DATE + " ASC");

        if (c == null) {
            return;
        }

        int odometerPrev = -1;
        long stationIdPrev = -1;
        long fuelTypeIdPrev = -1;

        while (c.moveToNext()) {
            int odometer = DBUtils.getIntByName(c, ProviderDescriptor.Log.Cols.ODOMETER);
            long stationId = DBUtils.getLongByName(c, ProviderDescriptor.Log.Cols.FUEL_STATION_ID);
            long fuelTypeId = DBUtils.getLongByName(c, ProviderDescriptor.Log.Cols.FUEL_TYPE_ID);
            double fuelVolume = DBUtils.getDoubleByName(c, ProviderDescriptor.Log.Cols.FUEL_VOLUME);

            int odometerDiff = odometer - odometerPrev;
            if (odometerPrev > 0 && odometerDiff > 0) {
                double rate = unitFacade.getRate(fuelVolume, odometerDiff);
                updateRate(carId, fuelTypeIdPrev, stationIdPrev, rate, odometerDiff, fuelVolume, unitFacade);
            }

            odometerPrev = odometer;
            stationIdPrev = stationId;
            fuelTypeIdPrev = fuelTypeId;
        }

        c.close();
    }

    public void updateRate(long carId, long fuelTypeId, long stationId, double rate, int odometerDiff, double fuelVolume, UnitFacade unitFacade) {
        FuelRateBean fuelRateBean = DBUtils.getCurrentFuelRate(cr, carId, fuelTypeId, stationId);

        if (fuelRateBean == null) {
            fuelRateBean = new FuelRateBean();
            fuelRateBean.setCarId(carId);
            fuelRateBean.setStationId(stationId);
            fuelRateBean.setFuelTypeId(fuelTypeId);
            fuelRateBean.setRate(rate);
            fuelRateBean.setMinRate(rate);
            fuelRateBean.setMaxRate(rate);
            fuelRateBean.setFuelSum(fuelVolume);
            fuelRateBean.setDistSum(odometerDiff);
            fuelRateBean.setAvg(unitFacade.getRate(fuelVolume, odometerDiff));
            cr.insert(ProviderDescriptor.FuelRate.CONTENT_URI, fuelRateBean.getCV());
        } else {
            fuelRateBean.setRate(rate);

            fuelRateBean.setFuelSum(fuelRateBean.getFuelSum() + fuelVolume);
            fuelRateBean.setDistSum(fuelRateBean.getDistSum() + odometerDiff);
            fuelRateBean.setAvg(unitFacade.getRate(fuelRateBean.getFuelSum(), (int) fuelRateBean.getDistSum()));

            if (rate < fuelRateBean.getMinRate()) {
                fuelRateBean.setMinRate(rate);
            }
            if (rate > fuelRateBean.getMaxRate()) {
                fuelRateBean.setMaxRate(rate);
            }
            cr.update(ProviderDescriptor.FuelRate.CONTENT_URI, fuelRateBean.getCV(), "_id = ?",
                    new String[]{String.valueOf(fuelRateBean.getId())});
        }
    }

    //FUEL
    public double getFuelCountTotal(ContentResolver cr, long carId) {
        return DBUtils.getTotalFuel(carId, cr, -1, -1, false);
    }

    public int getFillupCount(ContentResolver cr, long carId) {
        int result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.Log.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"count(*) as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getIntByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getMinFillupVolume(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.Log.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"min(" + ProviderDescriptor.LogView.Cols.FUEL_VOLUME + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getMaxFillupVolume(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.Log.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"max(" + ProviderDescriptor.LogView.Cols.FUEL_VOLUME + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getAvgFillupVolume(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.Log.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"avg(" + ProviderDescriptor.LogView.Cols.FUEL_VOLUME + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getFuelCountCurrentMonth(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getTotalFuel(carId, cr, from, to, false);
    }

    public double getFuelCountLastMonth(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long from = calendar.getTimeInMillis();

        return DBUtils.getTotalFuel(carId, cr, from, to, false);
    }

    public double getFuelCountCurrentYear(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getTotalFuel(carId, cr, from, to, false);
    }

    public double getFuelCountLastYear(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long from = calendar.getTimeInMillis();

        return DBUtils.getTotalFuel(carId, cr, from, to, false);
    }


    //DISTANCE
    public int getTotalDistance(ContentResolver cr, long carId) {
        return DBUtils.getOdometerCount(carId, cr);
    }

    public long getOdometer(ContentResolver cr, long carId) {
        return DBUtils.getMaxOdometerValue(cr, carId);
    }


    public int getCurrentMonthDistance(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getOdometerCount(carId, cr, from, to, -1);
    }

    public int getLastMonthDistance(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long from = calendar.getTimeInMillis();

        return DBUtils.getOdometerCount(carId, cr, from, to, -1);
    }

    public int getCurrentYearDistance(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getOdometerCount(carId, cr, from, to, -1);
    }

    public int getLastYearDistance(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long from = calendar.getTimeInMillis();


        return DBUtils.getOdometerCount(carId, cr, from, to, -1);
    }

    public double getAVGDistancePerDay(ContentResolver cr, long carId) {
        long odometerCount = DBUtils.getOdometerCount(carId, cr);
        double days = DBUtils.getDayPassed(cr, carId);

        return odometerCount / days;
    }

    public double getAVGDistancePerMonth(ContentResolver cr, long carId) {
        return getAVGDistancePerDay(cr, carId) * 31;
    }

    public double getAVGDistancePerYear(ContentResolver cr, long carId) {
        return getAVGDistancePerMonth(cr, carId) * 12;
    }


    //COST
    public double getTotalCost(ContentResolver cr, long carId) {
        return DBUtils.getTotalPrice(carId, cr);
    }

    public double getTotalCostThisMonth(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getTotalPrice(carId, cr, from, to, -1);
    }

    public double getTotalCostLastMonth(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkMonth(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long from = calendar.getTimeInMillis();

        return DBUtils.getTotalPrice(carId, cr, from, to, -1);
    }

    public double getTotalCostThisYear(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);
        long from = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, 1);
        long to = calendar.getTimeInMillis();

        return DBUtils.getTotalPrice(carId, cr, from, to, -1);
    }

    public double getTotalCostLastYear(ContentResolver cr, long carId) {
        Calendar calendar = Calendar.getInstance();
        CommonUtils.trunkYear(calendar);

        long to = calendar.getTimeInMillis();
        calendar.add(Calendar.YEAR, -1);
        long from = calendar.getTimeInMillis();

        return DBUtils.getTotalPrice(carId, cr, from, to, -1);
    }

    public double getCostPer1Dist(ContentResolver cr, long carId) {
        return DBUtils.getPricePer1km(carId, cr, -1, -1);
    }

    public double getMinFuelPrice1Unit(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"min(" + ProviderDescriptor.LogView.Cols.PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getAvgFuelPrice1Unit(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"avg(" + ProviderDescriptor.LogView.Cols.PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getMaxFuelPrice1Unit(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"max(" + ProviderDescriptor.LogView.Cols.PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getMinCostFillup(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"min(" + ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getAvgCostFillup(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"avg(" + ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getMaxCostFillup(ContentResolver cr, long carId) {
        double result = 0;

        StringBuilder selection = new StringBuilder();
        selection.append(ProviderDescriptor.Log.Cols.CAR_ID).append(" = ? ")
                .append("and ").append(ProviderDescriptor.LogView.Cols.TYPE_LOG).append(" = ?");


        String[] args = new String[]{String.valueOf(carId),
                String.valueOf(ProviderDescriptor.Log.Type.FUEL)};

        Cursor c = cr.query(ProviderDescriptor.LogView.CONTENT_URI,
                new String[]{"max(" + ProviderDescriptor.LogView.Cols.TOTAL_PRICE + ") as sum"},
                selection.toString(), args, null);

        if (DBUtils.isCursorHasValue(c)) {
            result = DBUtils.getDoubleByName(c, "sum");
            c.close();
        }


        return result;
    }

    public double getAvgTotalCostPerDay(ContentResolver cr, long carId) {
        double cost = DBUtils.getTotalPrice(carId, cr);
        double dayPassed = DBUtils.getDayPassed(cr, carId);

        return cost / dayPassed;
    }

    public double getAvgTotalCostPerMonth(ContentResolver cr, long carId) {
        return getAvgTotalCostPerDay(cr, carId) * 31;
    }

    public double getAvgTotalCostPerYear(ContentResolver cr, long carId) {
        return getAvgTotalCostPerMonth(cr, carId) * 12;
    }

    public double getAvgFuelCostPerDay(ContentResolver cr, long carId) {
        double cost = DBUtils.getTotalPrice(carId, cr, -1, -1, ProviderDescriptor.Log.Type.FUEL);
        double dayPassed = DBUtils.getDayPassed(cr, carId);

        return cost / dayPassed;
    }

    public double getAvgFuelCostPerMonth(ContentResolver cr, long carId) {
        return getAvgFuelCostPerDay(cr, carId) * 31;
    }

    public double getAvgFuelCostPerYear(ContentResolver cr, long carId) {
        return getAvgFuelCostPerMonth(cr, carId) * 12;
    }

    public double getAvgOtherExpensesCostPerDay(ContentResolver cr, long carId) {
        double cost = DBUtils.getTotalPrice(carId, cr, -1, -1, ProviderDescriptor.Log.Type.OTHER);
        double dayPassed = DBUtils.getDayPassed(cr, carId);

        return cost / dayPassed;
    }

    public double getAvgOhterExpensesCostPerMonth(ContentResolver cr, long carId) {
        return getAvgOtherExpensesCostPerDay(cr, carId) * 31;
    }

    public double getAvgOtherExpensesCostPerYear(ContentResolver cr, long carId) {
        return getAvgOhterExpensesCostPerMonth(cr, carId) * 12;
    }

    //Consumption
    public double getAvgLPer100(ContentResolver cr, long carId) {
        UnitFacade customUnit = new UnitFacade(ctx);
        customUnit.setConsumptionValue(0);
        return DBUtils.getAvgFuel(carId, cr, 0, 0, customUnit);
    }

    public double getAvgLPer1Km(ContentResolver cr, long carId) {
        UnitFacade customUnit = new UnitFacade(ctx);
        customUnit.setConsumptionValue(1);
        return DBUtils.getAvgFuel(carId, cr, 0, 0, customUnit);
    }

    public double getAvgKmPerL(ContentResolver cr, long carId) {
        UnitFacade customUnit = new UnitFacade(ctx);
        customUnit.setConsumptionValue(2);
        return DBUtils.getAvgFuel(carId, cr, 0, 0, customUnit);
    }

    public void calculateXReport(XReport xReport, ContentResolver cr, long carId) {

        Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI, null,
                ProviderDescriptor.Log.Cols.CAR_ID + " = ? and "
                        + ProviderDescriptor.Log.Cols.TYPE_LOG + " = ?",
                new String[]{String.valueOf(carId), String.valueOf(ProviderDescriptor.Log.Type.FUEL)},
                ProviderDescriptor.Log.Cols.DATE + " ASC");

        double daysSUM = 0;
        double daysCount = 0;
        double daysMin = 0;
        double daysMax = 0;

        double odometerSUM = 0;
        double odometerCount = 0;
        double odometerMin = 0;
        double odometerMax= 0;

        if (c == null) {
            return;
        }

        int odometerPrev = -1;
        long datePrev = -1;

        while (c.moveToNext()) {
            int odometer = DBUtils.getIntByName(c, ProviderDescriptor.Log.Cols.ODOMETER);
            long date = DBUtils.getLongByName(c, ProviderDescriptor.Log.Cols.DATE);

            long dateDiff = date - datePrev;
            if (datePrev > 0 && dateDiff >0) {
                double days = DBUtils.calcDayPassed(datePrev, date);
                if (days > 0) {
                    daysSUM += days;
                    daysCount++;
                    daysMin = (daysMin == 0) ? days : Math.min(daysMin, days);
                    daysMax = Math.max(daysMax, days);
                }
            }

            int odometerDiff = odometer - odometerPrev;
            if (odometerPrev > 0 && odometerDiff > 0) {
                odometerSUM += odometerDiff;
                odometerCount++;
                odometerMin = (odometerMin == 0) ? odometerDiff : Math.min(odometerMin, odometerDiff);
                odometerMax = Math.max(odometerMax, odometerDiff);
            }

            odometerPrev = odometer;
            datePrev = date;
        }

        c.close();

        xReport.avg_days_fillups = (daysCount > 0) ? daysSUM / daysCount : 0;
        xReport.min_days_fillups = daysMin;
        xReport.max_days_fillups = daysMax;

        xReport.avg_fillup_dist = (odometerCount > 0) ? odometerSUM / odometerCount : 0;
        xReport.min_fillup_dist = odometerMin;
        xReport.max_fillup_dist = odometerMax;
    }
}

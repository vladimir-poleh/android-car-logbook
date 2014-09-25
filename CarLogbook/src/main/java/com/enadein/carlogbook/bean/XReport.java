package com.enadein.carlogbook.bean;

public class XReport {
    //FUEL
    public double fuelCountTotal;
    public int fillupCount;
    public double minFillupVolume;
    public double maxFillupVolume;
    public double avgFillupVolume;
    public double fuelVolumeCurrentMonth;
    public double fuelVolumeLastMonth;
    public double fuelVolumeCurrentYear;
    public double fuelVolumeLastYear;

    public double min_days_fillups;
    public double max_days_fillups;
    public double avg_days_fillups;

    //DIST
    public int totalDist;
    public long odometer_count;
    public int month_dist;
    public int last_month_dist;
    public int year_dist;
    public int last_year_dist;
    public double per_day_dist;
    public double per_month_dist;
    public double per_year_dist;

    public double min_fillup_dist;
    public double max_fillup_dist;
    public double avg_fillup_dist;

    //COST
    public double cost_total;
    public double cost_per1;
    public double cost_total_month;
    public double cost_total_last_month;
    public double cost_total_year;
    public double cost_total_last_year;
    public double cost_price_min;
    public double cost_price_max;
    public double cost_price_avg;
    public double cost_fillup_min;
    public double cost_fillup_max;
    public double cost_fillup_avg;
    public double cost_total_per_day;
    public double cost_total_per_month;
    public double cost_total_per_year;
    public double cost_total_per_day_fuel;
    public double cost_total_per_month_fuel;
    public double cost_total_per_year_fuel;
    public double cost_total_per_day_other;
    public double cost_total_per_month_other;
    public double cost_total_per_year_other;

    public double avg100;
    public double avglperkm;
    public double avgkmperl;
}

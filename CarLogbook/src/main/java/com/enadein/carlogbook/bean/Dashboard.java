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
package com.enadein.carlogbook.bean;

import java.util.ArrayList;

public class Dashboard {
	private double totalOdometerCount; //mileage/run
	private double totalPrice;

	private double totalFuelCount;

	private double pricePer1;
	private double priceFuelPer1;

	private double fuelRateAvg; //2

	public double getPriceFuelPer1() {
		return priceFuelPer1;
	}

	public void setPriceFuelPer1(double priceFuelPer1) {
		this.priceFuelPer1 = priceFuelPer1;
	}

	public double getFuelRateAvg100() {
        return fuelRateAvg100;
    }

    public double getFuelRateAvg2() {
        return fuelRateAvg2;
    }

    public void setFuelRateAvg2(double fuelRateAvg2) {
        this.fuelRateAvg2 = fuelRateAvg2;
    }

    public void setFuelRateAvg100(double fuelRateAvg100) {
        this.fuelRateAvg100 = fuelRateAvg100;
    }

    private double fuelRateAvg100; // 0

    private double fuelRateAvg2; //1


	private double totalFuelPrice;
	private double totalServicePrice;
	private double totalPartsPrice;
	private double totalParkingPrice;
	private double totalOtherPrice;

	private double odometerCountByYearAvg;
	private double odometerCountByMonthAvg;
	private double odometerCountByDayAvg;

	private ArrayList<BarInfo> costLast4Months;

	public ArrayList<BarInfo> getRunLast4Months() {
		return runLast4Months;
	}

	public void setRunLast4Months(ArrayList<BarInfo> runLast4Months) {
		this.runLast4Months = runLast4Months;
	}

	private ArrayList<BarInfo> runLast4Months;


	public double getTotalOdometerCount() {
		return totalOdometerCount;
	}

	public void setTotalOdometerCount(double totalOdometerCount) {
		this.totalOdometerCount = totalOdometerCount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public double getTotalFuelCount() {
		return totalFuelCount;
	}

	public void setTotalFuelCount(double totalFuelCount) {
		this.totalFuelCount = totalFuelCount;
	}

	public double getPricePer1() {
		return pricePer1;
	}

	public void setPricePer1(double pricePer1) {
		this.pricePer1 = pricePer1;
	}

	public double getFuelRateAvg() {
		return fuelRateAvg;
	}

	public void setFuelRateAvg(double fuelRateAvg) {
		this.fuelRateAvg = fuelRateAvg;
	}

	public double getTotalFuelPrice() {
		return totalFuelPrice;
	}

	public void setTotalFuelPrice(double totalFuelPrice) {
		this.totalFuelPrice = totalFuelPrice;
	}

	public double getTotalServicePrice() {
		return totalServicePrice;
	}

	public void setTotalServicePrice(double totalServicePrice) {
		this.totalServicePrice = totalServicePrice;
	}

	public double getTotalParkingPrice() {
		return totalParkingPrice;
	}

	public void setTotalParkingPrice(double totalParkingPrice) {
		this.totalParkingPrice = totalParkingPrice;
	}

	public double getTotalOtherPrice() {
		return totalOtherPrice;
	}

	public void setTotalOtherPrice(double totalOtherPrice) {
		this.totalOtherPrice = totalOtherPrice;
	}

	public double getOdometerCountByYearAvg() {
		return odometerCountByYearAvg;
	}

	public void setOdometerCountByYearAvg(double odometerCountByYearAvg) {
		this.odometerCountByYearAvg = odometerCountByYearAvg;
	}

	public double getOdometerCountByMonthAvg() {
		return odometerCountByMonthAvg;
	}

	public void setOdometerCountByMonthAvg(double odometerCountByMonthAvg) {
		this.odometerCountByMonthAvg = odometerCountByMonthAvg;
	}

	public double getOdometerCountByDayAvg() {
		return odometerCountByDayAvg;
	}

	public void setOdometerCountByDayAvg(double odometerCountByDayAvg) {
		this.odometerCountByDayAvg = odometerCountByDayAvg;
	}

	public double getTotalPartsPrice() {
		return totalPartsPrice;
	}

	public void setTotalPartsPrice(double totalPartsPrice) {
		this.totalPartsPrice = totalPartsPrice;
	}

	public ArrayList<BarInfo> getCostLast4Months() {
		return costLast4Months;
	}

	public void setCostLast4Months(ArrayList<BarInfo> costLast4Months) {
		this.costLast4Months = costLast4Months;
	}
}

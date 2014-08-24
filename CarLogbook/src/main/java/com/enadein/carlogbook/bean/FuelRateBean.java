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

import android.content.ContentValues;
import android.database.Cursor;

import com.enadein.carlogbook.core.DBBean;
import com.enadein.carlogbook.db.ProviderDescriptor;

public class FuelRateBean implements DBBean{
	private long id;
	private long fuelTypeId;
	private long carId;
	private long stationId;

	private double rate;
	private double minRate;
	private double maxRate;

	public long getId() {
		return id;
	}

	public long getCarId() {
		return carId;
	}

	public void setCarId(long carId) {
		this.carId = carId;
	}

	public void setId(long id) {
		this.id = id;

	}

	public long getFuelTypeId() {
		return fuelTypeId;
	}

	public void setFuelTypeId(long fuelTypeId) {
		this.fuelTypeId = fuelTypeId;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getMinRate() {
		return minRate;
	}

	public void setMinRate(double minRate) {
		this.minRate = minRate;
	}

	public double getMaxRate() {
		return maxRate;
	}

	public void setMaxRate(double maxRate) {
		this.maxRate = maxRate;
	}

	@Override
	public ContentValues getCV() {
		ContentValues cv = new ContentValues();

		cv.put(ProviderDescriptor.FuelRate.Cols.CAR_ID, getCarId());
		cv.put(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID, getFuelTypeId());
		cv.put(ProviderDescriptor.FuelRate.Cols.STATION_ID, getStationId());
		cv.put(ProviderDescriptor.FuelRate.Cols.MAX_RATE, getMaxRate());
		cv.put(ProviderDescriptor.FuelRate.Cols.MIN_RATE, getMinRate());
		cv.put(ProviderDescriptor.FuelRate.Cols.RATE, getRate());

		return cv;
	}

	@Override
	public void populate(Cursor c) {
		int idIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols._ID);
		int carIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.CAR_ID);
		int fuelTypeIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID);
		int stationIdIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.STATION_ID);
		int maxRateIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.MAX_RATE);
		int minRateIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.MIN_RATE);
		int rateIdx = c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.RATE);

		setId(c.getLong(idIdx));
		setCarId(c.getLong(carIdx));
		setFuelTypeId(c.getLong(fuelTypeIdx));
		setStationId(c.getLong(stationIdIdx));
		setMaxRate(c.getDouble(maxRateIdx));
		setMinRate(c.getDouble(minRateIdx));
		setRate(c.getDouble(rateIdx));
	}
}

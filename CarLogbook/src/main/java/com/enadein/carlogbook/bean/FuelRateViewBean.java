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

import android.database.Cursor;

import com.enadein.carlogbook.db.ProviderDescriptor;

public class FuelRateViewBean extends FuelRateBean {
	private String station;
	private String fuelType;

	@Override
	public void populate(Cursor c) {
		super.populate(c);
		int stationIdx = c.getColumnIndex(ProviderDescriptor.FuelRateView.Cols.STATION_NAME);
		station = c.getString(stationIdx);
		int fuelTypeIdx = c.getColumnIndex(ProviderDescriptor.FuelRateView.Cols.FUEL_NAME);
		fuelType = c.getString(fuelTypeIdx);
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
}

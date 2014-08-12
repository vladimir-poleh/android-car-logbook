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
package com.enadein.carlogbook.core;

public class MenuEnabler {
	private boolean share;
	private boolean addLog;
	private boolean addFuelLog;
	private boolean notification;
	private boolean addCar;

	public boolean isShare() {
		return share;
	}

	public void setShare(boolean share) {
		this.share = share;
	}

	public boolean isAddLog() {
		return addLog;
	}

	public void setAddLog(boolean addLog) {
		this.addLog = addLog;
	}

	public boolean isAddFuelLog() {
		return addFuelLog;
	}

	public void setAddFuelLog(boolean addFuelLog) {
		this.addFuelLog = addFuelLog;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public boolean isAddCar() {
		return addCar;
	}

	public void setAddCar(boolean addCar) {
		this.addCar = addCar;
	}

}

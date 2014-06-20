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
package com.carlogbook.core;

import android.util.Log;

public class Logger {
	public static final String GLOBAL_TAG = "com.carlogbook";
	public static boolean GLOBAL_FLAG = true;

	private String tag;
	private int level = Log.DEBUG;


	private Logger(String tag) {
		this.tag = (GLOBAL_FLAG)? GLOBAL_TAG : tag;
	}

	public static Logger createLogger(Class<?> cls) {
		return new Logger(cls.getSimpleName());
	}

	public void debug(String msg) {
		if (level <= Log.DEBUG) {
			Log.d(tag, msg);
		}
	}

	public void info(String msg) {
		if (level <= Log.INFO) {
			Log.i(tag, msg);
		}
	}

	public void warn(String msg) {
		if (level <= Log.WARN) {
			Log.w(tag, msg);
		}
	}

	public void error(String msg) {
		if (level <= Log.ERROR) {
			Log.e(tag, msg);
		}
	}
}

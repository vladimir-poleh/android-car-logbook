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
package com.enadein.carlogbook.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.enadein.carlogbook.db.CommonUtils;

import java.util.Calendar;


public class NotifyService extends IntentService {
	public static final int DAY = 1000 * 60 * 60 * 24;

	public NotifyService() {
		super("CarLogbook service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		CommonUtils.createNotify(getBaseContext(), 6);
	}

	public static void createAlarm(Context ctx) {
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 0);
		c.add(Calendar.DAY_OF_MONTH, 1);

//		long when = c.getTimeInMillis();
		long when = Calendar.getInstance().getTimeInMillis() + 5000;

		PendingIntent pendingIntent = getPendingIntentForNotifyService(ctx);


		alarmManager.setRepeating(AlarmManager.RTC, when, DAY, pendingIntent);
	}

	public static void cancelAlarm(Context ctx) {
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pendingIntent = getPendingIntentForNotifyService(ctx);
		alarmManager.cancel(pendingIntent);
	}

	private static PendingIntent getPendingIntentForNotifyService(Context ctx) {
		Intent intent = new Intent(ctx, NotifyService.class);
		return PendingIntent.getService(ctx, 0, intent, 0);
	}
}

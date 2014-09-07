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
package com.enadein.carlogbook.db;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.DatePicker;
import android.widget.EditText;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.service.NotifyService;
import com.enadein.carlogbook.ui.AddUpdateNotificationActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class CommonUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_FORMAT_MONTH = "MMM";
	private static 	DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();

	static {
		format.setRoundingMode(RoundingMode.DOWN);
		format.setMaximumFractionDigits(3);
		format.setMinimumFractionDigits(1);
		format.setMaximumIntegerDigits(10);
		format.setMinimumIntegerDigits(1);

		DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
		decimalSymbol.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(decimalSymbol);
		format.setGroupingUsed(false);
	}

	public static void createNotify(Context ctx, long id, int res) {
		Cursor c = ctx.getContentResolver()
				.query(ProviderDescriptor.Notify.CONTENT_URI, null, BaseActivity.SELECTION_ID_FILTER,
						new String[]{String.valueOf(id)}, null);

		if (c == null) {
			return;
		}
		boolean hasItem = c.moveToFirst();

		if (!hasItem) {
			return;
		}

		int nameIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME);

		String name = c.getString(nameIdx);

		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
						.setSmallIcon(res)
						.setSound(alarmSound)
						.setContentTitle(ctx.getString(R.string.app_name))
						.setContentText(name);
		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notifyIntent = new Intent(ctx, AddUpdateNotificationActivity.class);
		notifyIntent.putExtra(BaseActivity.MODE_KEY, BaseActivity.PARAM_EDIT);
		notifyIntent.putExtra(BaseActivity.ENTITY_ID, id);

		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(AddUpdateNotificationActivity.class);
		stackBuilder.addNextIntent(notifyIntent);

		PendingIntent notifyPendingIntent =
				PendingIntent.getActivity(
						ctx,
						0,
						notifyIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		mBuilder.setContentIntent(notifyPendingIntent);
		mBuilder.setAutoCancel(true);

		mNotificationManager.notify((int) id, mBuilder.build());
	}

	public static void validateDateNotifications(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
		long carId = DBUtils.getActiveCarId(cr);
		String selection = DBUtils.CAR_SELECTION_NOTIFY + " and "
				+ ProviderDescriptor.Notify.Cols.TYPE + " = ?";

		Cursor c = cr.query(ProviderDescriptor.Notify.CONTENT_URI,
				null, selection,
				new String[]{String.valueOf(carId),
						String.valueOf(ProviderDescriptor.Notify.Type.DATE)
				}, null
		);

		if (c == null) {
			NotifyService.cancelAlarm(ctx);
			return;
		}

		int count = c.getCount();
		c.close();

		if (count > 0) {
			NotifyService.cancelAlarm(ctx);
			NotifyService.createAlarm(ctx);
		} else {
			NotifyService.cancelAlarm(ctx);
		}

	}

	public static void validateOdometerNotifications(Context ctx, int odmeterValue) {
		ContentResolver cr = ctx.getContentResolver();
		long carId = DBUtils.getActiveCarId(cr);
		String selection = DBUtils.CAR_SELECTION_NOTIFY + " and "
				+ ProviderDescriptor.Notify.Cols.TYPE + " = ? and "
				+ ProviderDescriptor.Notify.Cols.TRIGGER_VALUE + " <= ?";
		Cursor c = cr.query(ProviderDescriptor.Notify.CONTENT_URI,
				null, selection,
				new String[]{String.valueOf(carId),
						String.valueOf(ProviderDescriptor.Notify.Type.ODOMETER),
						String.valueOf(odmeterValue)
				}, null
		);

		if (c == null) {
			return;
		}

		while (c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols._ID));
			createNotify(ctx, id, R.drawable.not_odom);
		}

		c.close();
	}

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

	public static String formatMonth(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_MONTH);
		return sdf.format(date);
	}

	public static double getPriceValue(EditText text) {
		double result = 0;
		result = getPriceValue(text.getText().toString().trim(), result);
		return result;
	}

	public static double getRawDouble(String text) {
		double result = 0.;
		try {
			result = Double.valueOf(text.trim());
		} catch (NumberFormatException e){	}
		return result;
	}

	public static int getOdometerInt(EditText text) {
		int value = 0;
		try {
			value = Integer.valueOf(text.getText().toString().trim());
		} catch (NumberFormatException e){	}
		return value;
	}

	public static double getPriceValue(String stringValue, double result) {
		if (stringValue.length() > 0) {
			result = parsePriceString(stringValue);
		}
		return result;
	}

	public static double parsePriceString(String string) {
		NumberFormat format = getPriceNumberFormat();

		Number result = 0;
		try {
			result = format.parse(string);
		} catch (ParseException e) {
			//nothing
		}

		return result.doubleValue();
	}

	public static String formatPrice(double price) {
		NumberFormat format = getPriceNumberFormat();
		String result = format.format(price);
		return "0.0".equals(result) ? "" : result;
	}

	public static double div(double a, double b) {
		return (b == 0) ? 0 : a / b;
	}

	private static NumberFormat getPriceNumberFormat() {
		return format;
	}

	public static void trunkMonth(Calendar c) {
		trunkDay(c);
		c.set(Calendar.DAY_OF_MONTH, 1);
	}

	public static void trunkDay(Calendar c) {
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
	}

	public static boolean isNotEmpty(String str) {
		return (str != null && !str.trim().equals(""));
	}

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().equals(""));
	}
}

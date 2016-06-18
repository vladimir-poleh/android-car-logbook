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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.EditText;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseActivity;
import com.enadein.carlogbook.core.UnitFacade;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class CommonUtils {
	public static final String DATE_FORMAT_TEST = "yyyy-MM-dd";
	public static final String DATE_FORMAT_MONTH = "MMM";

	private static 	DecimalFormat format; // = (DecimalFormat) DecimalFormat.getNumberInstance();
	private static 	DecimalFormat formatFuel;// = (DecimalFormat) DecimalFormat.getNumberInstance();
	private static 	DecimalFormat formatComma;// = (DecimalFormat) DecimalFormat.getNumberInstance();
	private static 	DecimalFormat formatFuelComma; // = (DecimalFormat) DecimalFormat.getNumberInstance();
	private static 	DecimalFormat formatDistance;
	private static 	DecimalFormat formatP;
	private static 	DecimalFormat formatPComma;

	private static HashMap<String, Integer> consumption = new HashMap<String, Integer>();

	private CommonUtils() {
	}

	public static void runAnimation(int mlastPos, int pos, View view, float size) {
		if (UnitFacade.ANIM_LIST_ON) {
			float initialTranslation = (mlastPos <= pos) ? size : -size;
			Animation animationY = new TranslateAnimation(0, 0, initialTranslation, 0);
			animationY.setDuration(400);
			view.startAnimation(animationY);
			animationY = null;
		}
	}

	static {
        setupDecimalFormat();

		consumption.put("00", R.array.unit_consumption_1_1);
		consumption.put("01", R.array.unit_consumption_1_2);
		consumption.put("10", R.array.unit_consumption_2_1);
		consumption.put("11", R.array.unit_consumption_2_2);
	}

	public static void setupDecimalFormat() {
		formatP = createDecimalFormat(1, 3, 0, 2, '.', RoundingMode.HALF_UP);
		formatPComma =  createDecimalFormat(1, 3, 0, 2, ',',RoundingMode.HALF_UP);
		formatDistance = createDecimalFormat(1, 16,
				0, 0, '.');

		format = createDecimalFormat(1, 16,
				UnitFacade.currencyFract, UnitFacade.currencyFract, '.');
		formatComma = createDecimalFormat(1, 16,
				UnitFacade.currencyFract, UnitFacade.currencyFract, ',');

		formatFuel = createDecimalFormat(1, 16,
				UnitFacade.fuelFract, UnitFacade.fuelFract, '.');
		formatFuelComma = createDecimalFormat(1, 16,
				UnitFacade.fuelFract, UnitFacade.fuelFract, ',');
	}
	public static DecimalFormat createDecimalFormat(int minInt, int maxInt,
													int minFract, int maxFract, char separator) {
		return createDecimalFormat(minInt, maxInt, minFract, maxFract, separator, RoundingMode.HALF_UP);
	}

	public static DecimalFormat createDecimalFormat(int minInt, int maxInt,
													int minFract, int maxFract, char separator,RoundingMode mode) {

		DecimalFormat format = (DecimalFormat) DecimalFormat.getNumberInstance();
		format.setRoundingMode(mode);

		format.setMaximumFractionDigits(maxFract);
		format.setMinimumFractionDigits(minFract);
		format.setMaximumIntegerDigits(maxInt);
		format.setMinimumIntegerDigits(minInt);
		DecimalFormatSymbols decimalSymbolComma = new DecimalFormatSymbols();
		decimalSymbolComma.setDecimalSeparator(separator);
		format.setDecimalFormatSymbols(decimalSymbolComma);
		format.setGroupingUsed(false);

		return format;
	}

	public static int getConsumptionArrayId(int distId, int fuelId) {
		String key = distId +"" + fuelId;
		return consumption.get(key);
	}

	public static void createNotify(Context ctx, long id) {
		Cursor c = ctx.getContentResolver()
				.query(ProviderDescriptor.Notify.CONTENT_URI, null, BaseActivity.SELECTION_ID_FILTER,
						new String[]{String.valueOf(id)}, null);

		UnitFacade notifyUF = new UnitFacade(ctx);
		boolean vibrate = "1".equals(notifyUF
				.getSetting(UnitFacade.SET_NOTIFY_VIBRATE, "1"));

		boolean sound = "1".equals(notifyUF
				.getSetting(UnitFacade.SET_NOTIFY_SOUND, "1"));


		if (c == null) {
			return;
		}
		boolean hasItem = c.moveToFirst();

		if (!hasItem) {
			return;
		}

		int nameIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME);
		int icoIdx = c.getColumnIndex(ProviderDescriptor.Notify.Cols.TYPE);
		int type = c.getInt(icoIdx);

		int res = (type == ProviderDescriptor.Notify.Type.DATE) ? R.drawable.not_date :
				(type == ProviderDescriptor.Notify.Type.ODOMETER) ? R.drawable.not_odom : R.drawable.notify;


		String name = c.getString(nameIdx);
		String carName = DBUtils.getActiveCarName(ctx.getContentResolver(),
				c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols.CAR_ID)));


		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(ctx)
						.setSmallIcon(res);
		if (sound) {
			mBuilder.setSound(alarmSound);
		}

//		if (vibrate) {
//			mBuilder.setVibrate(new long[] {1000, 500, 1000});
//		}

		mBuilder.setContentTitle(carName)
						.setContentText(name);

		NotificationManager mNotificationManager =
				(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notifyIntent = new Intent(ctx, AddUpdateNotificationActivity.class);
		notifyIntent.putExtra(BaseActivity.MODE_KEY, BaseActivity.PARAM_EDIT);
		notifyIntent.putExtra(BaseActivity.ENTITY_ID, id);
		notifyIntent.putExtra(BaseActivity.NOTIFY_EXTRA, true);
//		notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		stackBuilder.addParentStack(AddUpdateNotificationActivity.class);
//		stackBuilder.addNextIntent(notifyIntent);

		PendingIntent notifyPendingIntent =
				PendingIntent.getActivity(
						ctx,
						(int) id,
						notifyIntent,
						PendingIntent.FLAG_UPDATE_CURRENT
				);

		mBuilder.setContentIntent(notifyPendingIntent);
		mBuilder.setAutoCancel(true);

		mNotificationManager.notify((int) id, mBuilder.build());
	}

	public static void validateDateNotifications(Context ctx) {
		ContentResolver cr = ctx.getContentResolver();
//		long carId = DBUtils.getActiveCarId(cr);
//		String selection = DBUtils.CAR_SELECTION_NOTIFY + " and "
//				+ ProviderDescriptor.Notify.Cols.TYPE + " = ?";

		String selection = ProviderDescriptor.Notify.Cols.TYPE + " = ? or " + ProviderDescriptor.Notify.Cols.TYPE + " = ?";

		Cursor c = cr.query(ProviderDescriptor.Notify.CONTENT_URI,
				null, selection,
				new String[]{String.valueOf(ProviderDescriptor.Notify.Type.DATE),
						String.valueOf(ProviderDescriptor.Notify.Type.DATE_ODOMETER)
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
		validateOdometerNotify(ctx, odmeterValue
				, ProviderDescriptor.Notify.Type.ODOMETER
				,ProviderDescriptor.Notify.Cols.TRIGGER_VALUE);

		validateOdometerNotify(ctx, odmeterValue
				, ProviderDescriptor.Notify.Type.DATE_ODOMETER
				,ProviderDescriptor.Notify.Cols.TRIGGER_VALUE2);

		//DOUBLE CHECK
		NotifyService.checkDateNotifications(ctx);
		CommonUtils.validateDateNotifications(ctx);
	}

	public static void validateOdometerNotify(Context ctx, int odmeterValue,
			int type, String trigerKey) {
		ContentResolver cr = ctx.getContentResolver();
		long carId = DBUtils.getActiveCarId(cr);
		Cursor c = getOdometerNotifyCursor(odmeterValue, cr, carId,
				type,
				trigerKey);

		if (c == null) {
			return;
		}

		while (c.moveToNext()) {
			long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols._ID));
			createNotify(ctx, id);
		}

		c.close();


	}

	private static Cursor getOdometerNotifyCursor(int odmeterValue, ContentResolver cr,
												  long carId, int type, String trigerKey) {
		String selection = DBUtils.CAR_SELECTION_NOTIFY + " and "
				+ ProviderDescriptor.Notify.Cols.TYPE + " = ? and "
				+ trigerKey + " <= ?";
		return cr.query(ProviderDescriptor.Notify.CONTENT_URI,
				null, selection,
				new String[]{String.valueOf(carId),
						String.valueOf(type),
						String.valueOf(odmeterValue)
				}, null
		);
	}

	public static String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(UnitFacade.DATATE_FORMAT);
		return sdf.format(date);
	}

    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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

	public static double getPercent(double value, double total) {
		return (value * 100) / total;
	}

	public static String wrapPt(UnitFacade unitFacade, double value, double total) {
		DecimalFormat f = unitFacade.COMMA_ON ? formatPComma : formatP;
		String result = f.format(getPercent(value, total));
//		String result = String.valueOf(Math.round(getPercent(value, total)));

		return "\n (" + result+ "%)";
	}



	public static double getRawDouble(String text) {

		double result = 0.;
		try {
			result = Double.valueOf(text.trim().replace(',', '.'));
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
			result = format.parse(string.replace(',', '.'));
		} catch (ParseException e) {
			//nothing
		}

		return result.doubleValue();
	}

	public static String formatPrice(double price) {
		NumberFormat format = getPriceNumberFormat();
		String result = format.format(price);
		return "0.0".equals(result) || "0,0".equals(result)  ? "" : result;
	}

    public static String formatPriceNew(double price, UnitFacade unitFacade) {
        DecimalFormat f = unitFacade.COMMA_ON ? formatComma : format;
        String result = f.format(price);
        return "0.0".equals(result) || "0,0".equals(result)  ? "" : result;
    }

	public static String formatDistance(double value) {
		String result = formatDistance.format(value);
		return "0".equals(result) ? "" : result;
	}

    public static String formatFuel(double price, UnitFacade unitFacade) {
        DecimalFormat f = unitFacade.COMMA_ON ? formatFuelComma : formatFuel;
        String result = f.format(price);
        return "0.0".equals(result) || "0,0".equals(result)  ? "" : result;
    }


    public static double div(double a, double b) {
		return (b == 0) ? 0 : a / b;
	}

	private static NumberFormat getPriceNumberFormat() {
		return format;
	}


    public static void trunkYear(Calendar c) {
        trunkMonth(c);
        c.set(Calendar.MONTH, 0);
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

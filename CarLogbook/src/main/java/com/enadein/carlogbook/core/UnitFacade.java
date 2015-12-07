package com.enadein.carlogbook.core;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;

import com.enadein.carlogbook.CarLogbook;
import com.enadein.carlogbook.R;
import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import java.text.NumberFormat;

public class UnitFacade {
	public static final String LOG_DEF = "LOG_DEF_";
	private Context ctx;

	private String[] distanceUnitArray;
	private String[] fuelUnitArray;
	private String[] consumptionUnitArray;

	private String[] dataFormatArray;

	private int distanceValue;
	private int fuelValue;
	private int consumptionValue;

	private String currency;

	public static String DATATE_FORMAT = "yyyy-MM-dd";

	public static boolean ANIM_LIST_ON = false;
	public static boolean COMMA_ON = false;


	public static final String SET_LOG_DEFAULT = "log_default";
	public static final String SET_DATE_FORMAT = "date_format";
	public static final String SET_ANIM_LIST = "anim_list";
	public static final String SET_COMMA = "comma";

	public static final String SET_FRACT_FUEL = "fract_fuel";
//	public static final String SET_FRACT_DIST = "fract_dist";
	public static final String SET_FRACT_CURRENCY = "fract_currency";
	public static final String SET_NOTIFY_TIME = "notify_time";
	public static final String SET_NOTIFY_VIBRATE = "notify_vib";
	public static final String SET_NOTIFY_SOUND = "notify_sound";

	public static float animSize;

    public  String carName = "-";
    public long carId = -1;

	public static int currencyFract = 3;
	public static int fuelFract = 3;
	public static int minFract = 3;

	public UnitFacade(Context ctx) {
		this.ctx = ctx;

		loadDefault(ctx);
	}

	private void loadDefault(Context ctx) {
		currency = NumberFormat.getCurrencyInstance().getCurrency().getSymbol();
		animSize = ctx.getResources().getDimension(R.dimen.anim_list_height);
		distanceUnitArray = ctx.getResources()
				.getStringArray(R.array.unit_distance);

		fuelUnitArray = ctx.getResources()
				.getStringArray(R.array.unit_fuel);

		dataFormatArray = ctx.getResources()
				.getStringArray(R.array.date_format);

		distanceValue = ctx.getResources().getInteger(R.integer.distance_default);
		fuelValue = ctx.getResources().getInteger(R.integer.fuel_default);
		consumptionValue = ctx.getResources().getInteger(R.integer.consumption_default);

		loadConsumptionArrayType();
	}

    public void reload(long carId) {
        reload(carId, false);
    }

    public void reload(long carId, boolean onlyLabels) {

		if (carId != -1) {
			Cursor c = ctx.getContentResolver()
					.query(ProviderDescriptor.Car.CONTENT_URI, null, BaseActivity.SELECTION_ID_FILTER,
							new String[]{String.valueOf(carId)}, null);

			if (c == null) {
				return;
			}

			boolean hasItem = c.moveToFirst();

			if (hasItem) {
				fuelValue = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_FUEL));
				distanceValue = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_DISTANCE));
				consumptionValue = c.getInt(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION));
				currency = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CURRENCY));

				loadConsumptionArrayType();
			}
		} else {
			loadDefault(ctx);
		}

        if (onlyLabels) {
            return;
        }

        carId = DBUtils.getActiveCarId(ctx.getContentResolver());
        carName = DBUtils.getActiveCarName(ctx.getContentResolver(), carId);

        if (carName == null || carName.length() == 0) {
            carName = ctx.getString(R.string.no_car);
        }


		invalidateAll();
	}

	public void invalidateAll() {
		invalidateDateFormat();
		invalidateFlags();

		fuelFract =  Integer.valueOf(getSetting(SET_FRACT_FUEL, "3"));
		currencyFract =  Integer.valueOf(getSetting(SET_FRACT_CURRENCY, "3"));
		CommonUtils.setupDecimalFormat();
	}

	public void refreshNotifySystem(Context ctx) {
		CommonUtils.validateDateNotifications(ctx);
	}

	private void loadConsumptionArrayType() {
		int consumptionId = CommonUtils.getConsumptionArrayId(distanceValue, fuelValue);

		consumptionUnitArray = ctx.getResources()
				.getStringArray(consumptionId);
	}

	public int getDistanceValue() {
		return distanceValue;
	}

	public int getFuelValue() {
		return fuelValue;
	}

	public int getConsumptionValue() {
		return consumptionValue;
	}

	public String getCurrency() {
		return currency;
	}

	public void appendCurrency(TextView textView, boolean wrap) {
		String currentText = textView.getText() != null ? textView.getText().toString() : "";
		textView.setText(appendCurrency(wrap, currentText));
	}

	public String appendCurrency(boolean wrap, String currentText) {
		return getText(currentText, currency, wrap);
	}

    public void appendCurrency(TextView textView, boolean wrap, boolean na) {
        String currentText = textView.getText() != null ? textView.getText().toString() : "";
        textView.setText(getText(currentText, currency, wrap, na));
    }


	public void appendFuelUnit(TextView textView, boolean wrap) {
		String currentText = textView.getText() != null ? textView.getText().toString() : "";
		textView.setText(appendFuelUnit(wrap, currentText));
	}

	public String appendFuelUnit(boolean wrap, String currentText) {
		return getText(currentText, fuelUnitArray[fuelValue], wrap);
	}

	public void appendDistUnit(TextView textView, boolean wrap) {
		String currentText = textView.getText() != null ? textView.getText().toString() : "";
		textView.setText(appendDistUnit(wrap, currentText));
	}

	public String appendDistUnit(boolean wrap, String currentText) {
		return getText(currentText, distanceUnitArray[distanceValue], wrap);
	}

	public String getDistUnit() {
		return  distanceUnitArray[distanceValue];
	}

	public void appendConsumUnit(TextView textView, boolean wrap) {
		String currentText = textView.getText() != null ? textView.getText().toString() : "";
		textView.setText(appendConsumUnit(wrap, currentText));
	}

    public void appendConsumUnit(TextView textView, boolean wrap, int index) {
        String currentText = textView.getText() != null ? textView.getText().toString() : "";
        textView.setText(appendConsumUnit(wrap, currentText, index));
    }

	public void appendConsumValue(TextView textView, boolean wrap) {
		String currentText = textView.getText() != null ? textView.getText().toString() : "";
		String value;
		if (consumptionValue == 0 || consumptionValue == 1) {
			value = fuelUnitArray[fuelValue];
		} else {
			value = distanceUnitArray[distanceValue];
		}
		textView.setText(getText(currentText, value, wrap));
	}

	public String getConsumPostfix() {
		String value;
		if (consumptionValue == 0 || consumptionValue == 1) {
			value = fuelUnitArray[fuelValue];
		} else {
			value = distanceUnitArray[distanceValue];
		}
		return value;
	}

	public String appendConsumUnit(boolean wrap, String currentText) {
		return getText(currentText, consumptionUnitArray[consumptionValue], wrap);
	}

    public String appendConsumUnit(boolean wrap, String currentText, int index) {
        return getText(currentText, consumptionUnitArray[index], wrap);
    }
    public String getText(String currentText, String value, boolean wrap) {
        return getText(currentText, value, wrap, true);
    }

    public String getText(String currentText, String value, boolean wrap, boolean na) {
        if (na) {
            if (currentText == null || currentText.trim().length() == 0
                    || CommonUtils.formatFuel(0, this).equals(currentText)
                    || CommonUtils.formatPriceNew(0, this).equals(currentText)) {
                return ctx.getString(R.string.na);
            }
        }
		
		if ((wrap && currentText.endsWith("(" + value + ")")) || (!wrap && currentText.endsWith(value))) {
			return currentText;
		} else {
			return wrap ? currentText + " (" + value + ")" : currentText + value;
		}
    }

    public void setFuelValue(int fuelValue) {
        this.fuelValue = fuelValue;
    }

    public void setConsumptionValue(int consumptionValue) {
        this.consumptionValue = consumptionValue;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getRate(double allFuel, int odometerCount) {
		double result = 0.0d;

		switch (consumptionValue) {
			case 0: {
				result = (allFuel / odometerCount) * 100;
				break;
			}
			case 1: {
				result = (allFuel / odometerCount);
				break;
			}
			case 2: {
				result = odometerCount /allFuel;
				break;
			}
		}
		return result;
	}

	public void setSetings(String key, String value) {
		ContentResolver cr = ctx.getContentResolver();
		String currentValue = DBUtils.getSettValue(cr, key);
		if (currentValue == null) {
			DBUtils.createSetValue(cr, key, value);
		} else {
			DBUtils.updateSetValue(cr, key, value);
		}
	}

	public String getSetting(String key) {
		ContentResolver cr = ctx.getContentResolver();
		return DBUtils.getSettValue(cr, key);
	}

	public String getSetting(String key, String defaultValue) {
		ContentResolver cr = ctx.getContentResolver();
		String value = DBUtils.getSettValue(cr, key);
		return value == null ? defaultValue : value;
	}

	public String getLogDefault(int type) {
		ContentResolver cr = ctx.getContentResolver();
		String value = DBUtils.getSettValue(cr, LOG_DEF + type);
		return value == null ? "" : value;
	}

	public void setLogDefault(int type, String value) {
		setSetings(LOG_DEF + type, value);
	}



	public void invalidateDateFormat() {
		String value = DBUtils.getSettValue(ctx.getContentResolver(), SET_DATE_FORMAT);

		if (value == null) {
			value = "0";
		}

		DATATE_FORMAT = dataFormatArray[Integer.valueOf(value)];
	}


    public  String getCarName() {
        return carName == null ? "-" : carName;
    }

    public  void setCarName(String carName) {
        this.carName = carName;
    }

    public void invalidateFlags() {
		ANIM_LIST_ON = "1".equals(getSetting(SET_ANIM_LIST, "1"));
        COMMA_ON = "1".equals(getSetting(SET_COMMA, "0"));
	}


    public double getTotalFuel(double consum, double dist) {
        double result = .0;

        switch (consumptionValue) {
            case 0: {
                result = (dist / 100) * consum;
                break;
            }
            case 1: {
                result = consum * dist;
                break;
            }
            case 2: {
                result = dist / consum;
                break;
            }
        }

        return result;
    }

}

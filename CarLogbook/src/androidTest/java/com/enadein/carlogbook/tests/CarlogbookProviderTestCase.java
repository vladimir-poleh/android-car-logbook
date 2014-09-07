package com.enadein.carlogbook.tests;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.test.ProviderTestCase2;

import com.enadein.carlogbook.db.CarLogbookProvider;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import junit.framework.Assert;

import java.util.Calendar;
import java.util.Date;

public class CarlogbookProviderTestCase extends ProviderTestCase2<CarLogbookProvider> {
	public static final String CAR = "CAR";
	public static final String CAR_2 = "CAR2";
	private ContentResolver cr;

	public static final long DAY = 60*60*24*1000;
	public static final long MONTH = DAY * 31;
	public static final long YEAR =  MONTH * 12;

	public CarlogbookProviderTestCase() {

		super(CarLogbookProvider.class, "com.enadein.carlogbook");
	}

	public void testDeleteCascadeCar() {
		addTestDefaultCar(CAR, 1);
		long carId = DBUtils.getActiveCarId(cr);
		setupFuelLogBasic(DAY);
		addNotify();

		Assert.assertTrue(DBUtils.getCount(cr, ProviderDescriptor.Log.CONTENT_URI, DBUtils.CAR_SELECTION,
				new String[] {String.valueOf(carId)}) > 0);

		Assert.assertTrue(DBUtils.getCount(cr, ProviderDescriptor.Notify.CONTENT_URI, DBUtils.CAR_SELECTION_NOTIFY,
				new String[] {String.valueOf(carId)}) > 0);

		DBUtils.deleteCascadeCar(cr, carId);

		Assert.assertTrue(DBUtils.getCount(cr, ProviderDescriptor.Log.CONTENT_URI, DBUtils.CAR_SELECTION,
				new String[] {String.valueOf(carId)}) == 0);

		Assert.assertTrue(DBUtils.getCount(cr, ProviderDescriptor.Notify.CONTENT_URI, DBUtils.CAR_SELECTION_NOTIFY,
				new String[] {String.valueOf(carId)}) == 0);

	}

	private void addNotify() {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Notify.Cols.NAME, "Test");
		long carId = DBUtils.getActiveCarId(cr);
		cv.put(ProviderDescriptor.Notify.Cols.CAR_ID, carId);

		cv.put(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE, System.currentTimeMillis());
		cv.put(ProviderDescriptor.Notify.Cols.TYPE, ProviderDescriptor.Notify.Type.DATE);
		cr.insert(ProviderDescriptor.Notify.CONTENT_URI, cv);
	}

	public void testIsUsedInLog() {
		addTestDefaultCar(CAR, 1);

		long stationId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.STATION);
		long fuelTypeId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.FUEL);

		Assert.assertFalse(DBUtils.isStationUsed(cr, stationId));
		Assert.assertFalse(DBUtils.isFuelTypeUsed(cr, fuelTypeId));

		setupFuelLogBasic(DAY);

		Assert.assertTrue(DBUtils.isStationUsed(cr, stationId));
		Assert.assertTrue(DBUtils.isFuelTypeUsed(cr, fuelTypeId));

	}


	public void testActiveCar() {
		long carId = DBUtils.getActiveCarId(cr);
		assertEquals(-1, carId);
		addTestDefaultCar(CAR, 1);
		carId = DBUtils.getActiveCarId(cr);
		assertNotSame(-1, carId);
		assertEquals(DBUtils.getCount(cr, ProviderDescriptor.Car.CONTENT_URI), 1);
		addTestDefaultCar(CAR_2, 0);
		assertEquals(DBUtils.getCount(cr, ProviderDescriptor.Car.CONTENT_URI), 2);
	}

	public void testAvgFuel() {
		addTestDefaultCar(CAR, 1);
		addOtherLog(100, new Date().getTime(), 10., 1);
		setupFuelLogBasic(DAY);
		double avg = DBUtils.getAvgFuel(cr, 0, 0);

		double expected = (52. / 510) * 100;
		Assert.assertEquals(expected, avg);

	}

	public void testGetOdometerCountSimple() {
		addTestDefaultCar(CAR, 1);
		setupFuelLogBasic(DAY);
		Assert.assertEquals(3, DBUtils.getCount(cr,ProviderDescriptor.LogView.CONTENT_URI));
		Assert.assertEquals(510, DBUtils.getOdometerCount(cr));

		addTestDefaultCar(CAR_2, 0);
		selectDefaultCar(CAR_2);

		Assert.assertEquals(0, DBUtils.getOdometerCount(cr));

		setupFuelLogBasic(DAY);
		Assert.assertEquals(510, DBUtils.getOdometerCount(cr));

	}

	public void testAllPrice() {
		addTestDefaultCar(CAR, 1);
//		setupFuelLogBasic(DAY);

		addTestDefaultCar(CAR_2, 0);
		selectDefaultCar(CAR_2);

		double total = DBUtils.getTotalPrice(cr);
		Assert.assertEquals(0.0, total);

		setupFuelLogBasic(DAY);

		total = DBUtils.getTotalPrice(cr);
		Assert.assertEquals(1020.9, total);

		double per1km = DBUtils.getPricePer1km(cr, 0, 0);
		int odometer = DBUtils.getOdometerCount(cr);

		Assert.assertEquals(((10.45d * 2) + (50 * 10d)) / odometer, per1km);

		addOtherLog(2000, System.currentTimeMillis(), 200, 0);

		 total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(1020.9, total); //The same

		total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.OTHER);
		Assert.assertEquals(200.0, total);


		total = DBUtils.getTotalPrice(cr, 0, 0, -1);
		Assert.assertEquals(1220.9, total); //TOGETHER

	}

	public void testOdometerCount() {
		addTestDefaultCar(CAR, 1);

		long stationId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.STATION);
		long fuelTypeId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.FUEL);

		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, 1);

		long jun = c.getTimeInMillis();

		//add 1 jun
		addFuelLog(1000, jun , 50d, 10d, fuelTypeId, stationId);

		int odometerCount = DBUtils.getOdometerCount(cr, 0, 0, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(0, odometerCount);

		double totalPrice = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals((50.0 * 10.0), totalPrice );

		//add 2 jun
		addFuelLog(1200, jun , 50d, 10d, fuelTypeId, stationId);

		odometerCount = DBUtils.getOdometerCount(cr, 0, 0, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(200, odometerCount);

		totalPrice = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals((50.0 * 10.0) * 2, totalPrice );

		c.add(Calendar.MONTH, 1);

		long feb = c.getTimeInMillis() ;

		//add 3 feb
		addFuelLog(1400, feb + DAY, 50d, 10d, fuelTypeId, stationId);


		odometerCount = DBUtils.getOdometerCount(cr, jun, feb, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(200, odometerCount);


		totalPrice = DBUtils.getTotalPrice(cr, jun, feb, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals((50.0 * 10.0) * 2, totalPrice ); // from jun (2)

		c.add(Calendar.MONTH, 1);

		long mar = c.getTimeInMillis() ;

		odometerCount = DBUtils.getOdometerCount(cr, feb, mar, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(200, odometerCount);

		totalPrice = DBUtils.getTotalPrice(cr, feb, mar, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals((50.0 * 10.0), totalPrice ); //1 item

		//add 4 feb
		addFuelLog(1800, feb + DAY, 50d, 10d, fuelTypeId, stationId);
		odometerCount = DBUtils.getOdometerCount(cr, feb, mar, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals(600, odometerCount);

		totalPrice = DBUtils.getTotalPrice(cr, feb, mar, ProviderDescriptor.Log.Type.FUEL);
		Assert.assertEquals((50.0 * 10.0) * 2, totalPrice ); //2 item
	}

	public void testAllFuel() {
		addTestDefaultCar(CAR, 1);
		addOtherLog(10, new Date().getTime(), 20., -1);
		setupFuelLogBasic(DAY);
		int total = DBUtils.getTotalFuel(cr, 0, 0, false);
		Assert.assertEquals(102, total);
		total = DBUtils.getTotalFuel(cr, 0, 0, true);
		Assert.assertEquals(52, total);
	}

	private void setupFuelLogBasic(long dx) {
		Date date = new Date();
		long stationId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.STATION);
		long fuelTypeId = DBUtils.getDefaultId(cr, ProviderDescriptor.DataValue.Type.FUEL);
		long dateLong = date.getTime();

		addFuelLog(1000, dateLong, 50d, 10d, fuelTypeId, stationId);
		addFuelLog(1500, dateLong + dx, 50d, 10d, fuelTypeId, stationId);
		addFuelLog(1510, dateLong + dx + dx, 2d, 10.45d, fuelTypeId, stationId);

	}

	private void addFuelLog(int odometer, long date, double fuelVolume, double price, long fuelType, long station) {
		long carId = DBUtils.getActiveCarId(cr);

		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Log.Cols.ODOMETER, odometer);
		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);
		cv.put(ProviderDescriptor.Log.Cols.FUEL_VOLUME, fuelVolume);
		cv.put(ProviderDescriptor.Log.Cols.PRICE, price);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.FUEL);
		cv.put(ProviderDescriptor.Log.Cols.DATE, date);
		cv.put(ProviderDescriptor.Log.Cols.FUEL_TYPE_ID, fuelType);
		cv.put(ProviderDescriptor.Log.Cols.FUEL_STATION_ID, station);

		cr.insert(ProviderDescriptor.Log.CONTENT_URI, cv);
	}

	private void addOtherLog(int odometer, long date, double price, int type) {
		long carId = DBUtils.getActiveCarId(cr);

		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Log.Cols.ODOMETER, odometer);
		cv.put(ProviderDescriptor.Log.Cols.CAR_ID, carId);
		cv.put(ProviderDescriptor.Log.Cols.PRICE, price);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, ProviderDescriptor.Log.Type.OTHER);
		cv.put(ProviderDescriptor.Log.Cols.DATE, date);
		cv.put(ProviderDescriptor.Log.Cols.TYPE_ID, type);

		cr.insert(ProviderDescriptor.Log.CONTENT_URI, cv);
	}

	public void testOtherTypesFilter() {
		addTestDefaultCar(CAR, 1);
		addOtherLog(10, new Date().getTime(), 20., 1);
		addOtherLog(20, new Date().getTime(), 10., 1);
		addOtherLog(30, new Date().getTime(), 40., 2);
		addOtherLog(30, new Date().getTime(), 15., 3);

		{
			int types[] = new int[]{1};
			double total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.OTHER, types);
			Assert.assertEquals(30., total);
		}

		{
			int types[] = new int[]{1, 2};
			double total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.OTHER, types);
			Assert.assertEquals(70., total);
		}

		{
			int types[] = new int[]{1, 2, 3};
			double total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.OTHER, types);
			Assert.assertEquals(85., total);
		}

		{
			int types[] = new int[]{ 3};
			double total = DBUtils.getTotalPrice(cr, 0, 0, ProviderDescriptor.Log.Type.OTHER, types);
			Assert.assertEquals(15., total);
		}
	}



	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cr = getMockContentResolver();
	}

	private void addTestDefaultCar(String name, int active) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.NAME, name);
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, active);
		cr.insert(ProviderDescriptor.Car.CONTENT_URI,  cv);
	}

	private void selectDefaultCar(String name) {
		ContentValues cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 0);
		cr.update(ProviderDescriptor.Car.CONTENT_URI, cv, "1 = 1",null);

		cv = new ContentValues();
		cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 1);
		cr.update(ProviderDescriptor.Car.CONTENT_URI, cv, ProviderDescriptor.Car.Cols.NAME + " = ?",  new String[] {name});
	}
}

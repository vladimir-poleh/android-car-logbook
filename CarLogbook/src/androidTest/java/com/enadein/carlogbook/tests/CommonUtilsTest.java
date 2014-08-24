package com.enadein.carlogbook.tests;

import android.test.suitebuilder.annotation.MediumTest;

import com.enadein.carlogbook.db.CommonUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

public class CommonUtilsTest extends TestCase {

	@MediumTest
	public void testBasicOperations() {
		assertTrue(CommonUtils.isNotEmpty("test"));
		assertFalse(CommonUtils.isNotEmpty(""));
		assertFalse(CommonUtils.isNotEmpty("  "));
		assertFalse(CommonUtils.isNotEmpty(null));
		assertTrue(CommonUtils.isEmpty(null));
		assertTrue(CommonUtils.isEmpty(" "));
		assertTrue(CommonUtils.isEmpty("  "));
		assertFalse(CommonUtils.isEmpty("t  "));

//		assertEquals("24,45", CommonUtils.formatPrice(24.45));
//		assertEquals(24.45, CommonUtils.getPriceValue("24,45", 0));
		assertEquals(0.0, CommonUtils.div(2, 0));
	}

	public void testDate() {
		Calendar mar = getDate(2012, 2, 1);

		String dateString = CommonUtils.formatDate(new Date(mar.getTimeInMillis()));
		Assert.assertEquals("2012-03-01",  dateString);

		CommonUtils.trunkDay(mar);
		Assert.assertEquals(0, mar.get(Calendar.HOUR));
		Assert.assertEquals(0, mar.get(Calendar.MINUTE));
		Assert.assertEquals(0, mar.get(Calendar.SECOND));

		Calendar jun = getDate(2012, 0, 1);
		long junLong = jun.getTimeInMillis(); //febb not trunked
		CommonUtils.trunkDay(jun);

		Assert.assertTrue(junLong > jun.getTimeInMillis());

		jun = getDate(2012, 0, 1);
		jun.add(Calendar.MONTH, 1);
		long febLong = jun.getTimeInMillis(); // feb not trunked

		CommonUtils.trunkDay(jun); //trunk first day of feb

		Assert.assertTrue(febLong > jun.getTimeInMillis());

		Assert.assertEquals("2012-02-01",  CommonUtils.formatDate(new Date(jun.getTimeInMillis())));

	}

	public static Calendar getDate(int year, int month, int day) {
		Calendar date =  Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);
		date.set(Calendar.HOUR, 5);
		date.set(Calendar.MINUTE, 45);
		date.set(Calendar.SECOND, 25);
		return date;
	}
}

package com.enadein.carlogbook.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.enadein.carlogbook.ui.AddUpdateCarActivity;
import com.jayway.android.robotium.solo.Solo;


public class TemplateTestCase extends ActivityInstrumentationTestCase2<AddUpdateCarActivity> {
	private Solo solo;

	public TemplateTestCase() {
		super(AddUpdateCarActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}


	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

	public void testAssertTest() {
		assertTrue(true);

	}
}

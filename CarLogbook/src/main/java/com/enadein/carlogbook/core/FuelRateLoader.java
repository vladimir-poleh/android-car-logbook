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

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.enadein.carlogbook.db.CommonUtils;
import com.enadein.carlogbook.db.DBUtils;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class FuelRateLoader {
	private ExecutorService executorService;
	private Context ctx;
	private UnitFacade unitFacade;

	private Map<TextView, String> views = Collections.synchronizedMap
			(new WeakHashMap<TextView, String>());

	public FuelRateLoader(UnitFacade unitFacade, Context ctx) {
		this.ctx = ctx;
		this.unitFacade = unitFacade;
		executorService = Executors.newFixedThreadPool(3, new LowPriorityThreadFactory());
	}

	class LowPriorityThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return  thread;
		}
	}

	public void calculateFuelRate(TextView textView, long currentLogId) {
		textView.setText("");
		views.put(textView, "");
		LoaderTask task = new LoaderTask(textView, currentLogId);
		executorService.submit(task);
	}

	private class LoaderTask implements Runnable {
		private TextView textView;
		private long id;

		private LoaderTask(TextView textView, long id) {
			this.textView = textView;
			this.id = id;
		}

		@Override
		public void run() {
			try {
				final String text = getFuelRate();


				String placeHolder = views.get(textView);

				if (placeHolder != null) {
					((Activity) textView.getContext()).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							textView.setText(text);
						}
					});
				}
			} catch (Throwable t) {

			}
		}

		private String getFuelRate() {
			double rate = DBUtils.getFuelRateFromCurrentLogId(unitFacade, id, ctx.getContentResolver());


			int consum = unitFacade.getConsumptionValue();
			String rateString = (consum == 2) ? CommonUtils.formatDistance(rate) :
					CommonUtils.formatFuel(rate, unitFacade);

			rateString = unitFacade.appendConsumUnit(true, rateString);

			if (rate == 0) {
				rateString = "";
			}

			return rateString;
		}
	}
}

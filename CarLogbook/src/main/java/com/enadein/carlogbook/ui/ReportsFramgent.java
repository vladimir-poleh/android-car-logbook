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
package com.enadein.carlogbook.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.enadein.carlogbook.R;
import com.enadein.carlogbook.core.BaseFragment;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

public class ReportsFramgent extends BaseFragment  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.reports_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		PieGraph pg = (PieGraph) view.findViewById(R.id.graph);

		pg.setInnerCircleRatio(140);
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#99CC00"));
		slice.setValue(1);
		slice.setTitle("HEllo");
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FFBB33"));
		slice.setValue(35);
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#AA66CC"));
		slice.setValue(35);
		pg.addSlice(slice);

		for (PieSlice s : pg.getSlices())
			s.setGoalValue(10);
		pg.setDuration(1000);//default if unspecified is 300 ms
		pg.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
//		pg.setAnimationListener(getAnimationListener());//optional
		pg.animateToGoalValues();
	}

	@Override
	public String getSubTitle() {
		return getString(R.string.menu_item_reports);
	}


}

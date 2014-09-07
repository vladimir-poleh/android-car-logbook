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
package com.enadein.carlogbook.ui.fix;


import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

public class FixedDrawer  extends DrawerLayout {

	public FixedDrawer(Context context) {
		super(context);
	}

	public FixedDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FixedDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	boolean isContentView(View child) {
		if(child == null){
			return false;
		}
		return ((DrawerLayout.LayoutParams) child.getLayoutParams()).gravity == Gravity.NO_GRAVITY;
	}
}

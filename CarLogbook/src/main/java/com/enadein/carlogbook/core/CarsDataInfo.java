package com.enadein.carlogbook.core;


import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;

public class CarsDataInfo {
	private Cursor cursor;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
}

package com.enadein.carlogbook.core.gen;


import android.content.ContentValues;

import java.io.File;

public interface GenWriter {
	public boolean start();
	public void writeCarName(String name);
	public void writeText(String text, String color);
	public void startTable();
	public void startRow();
	public void startCell();
	public void endRow();
	public void endCell();
	public void endTable();
	public void end();
	public void newLine();
	public File getPath();
}

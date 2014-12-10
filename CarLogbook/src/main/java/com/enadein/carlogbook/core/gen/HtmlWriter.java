package com.enadein.carlogbook.core.gen;

import android.content.ContentValues;

import com.enadein.carlogbook.core.backup.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HtmlWriter implements GenWriter {
	private BufferedWriter w;
	private File file;

	@Override
	public boolean start() {
		File dir = FileUtils.getReportDir();
		String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	 	file = new File(dir, timeLog + ".html");

		try {
			w = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			w.write("<html><head><meta charset=\"UTF-8\"></head><body>");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	public void writeCarName(String name) {
		try {
			w.write("<h1>" + name + "</h1>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeText(String text, String color) {
		try {
			w.write("<span style='color:"+ color + ";'>" + text + "</span>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startTable() {
		try {
			w.write("<div style='display:table;'>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startRow() {
		try {
			w.write("<div style='display:table-row;'>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startCell() {
		try {
			w.write("<div style='display:table-cell;padding: 4pt;text-align: right;'>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endRow() {
		endDiv();
	}

	@Override
	public void endCell() {
		endDiv();
	}

	@Override
	public void endTable() {
		endDiv();
	}

	public void endDiv() {
		try {
			w.write("</div>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void end() {
		if (w != null) {
			try {
				w.write("</body></html>");
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void newLine() {
		try {
			w.write("<br/>");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public File getPath() {
		return file;
	}
}

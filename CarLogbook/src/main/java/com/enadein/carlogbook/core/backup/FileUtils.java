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
package com.enadein.carlogbook.core.backup;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	private static final String BACK_UP_DIR = "CarLogbook";

	public static void cleanDir() {
		File baseDir = getBackupDirectory();
		for(File file: baseDir.listFiles()) file.delete();
	}

	public static boolean exist(String name, String ext) {
		File externalStorage = Environment.getExternalStorageDirectory();
		File exportDir = new File(externalStorage, BACK_UP_DIR);
		return new File(exportDir, name + "." + ext).exists();
	}


	public static File[] getFiles() {

		File[] files = getBackupDirectory().listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isDirectory() &&
						file.getName().contains(XMLImportExportStrategy.EXTENSION);
			}
		});

		if (files == null) {
			files = new File[0];
		}

		return files;
	}

	public static  File getBackupDirectory() {

		File externalStorage = Environment.getExternalStorageDirectory();
		File exportDir = new File(externalStorage, BACK_UP_DIR);


		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

		return exportDir;
	}


	public static void copyStrem(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while (len != -1) {
			out.write(buffer, 0, len);
			len = in.read(buffer);
		}
	}

	public static InputStream openIn(String fileName) throws FileNotFoundException {
		File file = new File(getBackupDirectory(), fileName + ".xml");
		FileInputStream is = new FileInputStream(file);

		return new BufferedInputStream(is);
	}

	public static void createFile(String fileName) throws IOException {
		File file = new File(getBackupDirectory(), fileName + ".xml");
		file.createNewFile();
	}

	public static void deleteFile(String fileName) {
		File file = new File(getBackupDirectory(), fileName + ".xml");
		if (file.exists()) {
			file.delete();
		}
	}

	public static OutputStream openOut(String fileName) throws FileNotFoundException {
		File file = new File(getBackupDirectory(), fileName + ".xml");
		FileOutputStream out = new FileOutputStream(file);

		return new BufferedOutputStream(out);
	}

	public static  void closeIn(InputStream c) {
		try {
			c.close();
		} catch (IOException e) {
			//noting
		}
	}

	public static void closeOut(OutputStream os) {
		try {
			os.close();
		} catch (IOException e) {
			//nothing
		}
	}



}

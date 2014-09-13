package com.enadein.carlogbook.core.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import com.enadein.carlogbook.core.UnitFacade;
import com.enadein.carlogbook.db.DBUtils;
import com.enadein.carlogbook.db.ProviderDescriptor;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class XMLImportExportStrategy implements ImportExportStrategy {
	public static final String EXTENSION = "xml";
	public static final String TAG = "XML I/E";

	private static final String DEFAULT_ENCODING = "utf-8";
	private static final String DEFAULT_NAMESPACE = "";

	public static final String CAR_LOGBOOK_TAG = "CAR_LOGBOOK";
	public static final String EXPORT_VERSION_NAME = "ver";
	public static final String EXPORT_VERSION_VALUE = "2";
	public static final String ITEM_TAG = "ITEM_TAG";
	public static final String CAR_TAG = "CAR";
	public static final String DATA_VALUE_TAG = "DATA_VALUE";
	public static final String LOG_TAG = "LOG";
	public static final String NOTIFY_TAG = "NOTIFY";
	public static final String RATE_TAG = "RATE";

	public static final String SET_TAG = "SET";


	public static final String EXIST = "exist";

	public String impVer = "0";

	private HashMap<String, String> importContext = new HashMap<String, String>();

	public static final String DV_KEY = "DV_";
	public static final String CAR_KEY = "CAR_";

	private ContentResolver cr;
	private UnitFacade unitFacade;

	public XMLImportExportStrategy(Context ctx) {
		this.cr = ctx.getContentResolver();
		unitFacade = new UnitFacade(ctx);
	}

	@Override
	public boolean importData(String name, boolean reset) {
		boolean result = false;

		//TODO
//		exportData("auto_backup" + System.currentTimeMillis());

		if (reset) {
			cr.delete(ProviderDescriptor.DataValue.CONTENT_URI, "_id != -1", null);
			cr.delete(ProviderDescriptor.Notify.CONTENT_URI, "_id != -1", null);
			cr.delete(ProviderDescriptor.FuelRate.CONTENT_URI, "_id != -1", null);
			cr.delete(ProviderDescriptor.Car.CONTENT_URI, "_id != -1", null);
			cr.delete(ProviderDescriptor.Log.CONTENT_URI, "_id != -1", null);
		}

		File backUpDir = FileUtils.getBackupDirectory();
		File file = new File(backUpDir, name + "." + EXTENSION);

		BufferedReader reader = null;

		try {

			InputStream is = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(is));
			ImportHandler handler = new ImportHandler();
			Xml.parse(reader, handler);

			//TODO refresh data imported

			if ("1".equals(impVer)) {
				ContentValues cv = new ContentValues();
				cv.put(ProviderDescriptor.Car.Cols.UNIT_FUEL, unitFacade.getFuelValue());
				cv.put(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION, unitFacade.getConsumptionValue());
				cv.put(ProviderDescriptor.Car.Cols.UNIT_DISTANCE, unitFacade.getDistanceValue());
				cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY, unitFacade.getCurrency());

				cr.update(ProviderDescriptor.Car.CONTENT_URI, cv, null, null);
			}

//			unitFacade.reload(DBUtils.getActiveCarId(cr));
			result = true;
		} catch (java.io.IOException e) {
			Log.e(TAG, "Error" + file.getName());
		} catch (SAXException e) {
			Log.e(TAG, "Error" + file.getName());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) { /*nothing*/}
			}
		}

		return result;
	}

	public class ImportHandler extends DefaultHandler {
		private HashMap<String, Importer> importers = new HashMap<String, Importer>();

		public ImportHandler() {
			importers.put(DATA_VALUE_TAG, new DataValueImporter());
			importers.put(CAR_TAG, new CarImporter());
			importers.put(LOG_TAG, new LogImporter());
			importers.put(NOTIFY_TAG, new NotifyImporter());
			importers.put(RATE_TAG, new FuelRateImporter());
			//1.2
			importers.put(RATE_TAG, new SettingsImporter());
		}

		private Importer importer = null;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (ITEM_TAG.equals(localName) && importer != null) {
				importer.importData(attributes);
			} else {
				importer = importers.get(localName);
			}

			if (CAR_LOGBOOK_TAG.equals(localName)) {
				impVer = attributes.getValue(attributes.getIndex(EXPORT_VERSION_NAME));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (!ITEM_TAG.equals(localName) && importer != null) {
				importer.flush();
				importer = null;
			}
		}
	}

	private interface Importer {
		public void importData(Attributes attributes);

		public void flush();
	}

	private class SettingsImporter implements Importer {

		@Override
		public void importData(Attributes attributes) {
			{
				String key = attributes.getValue(attributes.getIndex(ProviderDescriptor.Sett.Cols.KEY));
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Sett.Cols.VALUE));

				if (key != null && value != null) {
					unitFacade.setSetings(key, value);
				}
			}
		}

		@Override
		public void flush() {

		}
	}

	private class DataValueImporter implements Importer {

		@Override
		public void importData(Attributes attributes) {
			ContentValues cv = new ContentValues();

			String innerKey = attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols._ID));

//			cv.put(ProviderDescriptor.DataValue.Cols._ID,
//					attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols._ID)));

			cv.put(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG)));

			cv.put(ProviderDescriptor.DataValue.Cols.SYSTEM,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols.SYSTEM)));

			String name = attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols.NAME));
			cv.put(ProviderDescriptor.DataValue.Cols.NAME, name);

			String type = attributes.getValue(attributes.getIndex(ProviderDescriptor.DataValue.Cols.TYPE));
			cv.put(ProviderDescriptor.DataValue.Cols.TYPE, type);


			long id = DBUtils.getValueId(cr, ProviderDescriptor.DataValue.CONTENT_URI,
					ProviderDescriptor.DataValue.Cols.NAME, name);

			if (id == -1) {
				Uri uri = cr.insert(ProviderDescriptor.DataValue.CONTENT_URI, cv);

				if (uri != null) {
					id = Long.valueOf(uri.getLastPathSegment());
				}
			}

			if (id > 0) {
				importContext.put(DV_KEY + type + innerKey, String.valueOf(id));
//				Log.e(TAG, "ud " +id);
			}
		}

		@Override
		public void flush() {

		}
	}

	private class CarImporter implements Importer {


		@Override
		public void importData(Attributes attributes) {
//			Log.e(TAG, "CAR" + attributes.getLocalName(0));

			String innerKey = attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols._ID));

			ContentValues cv = new ContentValues();

			long activeId = DBUtils.getValueId(cr, ProviderDescriptor.Car.CONTENT_URI,
					ProviderDescriptor.Car.Cols.ACTIVE_FLAG, "1");

			if (activeId == -1) {
				cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG,
						attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.ACTIVE_FLAG)));
			} else {
				cv.put(ProviderDescriptor.Car.Cols.ACTIVE_FLAG, 0);
			}

			cv.put(ProviderDescriptor.Car.Cols.NAME,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.NAME)));

			//1.2
			cv.put(ProviderDescriptor.Car.Cols.UNIT_CURRENCY,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.UNIT_CURRENCY)));
			cv.put(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION)));
			cv.put(ProviderDescriptor.Car.Cols.UNIT_FUEL,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.UNIT_FUEL)));
			cv.put(ProviderDescriptor.Car.Cols.UNIT_DISTANCE,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.UNIT_DISTANCE)));
			//1.2

			String uuid = attributes.getValue(attributes.getIndex(ProviderDescriptor.Car.Cols.UUID));

			cv.put(ProviderDescriptor.Car.Cols.UUID, uuid);

			long id = DBUtils.getValueId(cr, ProviderDescriptor.Car.CONTENT_URI,
					ProviderDescriptor.Car.Cols.UUID, uuid);

			Log.e("XXY", "" + uuid + " / " + id);

			if (id == -1) {
				Uri uri = cr.insert(ProviderDescriptor.Car.CONTENT_URI, cv);
				if (uri != null) {
					id = Long.valueOf(uri.getLastPathSegment());
				}
			} else {
				importContext.put(CAR_KEY + innerKey + EXIST, "1");
			}

			importContext.put(CAR_KEY + innerKey, String.valueOf(id));
		}

		@Override
		public void flush() {

		}
	}

	private class LogImporter implements Importer {

		@Override
		public void importData(Attributes attributes) {
			ContentValues cv = new ContentValues();

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.NAME));
				cv.put(ProviderDescriptor.Log.Cols.NAME, value);
			}

			String date = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.DATE));
			cv.put(ProviderDescriptor.Log.Cols.DATE, date);

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.ODOMETER));
				cv.put(ProviderDescriptor.Log.Cols.ODOMETER, value);
			}

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.PRICE));
				cv.put(ProviderDescriptor.Log.Cols.PRICE, value);
			}

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.CMMMENT));
				cv.put(ProviderDescriptor.Log.Cols.CMMMENT, value);
			}

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.TYPE_LOG));
				cv.put(ProviderDescriptor.Log.Cols.TYPE_LOG, value);
			}

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.FUEL_VOLUME));
				cv.put(ProviderDescriptor.Log.Cols.FUEL_VOLUME, value);
			}

			{
				String value = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.TYPE_ID));
				cv.put(ProviderDescriptor.Log.Cols.TYPE_ID, value);
			}

			{
				String innerValue = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.FUEL_TYPE_ID));
				String value = importContext.get(DV_KEY + ProviderDescriptor.DataValue.Type.FUEL + innerValue);
				cv.put(ProviderDescriptor.Log.Cols.FUEL_TYPE_ID, value);
			}

			{
				String innerValue = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.FUEL_STATION_ID));
				String value = importContext.get(DV_KEY + ProviderDescriptor.DataValue.Type.STATION + innerValue);
				cv.put(ProviderDescriptor.Log.Cols.FUEL_STATION_ID, value);
			}

			String innerValue = attributes.getValue(attributes.getIndex(ProviderDescriptor.Log.Cols.CAR_ID));
			String value = importContext.get(CAR_KEY + innerValue);
			cv.put(ProviderDescriptor.Log.Cols.CAR_ID, value);
//			Log.e(TAG, UUID.randomUUID().toString());

			boolean newValue = true;
			if (existCar(innerValue)) {
				long id = DBUtils.getValueId(cr,
						ProviderDescriptor.Log.CONTENT_URI,
						ProviderDescriptor.Log.Cols.DATE, date);
				newValue = (id == -1);
			}

			if (newValue) {
				cr.insert(ProviderDescriptor.Log.CONTENT_URI, cv);
			}
		}

		@Override
		public void flush() {

		}
	}

	private boolean existCar(String innerKey) {
		return importContext.get(CAR_KEY + innerKey + EXIST) != null;
	}

	private class NotifyImporter implements Importer {

		@Override
		public void importData(Attributes attributes) {
			ContentValues cv = new ContentValues();

			cv.put(ProviderDescriptor.Notify.Cols.NAME,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.NAME)));

			String date =
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.CREATE_DATE));
			cv.put(ProviderDescriptor.Notify.Cols.CREATE_DATE, date);

			cv.put(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE)));

			cv.put(ProviderDescriptor.Notify.Cols.TYPE,
					attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.TYPE)));

			String innerCarId = attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.CAR_ID));

			String carID = importContext.get(CAR_KEY + innerCarId);

			cv.put(ProviderDescriptor.Notify.Cols.CAR_ID,
					carID);

			boolean newValue = true;
			if (existCar(innerCarId)) {
				long id = DBUtils.getValueId(cr,
						ProviderDescriptor.Notify.CONTENT_URI,
						ProviderDescriptor.Notify.Cols.CREATE_DATE, date);
				newValue = (id == -1);
			}

			if (newValue) {
				Uri uri = cr.insert(ProviderDescriptor.Notify.CONTENT_URI, cv);
			}
		}

		@Override
		public void flush() {

		}
	}

	private class FuelRateImporter implements Importer {
		@Override
		public void importData(Attributes attributes) {
			ContentValues cv = new ContentValues();

			String rate = attributes.getValue(attributes.getIndex(ProviderDescriptor.FuelRate.Cols.RATE));
			cv.put(ProviderDescriptor.FuelRate.Cols.RATE, rate);

			String minRate = attributes.getValue(attributes.getIndex(ProviderDescriptor.FuelRate.Cols.MIN_RATE));
			cv.put(ProviderDescriptor.FuelRate.Cols.MIN_RATE, minRate);

			String maxRate = attributes.getValue(attributes.getIndex(ProviderDescriptor.FuelRate.Cols.MAX_RATE));
			cv.put(ProviderDescriptor.FuelRate.Cols.MAX_RATE, maxRate);

			String stationId = attributes.getValue(attributes.getIndex(ProviderDescriptor.FuelRate.Cols.STATION_ID));
			stationId = importContext.get(DV_KEY + ProviderDescriptor.DataValue.Type.STATION + stationId);
			cv.put(ProviderDescriptor.FuelRate.Cols.STATION_ID, stationId);

			String typeId = attributes.getValue(attributes.getIndex(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID));
			typeId = importContext.get(DV_KEY + ProviderDescriptor.DataValue.Type.FUEL + typeId);
			cv.put(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID, typeId);

			String innerCarId = attributes.getValue(attributes.getIndex(ProviderDescriptor.Notify.Cols.CAR_ID));
			String carID = importContext.get(CAR_KEY + innerCarId);

			cv.put(ProviderDescriptor.Notify.Cols.CAR_ID,
					carID);

			boolean newValue = true;
			if (existCar(innerCarId)) {
				long id = DBUtils.getFuelRateId(cr, typeId, stationId);
				newValue = (id == -1);
			}

			if (newValue) {
				Uri uri = cr.insert(ProviderDescriptor.FuelRate.CONTENT_URI, cv);
			}
		}

		@Override
		public void flush() {

		}
	}

	@Override
	public boolean exportData(String name) {
		boolean result = false;

//		FileUtils.cleanDir(); //TODO For Testing

		XmlSerializer serializer = Xml.newSerializer();

		File backUpDir = FileUtils.getBackupDirectory();
		File file = new File(backUpDir, name + "." + EXTENSION);

		BufferedWriter bw = null;

		try {
			boolean created = file.createNewFile();

			if (created) {

				OutputStream os = new FileOutputStream(file);
				bw = new BufferedWriter(new OutputStreamWriter(os));
				serializer.setOutput(bw);
			}

			serializer.startDocument(DEFAULT_ENCODING, false);
			serializer.startTag(DEFAULT_NAMESPACE, CAR_LOGBOOK_TAG);
			serializer.attribute(DEFAULT_NAMESPACE, EXPORT_VERSION_NAME, EXPORT_VERSION_VALUE);

			exportDataValues(serializer);
			exportCars(serializer);
			exportNotify(serializer);
			exportLog(serializer);
			exportFuelRate(serializer);
			exportSettings(serializer);

			serializer.endTag(DEFAULT_NAMESPACE, CAR_LOGBOOK_TAG);
			serializer.endDocument();

			result = true;
		} catch (java.io.IOException e) {
			Log.e(TAG, "Error" + file.getName());
		} catch (NullPointerException e) {
			Log.e(TAG, "Error" + file.getName());
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {/*nothing*/}
			}
		}

		return result;
	}


	private void exportDataValues(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, DATA_VALUE_TAG);

		//

		//LOOP

		Cursor c = cr.query(ProviderDescriptor.DataValue.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}

		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			long id = c.getLong(c.getColumnIndex(ProviderDescriptor.DataValue.Cols._ID));
			serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.DataValue.Cols._ID,
					String.valueOf(id));

			String name = c.getString(c.getColumnIndex(ProviderDescriptor.DataValue.Cols.NAME));
			serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.DataValue.Cols.NAME,
					name);

			long type = c.getLong(c.getColumnIndex(ProviderDescriptor.DataValue.Cols.TYPE));
			serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.DataValue.Cols.TYPE,
					String.valueOf(type));

			long system = c.getLong(c.getColumnIndex(ProviderDescriptor.DataValue.Cols.SYSTEM));
			serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.DataValue.Cols.SYSTEM,
					String.valueOf(system));

			long flag = c.getLong(c.getColumnIndex(ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG));
			serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.DataValue.Cols.DEFAULT_FLAG,
					String.valueOf(flag));

			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();


		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, DATA_VALUE_TAG);
	}

	private void exportLog(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, LOG_TAG);

		Cursor c = cr.query(ProviderDescriptor.Log.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}

		//LOOP


		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			{
				long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols._ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols._ID,
						String.valueOf(id));
			}

			{
				long date = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.DATE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.DATE,
						String.valueOf(date));
			}

			{
				long odometer = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.ODOMETER));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.ODOMETER,
						String.valueOf(odometer));
			}

			{
				double price = c.getDouble(c.getColumnIndex(ProviderDescriptor.Log.Cols.PRICE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.PRICE,
						String.valueOf(price));
			}

			{
				String comment = c.getString(c.getColumnIndex(ProviderDescriptor.Log.Cols.CMMMENT));
				if (comment != null) {
					serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.CMMMENT,
							comment);
				}
			}

			{
				long carId = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.CAR_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.CAR_ID,
						String.valueOf(carId));
			}

			{
				long fuelId = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.FUEL_TYPE_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.FUEL_TYPE_ID,
						String.valueOf(fuelId));
			}

			{
				long stationId = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.FUEL_STATION_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.FUEL_STATION_ID,
						String.valueOf(stationId));
			}

			{
				double fuelVolume = c.getDouble(c.getColumnIndex(ProviderDescriptor.Log.Cols.FUEL_VOLUME));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.FUEL_VOLUME,
						String.valueOf(fuelVolume));
			}

			{
				long typeId = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.TYPE_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.TYPE_ID,
						String.valueOf(typeId));
			}


			{
				String name = c.getString(c.getColumnIndex(ProviderDescriptor.Log.Cols.NAME));

				if (name != null) {
					serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.NAME,
							name);
				}
			}

			{
				long typeLog = c.getLong(c.getColumnIndex(ProviderDescriptor.Log.Cols.TYPE_LOG));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Log.Cols.TYPE_LOG,
						String.valueOf(typeLog));
			}


			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();


		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, LOG_TAG);
	}

	private void exportNotify(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, NOTIFY_TAG);

		Cursor c = cr.query(ProviderDescriptor.Notify.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}


		//LOOP
		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			{
				long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols._ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols._ID,
						String.valueOf(id));
			}

			{
				String name = c.getString(c.getColumnIndex(ProviderDescriptor.Notify.Cols.NAME));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols.NAME,
						name);
			}

			{
				long type = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols.TYPE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols.TYPE,
						String.valueOf(type));
			}

			{
				long value = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols.TRIGGER_VALUE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols.TRIGGER_VALUE,
						String.valueOf(value));

			}

			{
				long value = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols.CREATE_DATE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols.CREATE_DATE,
						String.valueOf(value));

			}

			{
				long carId = c.getLong(c.getColumnIndex(ProviderDescriptor.Notify.Cols.CAR_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Notify.Cols.CAR_ID,
						String.valueOf(carId));
			}

			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();


		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, NOTIFY_TAG);
	}

	private void exportCars(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, CAR_TAG);

		//LOOP

		Cursor c = cr.query(ProviderDescriptor.Car.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}

		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			{
				long id = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols._ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols._ID,
						String.valueOf(id));
			}

			{
				String name = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.NAME));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.NAME,
						name);
			}

			{
				String uuiud = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UUID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.UUID,
						uuiud);
			}

			{
                //TODO FIX IT
				String currency = c.getString(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CURRENCY));
				if (currency != null) {
					serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.UNIT_CURRENCY,
							currency);
				}
			}

			{
				long fuelV = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_FUEL));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.UNIT_FUEL,
						String.valueOf(fuelV));
			}

			{
				long distV = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_DISTANCE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.UNIT_DISTANCE,
						String.valueOf(distV));
			}


			{
				long consumV = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.UNIT_CONSUMPTION,
						String.valueOf(consumV));
			}


			{
				long activeFlag = c.getLong(c.getColumnIndex(ProviderDescriptor.Car.Cols.ACTIVE_FLAG));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Car.Cols.ACTIVE_FLAG,
						String.valueOf(activeFlag));
			}
			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();

		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, CAR_TAG);
	}

	private void exportSettings(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, SET_TAG);

		//LOOP

		Cursor c = cr.query(ProviderDescriptor.Sett.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}

		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			{
				String key = c.getString(c.getColumnIndex(ProviderDescriptor.Sett.Cols.KEY));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Sett.Cols.KEY,
						key);
			}

			{
				String value = c.getString(c.getColumnIndex(ProviderDescriptor.Sett.Cols.VALUE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.Sett.Cols.VALUE,
						value);
			}

			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();

		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, SET_TAG);
	}

	private void exportFuelRate(XmlSerializer serializer) throws IOException {
		serializer.startTag(DEFAULT_NAMESPACE, RATE_TAG);

		Cursor c = cr.query(ProviderDescriptor.FuelRate.CONTENT_URI, null, null, null, null);
		if (c == null) {
			return;
		}


		//LOOP
		while (c.moveToNext()) {
			serializer.startTag(DEFAULT_NAMESPACE, ITEM_TAG);

			{
				long id = c.getLong(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols._ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols._ID,
						String.valueOf(id));
			}


			{
				double rate = c.getDouble(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.RATE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.RATE,
						String.valueOf(rate));
			}

			{
				double rateMin = c.getDouble(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.MIN_RATE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.MIN_RATE,
						String.valueOf(rateMin));
			}

			{
				double rateMax = c.getDouble(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.MAX_RATE));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.MAX_RATE,
						String.valueOf(rateMax));
			}

			{
				long fuelId = c.getLong(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.FUEL_TYPE_ID,
						String.valueOf(fuelId));
			}

			{
				long stationId = c.getLong(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.STATION_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.STATION_ID,
						String.valueOf(stationId));
			}


			{
				long carId = c.getLong(c.getColumnIndex(ProviderDescriptor.FuelRate.Cols.CAR_ID));
				serializer.attribute(DEFAULT_NAMESPACE, ProviderDescriptor.FuelRate.Cols.CAR_ID,
						String.valueOf(carId));
			}

			serializer.endTag(DEFAULT_NAMESPACE, ITEM_TAG);
		}

		c.close();


		//END LOOP

		serializer.endTag(DEFAULT_NAMESPACE, RATE_TAG);
	}


}

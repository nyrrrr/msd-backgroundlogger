package com.nyrrrr.msd.collector;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves data on phone
 * Currently saves the data in JSON. I might reconsider this format.
 *
 * Created by nyrrrr on 27.09.2016.
 */

public class StorageManager {

    private static final String STRING_JSON_FILE_NAME = "victim-data.json";
    private static final String STRING_CSV_FILE_NAME = "victim-data.csv";

    private static StorageManager oInstance = null;
    public JSONArray oData;

    private SensorData oSensorData;
    private List<SensorData> oSensorDataList;

    protected StorageManager() {
        oSensorDataList = new ArrayList<SensorData>();
    }

    public static StorageManager getInstance() {
        if (oInstance == null) {
            oInstance = new StorageManager();
        }
        return oInstance;
    }

    /**
     * puts captured data into a list for later storage
     *
     * @param pEvent       sensor data event
     * @param pOrientation orientation during capture
     * @return SensorData object
     */
    public SensorData addSensorDataLogEntry(SensorEvent pEvent, int pOrientation) {
        oSensorData = new SensorData(pEvent, pOrientation);
        if (oSensorDataList.add(oSensorData)) {
            return oSensorData;
        }
        return null;
    }

    /**
     * Converts List of SensorData Object to JSONArray object.
     * Can also remove unnecessary entries form the list.
     *
     * @return JSONArray
     */
    private JSONArray convertSensorDataLogToJSON() {
        oData = new JSONArray();

        for (SensorData dataObject : oSensorDataList) {
                oData.put(dataObject.toJSONObject());
        }
        return oData;
    }

    /**
     * Convert List of SensorData to CSV String.
     * Can also remove unnecessary entries from the original list
     *
     * @return CSV String
     */
    private String convertSensorDataLogToCSV() {
        String csvString = oSensorDataList.get(0).getCsvHeaders();
        for (SensorData dataObject : oSensorDataList) {
                csvString += dataObject.toCSVString();
        }
        Log.d("CSV", csvString);
        return csvString;
    }

    /**
     * Create and save data file (TIMESTAMP-msd-data.json).
     * The list of data will first be converted to JSON.
     *
     * @param pAppContext
     * @return boolean  - true for successful save
     * @throws IOException
     * @throws JSONException
     */
    public void storeData(Context pAppContext) throws JSONException, IOException {
        oData = convertSensorDataLogToJSON();

        String fileName = oData.getJSONObject(0).get("Timestamp") + "-";
        FileWriter file = new FileWriter(pAppContext.getFilesDir().getPath() + "/" + fileName + STRING_JSON_FILE_NAME);
        file.write(oData.toString(4));
        file.flush();
        file.close();

        // write csv
        file = new FileWriter(pAppContext.getFilesDir().getPath() + "/" + fileName + STRING_CSV_FILE_NAME);
        file.write(convertSensorDataLogToCSV());
        file.flush();
        file.close();

        oSensorDataList = new ArrayList<SensorData>(); // reset list
    }

    // debug-only
    public void storeData(Context pAppContext, boolean pDebug) throws IOException, JSONException {
        this.storeData(pAppContext);
        Log.d("JSON Write debug", oData.toString(4));
    }

    /**
     * return size of list of logged data
     *
     * @return int
     */
    public int getSensorDataLogLength() {
        return oSensorDataList.size();
    }
}

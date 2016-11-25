package com.nyrrrr.msd.collector;

import android.hardware.SensorEvent;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sensor Data object used to be saved later on
 * Stores Accelerometer data and orientation
 * Created by nyrrrr on 24.09.2016.
 */

public class SensorData {

    private long dTimestamp = -1;
    private String sSensorType = "";
    private float[] fValues = new float[3];
    private int iOrientation = -1;

    /**
     * @param pEvent
     * @param pOrientation
     */
    public SensorData(SensorEvent pEvent, int pOrientation) {
        dTimestamp = pEvent.timestamp;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            sSensorType = pEvent.sensor.getStringType();
        }
        fValues[0] = pEvent.values[0];
        fValues[1] = pEvent.values[1];
        fValues[2] = pEvent.values[2];
        iOrientation = pOrientation;
    }

    /**
     * Convert List to JSONObject
     *
     * @return JSONObject
     * @throws JSONException
     */
    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        JSONObject valuesObject = new JSONObject();
        try {
            jsonObject.put("Timestamp", dTimestamp);
            jsonObject.put("Sensor", sSensorType);
            jsonObject.put("Orientation", iOrientation);

            valuesObject.put("x", fValues[0]);
            valuesObject.put("y", fValues[1]);
            valuesObject.put("z", fValues[2]);
            jsonObject.put("Values", valuesObject);
        } catch (JSONException e) {
            Log.e(e.getCause().toString(), "Error while converting list to JSON: " + e.getMessage());
        }
        return jsonObject;
    }

    public String toCSVString() {
        return dTimestamp + "," + fValues[0] + "," + fValues[1] + "," + fValues[2] + "," + iOrientation + "\n";
    }

    public String getCsvHeaders() {
        return "Timestamp,x,y,z,Key,Orientation\n";
    }

    public void print() {
        Log.d("Timestamp", dTimestamp + "");
        //Log.d("Sensor type", sSensorType);
        Log.d("Values", "x: " + fValues[0] + ", y: " + fValues[1] + ", z: " + fValues[2] + "");
        Log.d("Orientation", iOrientation + "");
        Log.d("-----------------------", "-------------------------------------------------------");
    }
}

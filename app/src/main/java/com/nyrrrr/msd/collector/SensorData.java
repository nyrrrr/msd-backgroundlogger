package com.nyrrrr.msd.collector;

import java.text.SimpleDateFormat;

/**
 * Sensor Data object used to be saved later on
 * Stores Accelerometer data and orientation
 * Created by nyrrrr on 24.09.2016.
 */

public class SensorData {

    public float x;
    public float y;
    public float z;
    public float alpha;
    public float beta;
    public float gamma;
    public String timestampString;
    public long timestamp;

    public SensorData(long pTimestamp) {
        timestamp = pTimestamp;
        SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:SSSS");
        timestampString = date.format(new java.sql.Timestamp(System.currentTimeMillis()));
    }

    public String toCSVString() {
        return timestampString + ","
                + x + "," + y + "," + z + ","
                + alpha + "," + beta + "," + gamma + "\n";
    }

    public String getCsvHeaders() {
        return "Timestamp,x,y,z,alpha,beta,gamma\n";
    }

}

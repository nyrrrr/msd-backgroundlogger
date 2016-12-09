package com.nyrrrr.msd.collector;

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
    long timestamp;


    public SensorData () {
        this(System.currentTimeMillis());
    }

    public SensorData(long pTimeStamp) {
        timestamp = pTimeStamp;
    }

    String toCSVString() {
        return timestamp + ","
                + x + "," + y + "," + z + ","
                + alpha + "," + beta + "," + gamma +  "\n";
    }

    String getCsvHeaders() {
        return "Timestamp,x,y,z,alpha,beta,gamma";
    }
}

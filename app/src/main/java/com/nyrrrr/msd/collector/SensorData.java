package com.nyrrrr.msd.collector;

/**
 * Sensor Data object used to be saved later on
 * Stores Accelerometer data and orientation
 * Created by nyrrrr on 24.09.2016.
 */

public class SensorData {

    public float x; // x acc
    public float y; // y acc
    public float z; // z acc
    public float a; // x gyro
    public float b; // y gyro
    public float c; // z gyro
    public float alpha; // alpha
    public float beta; // beta
    public float gamma; // gamma
    private long timestamp;


    public SensorData() {
        this(System.currentTimeMillis());
    }

    private SensorData(long pTimeStamp) {
        timestamp = pTimeStamp;
    }

    String toCSVString() {
        return timestamp + ","
                + x + "," + y + "," + z + ","
                + a + "," + b + "," + c + ","
                + alpha + "," + beta + "," + gamma + "\n";
    }

    String getCsvHeaders() {
        return "Timestamp,x,y,z,a,b,c,alpha,beta,gamma";
    }
}

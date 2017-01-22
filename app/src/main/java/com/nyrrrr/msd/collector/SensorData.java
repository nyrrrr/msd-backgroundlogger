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
    public float x_ra; // x_ra
    public float y_ra; // y_ra
    public float z_ra; // z_ra
    public float alpha;
    public float beta;
    public float gamma;
    public int id;
    private long timestamp;

    public SensorData(long pTimeStamp) {
        timestamp = pTimeStamp;
    }

    String toCSVString() {
        return id + "," + timestamp + ","
                + x + "," + y + "," + z + ","
                + a + "," + b + "," + c + ","
                + alpha + "," + beta + "," + gamma + ","
                + x_ra + "," + y_ra + "," + z_ra + "\n";
    }

    String getCsvHeaders() {
        return "id,Timestamp,x,y,z,a,b,c,alpha,beta,gamma,x_ra,y_ra,z_ra";
    }
}

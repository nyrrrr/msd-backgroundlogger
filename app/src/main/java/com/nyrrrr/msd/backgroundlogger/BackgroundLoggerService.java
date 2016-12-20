package com.nyrrrr.msd.backgroundlogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.nyrrrr.msd.collector.SensorData;
import com.nyrrrr.msd.collector.SensorReader;
import com.nyrrrr.msd.collector.StorageManager;

/**
 * Service that is supposed to log Accelerometer data in the background
 * Created by nyrrrr on 24.11.2016.
 */

public class BackgroundLoggerService extends Service implements SensorEventListener {

    private final MSDBinder oBinder = new MSDBinder();
    private HandlerThread oThread;
    private BackgroundServiceHandler oServiceHandler;

    private SensorManager oSensorManager;
    private Sensor oAcceleroMeter;
    private Sensor oGyroscope;

    private StorageManager oStorageManager;
    private SensorData oData;
    private Sensor oOrientation;


    @Override
    public void onCreate() {
        MainActivity.oBacklogger = this;
        if (StorageManager.getInstance().getSensorDataLogLength() <= 0) {
            oStorageManager = StorageManager.getInstance();
            // start oThread
            oThread = new HandlerThread("SensorData", Process.THREAD_PRIORITY_BACKGROUND);
            oThread.start();

            Looper oServiceLooper = oThread.getLooper();
            oServiceHandler = new BackgroundServiceHandler(this, oServiceLooper);

            initSensors();
            registerListeners();
        }
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        if (oThread != null) {
            Message message = oServiceHandler.obtainMessage();
            oServiceHandler.sendMessage(message);
        }
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent pSensorEvent) {
        if (oData == null) oData = new SensorData();
        if (pSensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            oData.x = pSensorEvent.values[0];
            oData.y = pSensorEvent.values[1];
            oData.z = pSensorEvent.values[2];
        } else if (pSensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            oData.a = pSensorEvent.values[0];
            oData.b = pSensorEvent.values[1];
            oData.c = pSensorEvent.values[2];
        } else if (pSensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            oData.alpha = pSensorEvent.values[0];
            oData.beta = pSensorEvent.values[1];
            oData.gamma = pSensorEvent.values[2];
        }

        if (oData.x != 0 && oData.y != 0 && oData.z != 0  && oData.a != 0 && oData.b != 0 && oData.c != 0 && oData.alpha != 0 && oData.beta != 0 && oData.gamma != 0) {
            oStorageManager.addSensorDataLogEntry(oData);
            oData = null;
        }
        if (oStorageManager.getSensorDataLogLength() > 9999) {
            store();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {
    }

    @Override
    public void onDestroy() {
        store();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        return oBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void store() {
        oStorageManager.storeData(this);
    }

    private void initSensors() {
        oSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorReader oSensorReader = new SensorReader(oSensorManager);
        oAcceleroMeter = oSensorReader.getSingleSensorOfType(Sensor.TYPE_LINEAR_ACCELERATION);
        oGyroscope = oSensorReader.getSingleSensorOfType(Sensor.TYPE_GYROSCOPE);
        oOrientation = oSensorReader.getSingleSensorOfType(Sensor.TYPE_ORIENTATION);
    }

    /**
     * Register Accelerometer and Gyroscope Sensors
     */
    public void registerListeners() {
        // register accelerometer
        if (oSensorManager != null) {
            oSensorManager.registerListener(this, oAcceleroMeter, SensorManager.SENSOR_DELAY_FASTEST);
            oSensorManager.registerListener(this, oGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
            oSensorManager.registerListener(this, oOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    /**
     * Unregister Accelerometer and Gyroscope Sensors
     */
    public void unregisterListeners() {
        if (oSensorManager != null) {
            oSensorManager.unregisterListener(this, oAcceleroMeter);
            oSensorManager.unregisterListener(this, oGyroscope);
            oSensorManager.unregisterListener(this, oOrientation);
        }
    }

    private class MSDBinder extends Binder {
    }

    /**
     * used for uploading files
     */
    public class BackgroundServiceHandler extends Handler {
        Context context;

        BackgroundServiceHandler(Context pContext, Looper pLooper) {
            super(pLooper);
            context = pContext;
        }

        @Override
        public void handleMessage(Message pMsg) {

        }
    }
}
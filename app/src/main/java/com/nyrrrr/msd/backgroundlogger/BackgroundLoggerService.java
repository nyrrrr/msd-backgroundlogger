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
import android.util.Log;
import android.view.OrientationEventListener;
import android.widget.Toast;

import com.nyrrrr.msd.collector.SensorReader;
import com.nyrrrr.msd.collector.StorageManager;

import org.json.JSONException;

import java.io.IOException;

/**
 * Service that is supposed to log Accelerometer data in the background
 * Created by nyrrrr on 24.11.2016.
 *
 * @// TODO: 24.11.2016 remove toasts
 */

public class BackgroundLoggerService extends Service implements SensorEventListener {
    private final MSDBinder oBinder = new MSDBinder();
    HandlerThread oThread;
    private Looper oServiceLooper;
    private BackgroundServiceHandler oServiceHandler;
    private SensorReader oSensorReader;
    private SensorManager oSensorManager;
    private Sensor oAcceleroMeter;
    private OrientationEventListener oOrientationEventListener;
    private int iOrientationLogVar = OrientationEventListener.ORIENTATION_UNKNOWN;

    @Override
    public void onCreate() {
        if (StorageManager.getInstance().getSensorDataLogLength() <= 0) {
            // start oThread
            oThread = new HandlerThread("SensorData", Process.THREAD_PRIORITY_BACKGROUND);
            oThread.start();

            oServiceLooper = oThread.getLooper();
            oServiceHandler = new BackgroundServiceHandler(this, oServiceLooper);

            oSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            oSensorReader = new SensorReader(oSensorManager);
            oAcceleroMeter = oSensorReader.getSingleSensorOfType(Sensor.TYPE_ACCELEROMETER);
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
        StorageManager.getInstance().addSensorDataLogEntry(pSensorEvent, iOrientationLogVar);
        if (StorageManager.getInstance().getSensorDataLogLength() > 9999) {
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
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT);
        unregisterListeners();
        return oBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        registerListeners();
        return super.onUnbind(intent);
    }

    private void store() {
        try {
            StorageManager.getInstance().storeData(this);
        } catch (IOException e) {
            Log.e("IO ERROR", e.getMessage());
        } catch (JSONException e) {
            Log.e("JSON ERROR", e.getMessage());
        }
    }

    /**
     *
     */
    private void registerListeners() {
        // register accelerometer
        oSensorManager.registerListener(this, oAcceleroMeter, SensorManager.SENSOR_DELAY_FASTEST);

        // TODO replace with gyro
        oOrientationEventListener = new OrientationEventListener(
                getApplicationContext(), SensorManager.SENSOR_DELAY_FASTEST) {
            @Override
            public void onOrientationChanged(int pOrientation) {
                iOrientationLogVar = pOrientation;
            }
        };
        if (oOrientationEventListener.canDetectOrientation()) {
            oOrientationEventListener.enable();
        }
    }

    private void unregisterListeners() {
        oSensorManager.unregisterListener(this, oAcceleroMeter);
    }

    private class MSDBinder extends Binder {
    }

    /**
     * used for uploading files
     */
    public class BackgroundServiceHandler extends Handler {
        Context context;

        public BackgroundServiceHandler(Context pContext, Looper pLooper) {
            super(pLooper);
            context = pContext;
        }

        @Override
        public void handleMessage(Message pMsg) {

        }
    }
}
package com.nyrrrr.msd.backgroundlogger;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Service that is supposed to log Accelerometer data in the background
 * Created by nyrrrr on 24.11.2016.
 *
 * @// TODO: 24.11.2016 remove toasts
 */

public class BackgroundService extends Service {
    private Looper oServiceLooper;
    private BackgroundServiceHandler oServiceHandler;

    private SensorReader oSensorReader;
    private SensorManager oSensorManager;
    private Sensor oAcceleroMeter;
    private OrientationEventListener oOrientationEventListener;
    private int iOrientationLogVar = OrientationEventListener.ORIENTATION_UNKNOWN;

    @Override
    public void onCreate() {
        // start thread
        HandlerThread thread = new HandlerThread("SensorData", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        oServiceLooper = thread.getLooper();
        oServiceHandler = new BackgroundServiceHandler(oServiceLooper);

        oSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        oSensorReader = new SensorReader(oSensorManager);
        oAcceleroMeter = oSensorReader.getSingleSensorOfType(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        Toast.makeText(this, "Service started", LENGTH_SHORT).show();

        Message message = oServiceHandler.obtainMessage();
        message.arg1 = pStartId;
        message.arg2 = pFlags;
        oServiceHandler.sendMessage(message);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "DESTROYED", LENGTH_SHORT).show();
        try {
            StorageManager.getInstance().storeData(getBaseContext(), true);
        } catch (IOException e) {
            Log.e("IO ERROR", e.getMessage());
        } catch (JSONException e) {
            Log.e("JSON ERROR", e.getMessage());
        }
        super.onDestroy(); // TODO backup data?
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        Toast.makeText(this, "Memory low", LENGTH_SHORT).show();
        super.onLowMemory(); // TODO do not kill
    }

    /**
     * This class is needed to receive messages from the thread and keep the logging process alive.
     */
    public class BackgroundServiceHandler extends Handler implements SensorEventListener {

        public BackgroundServiceHandler(Looper pLooper) {
            super(pLooper);
        }

        @Override
        public void handleMessage(Message pMsg) {
            registerListeners();
        }

        @Override
        public void onSensorChanged(SensorEvent pSensorEvent) {
            StorageManager.getInstance().addSensorDataLogEntry(pSensorEvent, iOrientationLogVar);
        }

        @Override
        public void onAccuracyChanged(Sensor pSensor, int pAccuracy) {}

        /**
         *
         */
        private void registerListeners() {
            // register accelerometer
            oSensorManager.registerListener(this, oAcceleroMeter, SensorManager.SENSOR_DELAY_FASTEST);

            //register orientation listener
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
    }
}

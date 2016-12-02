package com.nyrrrr.msd.backgroundlogger;

import android.app.Service;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Service that is supposed to log Accelerometer data in the background
 * Created by nyrrrr on 24.11.2016.
 *
 * @// TODO: 24.11.2016 remove toasts
 */

public class BackgroundService extends Service implements SensorEventListener {
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
            //registerListeners();
        }
    }

    @Override
    public int onStartCommand(Intent pIntent, int pFlags, int pStartId) {
        Toast.makeText(this, "Service started", LENGTH_SHORT).show();
        if (oThread != null) {
            Message message = oServiceHandler.obtainMessage();
            message.arg1 = pStartId;
            message.arg2 = pFlags;
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
//        store();
//        transferData(this.fileList());
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent pIntent) {
        return null;
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
     * Transfer files to server
     *
     * @param pFileList
     */
    public void transferData(String[] pFileList) {

        String serverName = "192.168.2.103";
        int port = 4444;
        Log.d("COMMUNICATION", "Start sending...");
        try {
            for (String fileName : pFileList) {
                if (fileName.equals("instant-run") || fileName.equals("PaxHeader")) continue;
                // socket and communication
                Socket socket = new Socket(serverName, port);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // read files
                File file = new File(getApplicationContext().getFilesDir().getPath() + "/" + fileName);
                char[] charArray = new char[(int) file.length()];
                FileInputStream inputStream = new FileInputStream(file);
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
                fileReader.read(charArray, 0, charArray.length);

                // transfer protocol
                writer.println("FILE");
                writer.flush();
                String message = reader.readLine();
                if (message.equals("File name?")) {
                    Log.d("Server response", message);
                    writer.println(fileName);
                    writer.flush();
                    message = reader.readLine();
                    if (message.equals("File size?")) {
                        Log.d("Server response", message);
                        if(file.length() == 0) {
                            writer.println("Abort");
                            writer.flush();
                            continue;
                        }
                        writer.println(file.length());
                        writer.flush();
                        message = reader.readLine();
                        if (message.equals("Waiting for file...")) {
                            Log.d("Server response", message);
                            Log.d("Sending", fileName);
                            writer.write(charArray, 0, charArray.length);
                            writer.flush();
                            Log.d("Server response", message = reader.readLine());
                        } else {
                            Log.e("File size Error", message);
                        }
                    } else {
                        Log.e("File name Error", message);
                    }
                } else {
                    Log.e("Server Error", message);
                }
                socket.close();
            }
        } catch (UnknownHostException e) {
            Log.e("HOST ERROR", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IO ERROR", e.getMessage());
            e.printStackTrace();
        }
    }

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

    /**
     * This class is needed to receive messages from the oThread and keep the logging process alive.
     */
    public class BackgroundServiceHandler extends Handler {


        Context context;

        public BackgroundServiceHandler(Context pContext, Looper pLooper) {
            super(pLooper);
            context = pContext;
        }

        @Override
        public void handleMessage(Message pMsg) {
            transferData(context.fileList());
        }
    }
}
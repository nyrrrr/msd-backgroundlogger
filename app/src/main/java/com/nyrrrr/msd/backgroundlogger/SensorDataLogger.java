package com.nyrrrr.msd.backgroundlogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by nyrrrr on 24.11.2016.
 */

public class SensorDataLogger extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "SERVICE STARTED", Toast.LENGTH_SHORT).show();
        return Service.START_STICKY; // restart when killed
    }

    @Override
    public void onLowMemory() {
        Toast.makeText(getApplicationContext(), "Memory low", Toast.LENGTH_SHORT).show();
        super.onLowMemory(); // TODO do not kill
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "DESTROYED", Toast.LENGTH_SHORT).show();
        super.onDestroy(); // TODO backup data?
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

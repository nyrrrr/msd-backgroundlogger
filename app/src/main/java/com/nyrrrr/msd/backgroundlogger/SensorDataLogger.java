package com.nyrrrr.msd.backgroundlogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by nyrrrr on 24.11.2016.
 */

public class SensorDataLogger extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}

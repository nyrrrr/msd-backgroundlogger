package com.nyrrrr.msd.backgroundlogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nyrrrr on 28.11.2016.
 */

public class Bootloader extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, BackgroundLoggerService.class);
        context.startService(startServiceIntent);
    }
}

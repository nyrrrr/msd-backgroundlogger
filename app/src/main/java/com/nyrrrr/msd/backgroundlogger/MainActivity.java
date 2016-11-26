package com.nyrrrr.msd.backgroundlogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Intent oIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oIntent = new Intent(this, BackgroundService.class);
        startService(oIntent);
    }

    @Override
    protected void onDestroy() {
// TODO       stopService(oIntent);
        super.onDestroy();
    }

}

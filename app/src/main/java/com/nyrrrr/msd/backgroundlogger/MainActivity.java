package com.nyrrrr.msd.backgroundlogger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.nyrrrr.msd.collector.StorageManager;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Intent oIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oIntent = new Intent(this, BackgroundService.class);
        if (StorageManager.getInstance().getSensorDataLogLength() > 0) {
            try {
                StorageManager.getInstance().storeData(this);
            } catch (IOException e) {
                Log.e("IO ERROR", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON ERROR", e.getMessage());
            }
        } else startService(oIntent);
    }

    @Override
    protected void onStop() {
        oIntent = new Intent(this, BackgroundService.class);
        if (StorageManager.getInstance().getSensorDataLogLength() > 0) {
            try {
                StorageManager.getInstance().storeData(this);
            } catch (IOException e) {
                Log.e("IO ERROR", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON ERROR", e.getMessage());
            }
        } else startService(oIntent);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

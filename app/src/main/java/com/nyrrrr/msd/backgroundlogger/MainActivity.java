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
        startServerOrBackUpData();
    }

    @Override
    protected void onStop() {
        startServerOrBackUpData();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Start Service if it's not already started.
     * Otherwise create a backup of the current dataset.
     */
    private void startServerOrBackUpData() {
        if (StorageManager.getInstance().getSensorDataLogLength() > 0) {
            try {
                StorageManager.getInstance().storeData(this);
            } catch (IOException e) {
                Log.e("IO ERROR", e.getMessage());
            } catch (JSONException e) {
                Log.e("JSON ERROR", e.getMessage());
            }
        } else {
            oIntent = new Intent(this, BackgroundService.class);
            startService(oIntent);
        }
    }

}

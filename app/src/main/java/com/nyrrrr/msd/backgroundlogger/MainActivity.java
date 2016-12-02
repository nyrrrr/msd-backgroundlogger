package com.nyrrrr.msd.backgroundlogger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nyrrrr.msd.collector.StorageManager;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Intent oIntent;
    private Button oButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startServerOrBackUpData();

        initButton();
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

    protected ServiceConnection oConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("LOG", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("LOG", "DISconnected");
        }
    };

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

    private void initButton() {
        oButton = (Button) findViewById(R.id.button);
        oButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO bind
                oIntent = new Intent(getApplicationContext(), BackgroundService.class);
                bindService(oIntent, oConnection, Context.BIND_ABOVE_CLIENT);
            }
        });
    }
}

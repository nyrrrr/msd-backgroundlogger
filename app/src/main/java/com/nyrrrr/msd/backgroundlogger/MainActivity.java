package com.nyrrrr.msd.backgroundlogger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.nyrrrr.msd.collector.StorageManager;

public class MainActivity extends AppCompatActivity {

    static BackgroundLoggerService oBacklogger;
    private Intent oIntent;
    private Button oButton;
    // used for binding service to main app
    protected ServiceConnection oConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_SHORT).show();
            final AsyncTask asyncTask = new BackgroundUploadTask() {
                @Override
                protected void onPostExecute(Object o) {
                    if (o == null)
                        Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), ((Exception) o).getMessage() + "\ntry again later.", Toast.LENGTH_LONG).show();
                    unbindService(oConnection);
                    oButton.setEnabled(true);
                }
            }.execute(getApplicationContext());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("LOG", "Disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        oIntent = new Intent(this, BackgroundLoggerService.class);
        startServerOrBackUpData();

        oBacklogger = new BackgroundLoggerService();

        initButton();
    }

    @Override
    protected void onStart() {
        // only log when app is in background
        if (oBacklogger != null) oBacklogger.unregisterListeners();
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (oBacklogger != null) oBacklogger.unregisterListeners();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (oBacklogger != null) oBacklogger.registerListeners();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (oBacklogger != null) oBacklogger.registerListeners();
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

            StorageManager.getInstance().storeData(this);

        } else {
            startService(oIntent);
        }
    }

    private void initButton() {
        oButton = (Button) findViewById(R.id.button);
        oButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startServerOrBackUpData();
                bindService(oIntent, oConnection, Context.BIND_ABOVE_CLIENT);
                oButton.setEnabled(false);
            }
        });
    }
}

package com.nyrrrr.msd.collector;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves data on phone
 * Currently saves the data in JSON. I might reconsider this format.
 * <p>
 * Created by nyrrrr on 27.09.2016.
 */

public class StorageManager {

    private static final String STRING_CSV_FILE_NAME = "victim-data.csv";

    private static StorageManager oInstance = null;

    private List<SensorData> oSensorDataList;

    private StorageManager() {
        oSensorDataList = new ArrayList<>();
    }

    public static StorageManager getInstance() {
        if (oInstance == null) {
            oInstance = new StorageManager();
        }
        return oInstance;
    }

    public void addSensorDataLogEntry(SensorData oData) {
        oSensorDataList.add(oData);
    }

    /**
     * Create and save data file (TIMESTAMP-msd-data.json).
     * The list of data will first be converted to JSON.
     *
     * @param pAppContext app context
     * @throws IOException
     */
    public void storeData(Context pAppContext) {


        FileWriter file = null;
        BufferedWriter bw = null;
        PrintWriter out = null;

        SimpleDateFormat date = new SimpleDateFormat("yyMMddHH");
        String filenamePrefix = date.format(new java.sql.Timestamp(System.currentTimeMillis()));
        // write csv
        try {
            File f = new File(pAppContext.getFilesDir().getPath() + "/" + filenamePrefix + "-" + STRING_CSV_FILE_NAME);
            boolean isExisting = f.exists();
            file = new FileWriter(f, true);
            bw = new BufferedWriter(file);
            out = new PrintWriter(bw);

            if (isExisting == false) {
                out.println(oSensorDataList.get(0).getCsvHeaders());
            } else {
                System.out.print(1);
            }
            for (SensorData dataObject : oSensorDataList) {
                out.print(dataObject.toCSVString());
            }
            out.close();

            Log.d("Data logged", oSensorDataList.size() + "");

            oSensorDataList = new ArrayList<>(); // reset list
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (bw != null)
                    bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * return size of list of logged data
     *
     * @return int
     */

    public int getSensorDataLogLength() {
        return oSensorDataList.size();
    }

}

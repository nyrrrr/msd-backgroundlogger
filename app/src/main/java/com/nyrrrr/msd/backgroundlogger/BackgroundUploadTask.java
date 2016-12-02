package com.nyrrrr.msd.backgroundlogger;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Background Upload of files
 * Created by nyrrrr on 02.12.2016.
 */

public class BackgroundUploadTask extends AsyncTask {

    Context oContext;

    @Override
    protected Object doInBackground(Object[] objects) {
        oContext = (Context) objects[0];
        transferData(oContext.fileList());
        return null;
    }

    /**
     * Transfer files to server
     *
     * @param pFileList list of files to be transferred
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
                File file = new File(oContext.getFilesDir().getPath() + "/" + fileName);
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
                        if (file.length() == 0) {
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
}

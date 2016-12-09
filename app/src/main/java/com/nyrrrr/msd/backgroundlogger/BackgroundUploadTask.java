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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Background Upload of files
 * Created by nyrrrr on 02.12.2016.
 */

class BackgroundUploadTask extends AsyncTask {

    private static final String SERVER_ADDRESS = "192.168.2.103";
    private static final int TCP_SERVER_PORT = 4444;
    private Context oContext;

    @Override
    protected Object doInBackground(Object[] pObjects) {
        oContext = (Context) pObjects[0];
        return transferData(oContext.fileList());
    }

    /**
     * Transfer files to server
     *
     * @param pFileList list of files to be transferred
     */
    private Exception transferData(String[] pFileList) {

        Exception exception;

        Log.d("COMMUNICATION", "Start sending...");
        if ((exception = hostAvailabilityCheck()) == null) {
            try {
                for (String fileName : pFileList) {
                    if (fileName.equals("instant-run") || fileName.equals("PaxHeader")) continue;
                    // socket and communication

                    Socket socket = new Socket(SERVER_ADDRESS, TCP_SERVER_PORT);
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
                                Log.d("Server response", reader.readLine());
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
                return null;
            } catch (UnknownHostException e) {
                Log.e("HOST ERROR", e.getMessage());
                e.printStackTrace();
                return e;
            } catch (IOException e) {
                Log.e("IO ERROR", e.getMessage());
                e.printStackTrace();
                return e;
            }
        }
        else return exception;
    }

    private Exception hostAvailabilityCheck() {
        try {
            InetSocketAddress isa = new InetSocketAddress(SERVER_ADDRESS, TCP_SERVER_PORT);
            Socket s = new Socket();
            s.connect(isa, 100);
            s.close();
        } catch (IOException e) {
            return e;
        }
        return null;
    }
}

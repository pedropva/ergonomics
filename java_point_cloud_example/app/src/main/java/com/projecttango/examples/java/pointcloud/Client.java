package com.projecttango.examples.java.pointcloud;
/**
 * Created by pedropva on 19/02/2018.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;


import java.io.DataOutputStream;


public class Client extends AsyncTask<Void,Void,String> {

    String dstAddress;
    int dstPort;
    String response = "";
    byte[] data;
    int sizeBuffer;
    int numPoints;
    PointCloudActivity activity;
    String mSent;
    Client(String addr, int port,byte[] message,PointCloudActivity activity) {
        dstAddress = addr;
        dstPort = port;
        data = message;
        this.activity = activity;
        this.numPoints=numPoints;
        this.mSent = mSent;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        Socket socket = null;
        Integer dataLength = 0;
        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            sizeBuffer = data.length;
            dOut.writeByte(1);
            dOut.writeInt(sizeBuffer);
            //OutputStream output = socket.getOutputStream();
            Log.d("ClientActivity", "C: image writing.");
            for (int i = 0; i < data.length/4; i++) {
                dOut.write(data,4*i,4);//
                //dOut.flush(); // Send off the data
            }
            dOut.flush(); // Send off the data
            Log.d("ClientActivity", "C: Sent.");
/*
// Send first message
            dOut.writeByte(1);
            dOut.writeInt(sizeBuffer);
            dOut.writeInt(numPoints);
            for (int i = 0; i < data.length/4; i++) {
                dOut.write(data,4*i,4);//
                //dOut.flush(); // Send off the data
            }
            dOut.flush(); // Send off the data
// Send the second message
            dOut.writeByte(2);
            dOut.writeUTF("foi?");
            dOut.flush(); // Send off the data

// Send the third message
            dOut.writeByte(3);
            dOut.writeUTF("ue");
            dOut.writeUTF("ue^2");
            dOut.flush(); // Send off the data
*/
// Send the exit message
            dOut.writeByte(-1);
            dOut.flush();

            //dOut.close();

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        activity.response.setText(verifyDataSent(response));
        super.onPostExecute(result);
    }

    private String verifyDataSent( String received) {
        activity.DrawPoints(received);
        //testar se eu recebi pontos mesmo e não exceptions
        return "success transmiting the data!";
    }

}
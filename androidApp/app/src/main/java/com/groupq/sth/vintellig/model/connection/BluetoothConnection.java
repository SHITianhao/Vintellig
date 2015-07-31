package com.groupq.sth.vintellig.model.connection;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Tianhao on 6/9/2015.
 */
public class BluetoothConnection extends Thread {
    private BluetoothSocket blueSocket;
    private BluetoothDevice blueDevice;

    public BluetoothConnection(BluetoothDevice device){
        blueDevice = device;
    }
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    public void run(){
        try {
            Log.d("BLUETOOTH","is connected");
            blueSocket = blueDevice.createRfcommSocketToServiceRecord(UUID.fromString("6871790e-be49-453e-a145-49a7a1456326"));
            blueSocket.connect();
            Log.d("BLUETOOTH", "is connected");
            DataOutputStream dos = null;
            dos = new DataOutputStream(blueSocket.getOutputStream());
            dos.writeChar('x'); // for example
            blueSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.groupq.sth.vintellig.model.connection;

import android.os.AsyncTask;

import com.groupq.sth.vintellig.ShareContext;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by sth on 11/06/15.
 */
public class RegisterConnection extends AsyncTask<String, Void, Boolean> {


    String addressIP;
    String msg = null;
    int port;
    Socket socket = null;

    public RegisterConnection(ShareContext shareContext){
        this.addressIP = shareContext.getLoginServerAddress();
        this.port = shareContext.getLoginServerPort();
    }

    public boolean register(String ownerinfo, String email,String pubkey){
        try {
            socket = new Socket(addressIP, port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            HashMap<String, String> loginData = new HashMap<String,String>();
            loginData.put("datatype","register");
            loginData.put("ownerinfo", ownerinfo);
            loginData.put("email",email);
            loginData.put("pubkey", pubkey);
            objectOutputStream.writeObject(loginData);
            msg = dataInputStream.readUTF();
            if("registerSuccess".equals(msg)){
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params.length>=3)
            return register(params[0], params[1],params[2]);
        return false;
    }
}

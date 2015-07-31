package com.groupq.sth.vintellig.model.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.groupq.sth.vintellig.ShareContext;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by sth on 10/06/15.
 */
public class LoginConnection extends AsyncTask<String, Void, Boolean> {
    String serverResponse = null;
    Socket socket = null;
    ShareContext shareContext;

    public LoginConnection(ShareContext shareContext){
        this.shareContext = shareContext;
    }

    public boolean login(String loginname, String password){
        try {
            //start socket
            socket = new Socket(shareContext.getLoginServerAddress(), shareContext.getLoginServerPort());
            //init dis
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            //Hash map for sending
            HashMap<String, String> loginData = new HashMap<String,String>();
            //put in data
            loginData.put("datatype","login");
            loginData.put("loginName", loginname);
            loginData.put("password", password);
            //write data
            objectOutputStream.writeObject(loginData);
            //read server response
            serverResponse = dataInputStream.readUTF();
            if("loginSuccess".equals(serverResponse)){
                //set app context's login state to true
                Log.d("LOGIN", "true");
                socket.close();
                return true;
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("LOGIN","false");
        return false;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params.length>=2)
            return login(params[0],params[1]);
        Log.d("LOGIN","false of params");
        return false;
    }
}

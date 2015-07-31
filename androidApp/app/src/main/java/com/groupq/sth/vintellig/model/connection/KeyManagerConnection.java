package com.groupq.sth.vintellig.model.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.groupq.sth.vintellig.Controller.MainActivity;
import com.groupq.sth.vintellig.ShareContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by sth on 6/16/15.
 */
public class KeyManagerConnection extends AsyncTask<String, Void, File> {

    private ShareContext shareContext;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    ObjectOutputStream oos;

    public KeyManagerConnection(ShareContext shareContext){
        this.shareContext = shareContext;
    }


    @Override
    protected File doInBackground(String... strings) {
        File result = null;
        if ("list".equals(strings[0])){
            try {
                startSocket();
                sendGetALLKeyRequest();
                result = getXmlFile();
                closeSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("delete".equals(strings[0])){
            try {
                Log.d("MANAGER","DELETE");
                startSocket();
                sendDeleteRequest(strings[1]);
                closeSocket();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        return result;
    }

    private void startSocket() throws IOException {
        socket = new Socket(shareContext.getLoginServerAddress(), shareContext.getLoginServerPort());
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        oos = new ObjectOutputStream(socket.getOutputStream());
    }

    private void closeSocket() throws IOException {
        socket.close();
    }

    private void sendGetALLKeyRequest() throws IOException {
        HashMap<String,String> requestMap = new HashMap<String, String>();
        requestMap.put("datatype", "manageKey");

        oos.writeObject(requestMap);
    }

    private File getXmlFile() throws IOException {
        File xmlFile = new File(MainActivity.rootFolder.getPath()+File.separator+"allKeys.xml");
        FileOutputStream fos = new FileOutputStream(xmlFile);

        // get file length
        int len = dis.readInt();
        byte[] tmp = new byte[len];
        dis.read(tmp,0,len);

        // save file
        fos.write(tmp);
        fos.close();

        return xmlFile;
    }

    private void sendDeleteRequest(String userName) throws IOException {
        HashMap<String,String> requestMap = new HashMap<String, String>();
        requestMap.put("datatype","deleteKey");
        requestMap.put("username",userName);
        oos.writeObject(requestMap);
    }


}

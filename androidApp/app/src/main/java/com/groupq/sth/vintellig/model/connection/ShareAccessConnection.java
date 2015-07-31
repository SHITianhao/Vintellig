package com.groupq.sth.vintellig.model.connection;

import android.os.AsyncTask;
import android.util.Log;

import com.groupq.sth.vintellig.Controller.MainActivity;
import com.groupq.sth.vintellig.ShareContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by feng on 11/06/2015.
 */
public class ShareAccessConnection extends AsyncTask<String, Void, Boolean> {

    String addressIP;
    String msg = null;
    int port;
    Socket socket = null;
    private ShareContext shareContext;

    public ShareAccessConnection(ShareContext shareContext){
        this.shareContext = shareContext;
        this.addressIP = shareContext.getLoginServerAddress();
        this.port = shareContext.getLoginServerPort();
    }

    public boolean share(String dstAddress){
        try {
            socket = new Socket(addressIP, port);
            File xmlRequestFile = new File(MainActivity.rootFolder.getAbsoluteFile()+ File.separator+"request.xml");
            FileInputStream fis = new FileInputStream(xmlRequestFile);

            // init dos and oos
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // write request hashMap
            HashMap<String,String> requestMap = new HashMap<String, String>();
            requestMap.put("datatype","shareAccess");
            requestMap.put("dstAddress",dstAddress);
            requestMap.put("userName", this.shareContext.getUserName());
            oos.writeObject(requestMap);

            int len = (int) xmlRequestFile.length();
            // length of file
            dos.writeInt(len);
            byte[] request = new byte[len];
            // read from file
            fis.read(request, 0, len);
            //write into dos
            dos.write(request);

            fis.close();

            // read file length
            len = dis.readInt();
            Log.d("Server",Integer.toString(len));
            byte[] fileShareBytes = new byte[len];
            dis.read(fileShareBytes,0,len);

            // save file
            File fileShare = new File(MainActivity.rootFolder.getPath()+File.separator+"welcome.vg");
            FileOutputStream fos = new FileOutputStream(fileShare);
            fos.write(fileShareBytes);

            fos.close();

            msg = dis.readUTF();
            socket.close();
            Log.d("share",msg);
            if("shareSuccess".equals(msg)){
                return true;
            } else {
                return false;
            }
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    protected Boolean doInBackground(String... params) {
        if (params.length>=1)
            return share(params[0]);
        return false;
    }
}

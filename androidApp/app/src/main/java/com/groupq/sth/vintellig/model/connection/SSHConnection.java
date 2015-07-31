package com.groupq.sth.vintellig.model.connection;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.groupq.sth.vintellig.model.manageKey.KeyGenerate;
import com.groupq.sth.vintellig.Controller.MainActivity;
import com.groupq.sth.vintellig.model.manageKey.ReadKeyFromFileThread;
import com.groupq.sth.vintellig.ShareContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by sth on 06/06/15.
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class SSHConnection extends AsyncTask<String, Void, Boolean> {


    private Socket socket;
    DataOutputStream dos;

    DataInputStream dis;
    String serverAddress = "192.168.1.36";
    String algorithm = "RSA";
    int port = 9090;
    Context appContext;
    Cipher cipher;
    public SSHConnection(ShareContext context){
        this.appContext = context;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        String authenName = params[0];
        Log.d("SSH",authenName);
        try {
            startSocket();
            sendUserName(authenName);
            String serverPubFilePath = saveServerPubKey();
            cipher = Cipher.getInstance(algorithm);
            ReadKeyFromFileThread serverPubKeyThread = new ReadKeyFromFileThread(serverPubFilePath,true);
            ReadKeyFromFileThread clientPrivKeyThread = new ReadKeyFromFileThread(KeyGenerate.getPrivateKeyFilePath(),false);
            serverPubKeyThread.start();
            clientPrivKeyThread.start();
            byte[] challenge = readChallenge();

            serverPubKeyThread.join();
            challenge = decryptByPublicKey(challenge,serverPubKeyThread.getKey());
            Log.d("Challenge", new String(challenge));

            clientPrivKeyThread.join();
            challenge = encipherByClientPrivateKey(challenge,clientPrivKeyThread.getKey());
            responseChallenge(challenge);
            boolean authen = readAuthenResult();
            Log.d("AUTHEN", Boolean.toString(authen));
            File serverPub = new File(serverPubFilePath);
            serverPub.delete();
            return authen;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void startSocket() throws IOException {
        socket = new Socket(serverAddress, port);
        // prepare out/in stream
        OutputStream o = socket.getOutputStream();
        dos = new DataOutputStream(o);
        InputStream in = socket.getInputStream();
        dis = new DataInputStream(in);
    }

    private byte[] readChallenge() throws IOException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException {
        int len = dis.readInt();
        byte[] challenge = new byte[len];
        if (len > 0) {
            dis.readFully(challenge);
        }
        return challenge;
    }

    private byte[] encipherByClientPrivateKey(byte[] challenge,Key privateKey) throws InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(challenge);
    }

    private byte[] decryptByPublicKey(byte[] encryptChallenge,Key publicKey) throws InvalidKeySpecException,
            NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encryptChallenge);
    }


    private void responseChallenge(byte[] challenge) throws IOException {
        dos.writeInt(challenge.length);
        if (challenge.length > 0) {
            dos.write(challenge, 0, challenge.length);
        }
    }

    private void sendUserName(String name) throws IOException {
        dos.writeInt(name.length());
        if (name.length() > 0) {
            dos.write(name.getBytes(), 0, name.length());
        }
    }

    private String saveServerPubKey() throws IOException {
        int len = dis.readInt();
        if (len > 0){
            String fileName = dis.readUTF();
            File serverPubKeyFile = new File(MainActivity.rootFolder.getAbsolutePath()+File.separator+fileName);
            serverPubKeyFile.createNewFile();
            DataOutputStream fileDos = new DataOutputStream(new FileOutputStream(serverPubKeyFile));
            byte[] fileBytes = new byte[len];
            this.dis.read(fileBytes,0,len);
            fileDos.write(fileBytes);
            return serverPubKeyFile.getPath();
        }
        return null;
    }

    private boolean readAuthenResult() throws IOException {
        return dis.readBoolean();
    }
}

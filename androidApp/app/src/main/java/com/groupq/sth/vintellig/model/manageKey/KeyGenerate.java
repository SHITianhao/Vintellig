package com.groupq.sth.vintellig.model.manageKey;

/**
 * Created by sth on 06/06/15.
 */
import android.util.Base64;
import android.util.Log;

import com.groupq.sth.vintellig.Controller.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyGenerate {
    private static String privFileName = "privClient";
    private static String pubFileName = "pubClient";
    String algorithme = "RSA";

    ByteArrayOutputStream out = new ByteArrayOutputStream();

    public KeyGenerate(){
        createKey();
    }

    private void createKey(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithme);
            keyGen.initialize(1024,new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();

            //get 2 keys
            Key privateKey = keyPair.getPrivate();
            Key publicKey = keyPair.getPublic();
            //encode by base64
//            publicK = Base64.encode(publicKey.getEncoded(),Base64.DEFAULT);
//            privateK=Base64.encode(privateKey.getEncoded());

            //write private key's file
            //if not exist then create a new one
            Log.d("CONTEXT", MainActivity.rootFolder.getAbsolutePath()+File.separator+privFileName);
            File privFile = new File(MainActivity.rootFolder.getAbsolutePath()+File.separator+privFileName);
            if (!privFile.exists()) {
                privFile.createNewFile();
                FileOutputStream privFileOut = new FileOutputStream(privFile);

                //write in using base64 string
                String privateK = Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT);
                privFileOut.write(privateK.getBytes());
                privFileOut.close();
            }

            //write public key's file
            //if not exist then create a new one
            File pubFile = new File(MainActivity.rootFolder.getAbsolutePath()+File.separator+pubFileName);
            if (!pubFile.exists()) {
                pubFile.createNewFile();
                FileOutputStream pubFileOut = new FileOutputStream(pubFile);

//                write in using base64 string
                String publicK= Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT);
                pubFileOut.write(publicK.getBytes());
//                pubFileOut.write(publicKey.getEncoded());
                pubFileOut.close();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String getPrivateKeyFilePath(){
        return MainActivity.rootFolder.getAbsolutePath()+File.separator+privFileName;
    }

    public static String getPublicKeyFilePath(){
        return MainActivity.rootFolder.getAbsolutePath()+File.separator+pubFileName;
    }

//    public static void main(String[] args) {
//        KeyGenerate generate = new KeyGenerate(true);
//        generate.createKey();
//    }

}

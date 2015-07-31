package com.groupq.sth.vintellig.model.manageKey;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.groupq.sth.vintellig.Controller.MainActivity;
import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.KeyManagerConnection;
import com.groupq.sth.vintellig.model.manageLock.KeyData;
import com.groupq.sth.vintellig.model.manageLock.KeyManagerAdaptor;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by sth on 10/06/15.
 */
public class ReadKeyFromFileThread extends Thread {
    String algorithme = "RSA";
    String filePath;
    boolean isPublicKey;
    Key key;
    public ReadKeyFromFileThread(String path,boolean isPublicKey){
        this.filePath = path;
        this.isPublicKey = isPublicKey;
    }


    @Override
    public void run() {
        try {
            if (isPublicKey)
                key = readPublicKeyFromFile(this.filePath);
            else
                key = readPrivateKeyFromFile(this.filePath);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private Key readPrivateKeyFromFile(String keyPath) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

        File privateKeyFile = new File(keyPath);

        String keyString = readFileIntoSingleLineString(privateKeyFile);
        // converts the String to a PublicKey instance
        KeyFactory keyFactory = KeyFactory.getInstance(algorithme);
        byte[] privateKeyBytes = Base64.decode(keyString.getBytes("utf-8"), Base64.DEFAULT);
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        RSAPrivateKey privKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);
        return privKey;
    }

    private Key readPublicKeyFromFile(String keyPath) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
        File pubKeyFile = new File(keyPath);
        if (!pubKeyFile.exists()) {
            return null;
        }
        String keyString = readFileIntoSingleLineString(pubKeyFile);

        // converts the String to a PublicKey instance
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.decode(keyString.getBytes("utf-8"), Base64.DEFAULT);
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(keyBytes);
        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(pubSpec);
        return pubKey;
    }

    private String readFileIntoSingleLineString(File file) throws IOException {
        // read the key stored in a file
        BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)));
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = br.readLine()) != null)
            lines.add(line);

        // concats the remaining lines to a single String
        StringBuilder sb = new StringBuilder();
        for (String aLine : lines)
            sb.append(aLine);
        return sb.toString();
    }

    public Key getKey(){
        return this.key;
    }

    /**
     * Created by sth on 11/06/15.
     */
    public static class ReadNewInviteFileActivity extends Activity {

        private Cipher cipher;
        private String algorithm = "RSA";
        private TextView inviteMessageView;
        private Intent intentToMain;
        private ShareContext shareContext;
        private String newMessage = "";
        private TextView senderNameView,messageView,startDateView,startTimeView,endDateView,endTimeView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_newinvite);
            inviteMessageView = (TextView)findViewById(R.id.new_invite_text);
            intentToMain = new Intent(this, MainActivity.class);
            shareContext = (ShareContext)getApplicationContext();

            senderNameView = (TextView) findViewById(R.id.sender_name_text);
            messageView = (TextView) findViewById(R.id.new_invite_text);
            startDateView = (TextView) findViewById(R.id.show_start_date);
            startTimeView = (TextView) findViewById(R.id.show_start_time);
            endDateView = (TextView) findViewById(R.id.show_end_date);
            endTimeView = (TextView) findViewById(R.id.show_end_time);

            InputStream attachment;
            try {
                // get attachment
                attachment = getContentResolver().openInputStream(
                        getIntent().getData());
                ReadKeyFromFileThread clientPrivKeyThread = new ReadKeyFromFileThread(KeyGenerate.getPrivateKeyFilePath(),false);
                clientPrivKeyThread.start();

                String tmp = getAttachmentName(getIntent().getData());
                final String attachmentName;
                if (tmp != null){
                    attachmentName = tmp.replaceAll(".vg","");
                }
                else{
                    attachmentName = null;
                }

                //save file
    //            int nbInvite = shareContext.getNbInviteMessage();
                File attachTmpFile = new File(MainActivity.rootFolder.getPath()+File.separator+attachmentName+"_tmp");

                // create file
                attachTmpFile.createNewFile();
                DataOutputStream os = new DataOutputStream(new FileOutputStream(attachTmpFile));
                byte[] buffer = new byte[1024];
                int byteRead = 0;
                while ((byteRead = attachment.read(buffer)) != -1) {
                    os.write(buffer, 0, byteRead);
                }
                // read from file
                DataInputStream dis = new DataInputStream(new FileInputStream(attachTmpFile));
    //            DataInputStream dis = new DataInputStream(new FileInputStream(attachmentFile));
                byte[] inviteMessage = new byte[(int)attachTmpFile.length()];
                dis.read(inviteMessage, 0, inviteMessage.length);
                clientPrivKeyThread.join();
                inviteMessage = decryptByKey(inviteMessage, clientPrivKeyThread.getKey());

                // write message decrypted into file
                File attachmentFile = new File(MainActivity.rootFolder.getPath() + File.separator + attachmentName);
                attachmentFile.createNewFile();
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(attachmentFile));
                dos.write(inviteMessage,0,inviteMessage.length);

                attachTmpFile.delete();

                syntaxXml(attachmentFile);

                Button okButton = (Button) findViewById(R.id.new_invite_okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isFromNewInvite", true);
                        bundle.putString("newMessage", newMessage);
                        if (attachmentName != null) {
                            bundle.putString("authenName", attachmentName);
                        }
                        intentToMain.putExtras(bundle);
                        startActivity(intentToMain);
                        finish();
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }


        }

        private byte[] decryptByKey(byte[] encryptMessage,Key key) throws InvalidKeySpecException,
                NoSuchAlgorithmException, IOException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException,
                NoSuchPaddingException {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            int MAX_BYTES = 128;
            int len = encryptMessage.length;
            int seg = len / MAX_BYTES;

            byte[] tmp = new byte[MAX_BYTES];
            byte[] result = new byte[len];
            // because of adding space at end of file, so il should be devised by 128.....so ugly!!
            for(int i = 0;i<seg;i++){
                System.arraycopy(encryptMessage, i * MAX_BYTES, tmp, 0, MAX_BYTES);
                tmp = cipher.doFinal(tmp);
                System.arraycopy(tmp, 0, result, i*MAX_BYTES, MAX_BYTES);
            }

            return result;
        }

        public static String getAttachmentName (Uri uri) {
            String path = uri.getPath();
            String[] tmps = path.split("/");
            path = tmps[tmps.length-1];
            return path;
        }

        public void syntaxXml(File xmlFile) throws XmlPullParserException, IOException {
            InputStream in = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "share");
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                Log.d("syn", name);
                // Starts by looking for the entry tag
                if (name.equals("receiver")){
                    readReceiver(parser);
                } else if (name.equals("sender")){
                    readSender(parser);
                }
            }
        }

        private void readSender(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "sender");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = readText(parser);
                senderNameView.setText(name);
                newMessage += "From: "+name+"\n";
            }
        }

        private void readReceiver(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "receiver");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("name"))
                    readText(parser);
                else if (name.equals("message")){
                    String message = readText(parser);
                    newMessage += "new message: "+message+"\n";
                    messageView.setText(message);
                }
                else if (name.equals("startDay"))
                    startDateView.setText(readDate(parser,"startDay"));
                else if (name.equals("startTime"))
                    startTimeView.setText(readTime(parser,"startTime"));
                else if (name.equals("endDay"))
                    endDateView.setText(readDate(parser,"endDay"));
                else if (name.equals("endTime"))
                    endTimeView.setText(readTime(parser,"endTime"));
            }
        }

        private String readDate(XmlPullParser parser,String node) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, node);
            String year = parser.getAttributeValue(null, "year");
            String month = parser.getAttributeValue(null, "month");
            String day = parser.getAttributeValue(null, "day");
            parser.next();
            return month+"-"+day+"-"+year;
        }

        private String readTime(XmlPullParser parser,String node) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, node);
            String hour = parser.getAttributeValue(null, "hour");
            String minute = parser.getAttributeValue(null, "minute");
            parser.next();
            return hour+":"+minute;
        }


        // For the tags title and summary, extracts their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            Log.d("READ_TXT",result);
            return result;
        }
    }

    /**
     * Created by sth on 6/16/15.
     */
    public static class ManagerActivity extends Activity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manager);

            KeyManagerConnection managerConnection = new KeyManagerConnection((ShareContext)getApplicationContext());
            File xmlFile = null;
            ArrayList<KeyData> keysData = new ArrayList<KeyData>();
            try {
    //            xmlFile = new File(MainActivity.rootFolder.getPath()+File.separator+"allKeys.xml");
    //            xmlFile.createNewFile();
                xmlFile  = managerConnection.execute("list").get();
                keysData = readXmlFile(xmlFile);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // 1. get a reference to recyclerView
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.key_manager_recy);
            // 2. set layoutManger
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // 3. create an adapter
            KeyManagerAdaptor mAdapter = new KeyManagerAdaptor(keysData,(ShareContext)getApplicationContext());
            // 4. set adapter
            recyclerView.setAdapter(mAdapter);
            // 5. set item animator to DefaultAnimator
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }

        private ArrayList<KeyData> readXmlFile(File xmlFile) throws IOException, XmlPullParserException {
            ArrayList<KeyData> keysData = new ArrayList<KeyData>();

            InputStream in = new FileInputStream(xmlFile);
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "allAccess");
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag
                if (name.equals("user")) {
                    readUser(parser, keysData);
                }
            }
            return keysData;
        }

        private void readUser(XmlPullParser parser,ArrayList<KeyData> keysData) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, null, "user");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "username");
            String userName = readText(parser);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "starttime");
            String startTime = readText(parser);
            startTime = startTime.substring(0,19);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "endtime");
            String endTime = readText(parser);
            Log.d("XML", endTime);
            if (endTime == null){
                endTime = "unlimited";
            }
            keysData.add(new KeyData(userName, startTime, endTime));
            Log.d("XML", "end user");
        }

        // For the tags title and summary, extracts their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }




    }

    /**
     * Created by feng on 09/06/2015.
     */
    public static class DeconnectActivity extends Activity{

        Button buttonDeconnect;
        TextView statement;
        ShareContext shareContext;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            shareContext = (ShareContext) getApplicationContext();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_deconnect);
            statement = (TextView)findViewById(R.id.statement);
            buttonDeconnect = (Button) findViewById(R.id.deconnectButton);

            buttonDeconnect.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    shareContext.setLoginState(false);
                    Intent intent = new Intent();
                    intent.setClass(DeconnectActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.action_main) {
                Intent intent = new Intent();
                intent.setClass(DeconnectActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(id == R.id.action_statement) {
                if(shareContext.getLoginState()) {
                    Intent intent = new Intent();
                    intent.setClass(DeconnectActivity.this, DeconnectActivity.class);
                    startActivity(intent);
                }
            }

            return super.onOptionsItemSelected(item);
        }
    }
}

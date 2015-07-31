package com.groupq.sth.vintellig.Controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.RegisterConnection;
import com.groupq.sth.vintellig.model.manageKey.KeyGenerate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * Created by feng on 10/06/2015.
 */
public class RegisterActivity extends Activity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    Button buttonRegister;
    EditText inputName,emailAdress;
    TextView message;
    private ShareContext shareContext;

    static File rootFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"vintellig");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = (Button) findViewById(R.id.register);
        inputName = (EditText) findViewById(R.id.nameinput);
        emailAdress = (EditText) findViewById(R.id.email_input);
        message = (TextView) findViewById(R.id.message);

        shareContext = (ShareContext)getApplicationContext();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String publicKeyString = null;
                if (!rootFolder.exists()){
                    rootFolder.mkdirs();
                    new KeyGenerate();
                }
                try {
                    publicKeyString = getStringFromFile(Environment.getExternalStorageDirectory()+File.separator+"vintellig/pubClient");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RegisterConnection register = new RegisterConnection(shareContext);

                try {
                    if(publicKeyString != null && !inputName.getText().toString().matches("")) {
//                        shareContext.setAuthenName(inputName.getText().toString());
                        // create google account
                        createAccount(inputName.getText().toString(),emailAdress.getText().toString());
                        boolean registerResult = register.execute(inputName.getText().toString(),
                                emailAdress.getText().toString(),
                                publicKeyString).get();
                        Log.d("REGIST Result",Boolean.toString(registerResult));
                        if (registerResult) {
                            Intent intent = new Intent();
                            intent.setClass(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            message.setText("RegisterFail");
                        }
                    } else{
                        message.setText("RegisterFail caused by empty name or generate key error please try again!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private void createAccount(String name, String email){
        AccountManager accountManager = AccountManager.get(this); //this is Activity
        Account account = new Account(name,"com.groupq.sth.vintellig.account.DEMOACCOUNT");
        Bundle bundle = new Bundle();
        bundle.putString("email",email);
        boolean success = accountManager.addAccountExplicitly(account, "password", bundle);
        if(success){
            Log.d("REGIS", "Account created");
        }else{
            Log.d("REGIS", "Account creation failed. Look at previous logs to investigate");
        }
    }


}

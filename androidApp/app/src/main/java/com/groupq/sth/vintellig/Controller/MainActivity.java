package com.groupq.sth.vintellig.Controller;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.groupq.sth.vintellig.R;
import com.groupq.sth.vintellig.ShareContext;
import com.groupq.sth.vintellig.model.connection.SSHConnection;
import com.groupq.sth.vintellig.model.login.LoginDialogFragment;
import com.groupq.sth.vintellig.model.login.LoginStateChangedEvent;
import com.groupq.sth.vintellig.model.login.LoginStateChangedListener;
import com.groupq.sth.vintellig.model.newInvite.ContactAdapter;
import com.groupq.sth.vintellig.model.newInvite.ContactInfo;
import com.groupq.sth.vintellig.model.newInvite.NewInviteEvent;
import com.groupq.sth.vintellig.model.newInvite.NewInviteListener;
import com.groupq.sth.vintellig.model.manageKey.ReadKeyFromFileThread;
import com.groupq.sth.vintellig.model.shareAccess.ShareAccessActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity implements LoginStateChangedListener,NewInviteListener{
    Button buttonAuthen,buttonBlueTooth,buttonLogin,shareButton,manageButton;
    Set<BluetoothDevice> devices;
    ShareContext shareContext;
    CardView loginCard,shareCard;
    TextView userNameView;
    static ArrayList<ContactInfo> inviteCards = new ArrayList<ContactInfo>();
    LinearLayout mainLayout;
    RecyclerView recyclerViewList;
    ContactAdapter adapter;
    RecyclerView.LayoutManager recLayoutManager;

    public static File rootFolder = new File(Environment.getExternalStorageDirectory()+File.separator+"vintellig");
    static int REQUEST_ENABLE_BT = 0;
    ArrayList<String> deviceList = new ArrayList<String>();
    BluetoothAdapter bluetoothAdapter;

    AccountManager accountManager;
    String accountType = "com.groupq.sth.vintellig.account.DEMOACCOUNT";


    @TargetApi(Build.VERSION_CODES.ECLAIR)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareContext = (ShareContext) getApplicationContext();
        // set shareContext listener
        shareContext.setLoginListener(this);
        shareContext.setNewInviteListener(this);

//        SharedPreferences preferences = getSharedPreferences("count",MODE_WORLD_READABLE);
//        int count = preferences.getInt("count", 0);
//        //verify the time of run
//        if (count == 0) {
//            Intent intent = new Intent();
//            intent.setClass(getApplicationContext(),RegisterActivity.class);
//            startActivity(intent);
//            this.finish();
//        }
//        SharedPreferences.Editor editor = preferences.edit();
//        //save the data
//        editor.putInt("count", ++count);
//        //commit the data
//        editor.commit();

        accountManager = AccountManager.get(MainActivity.this);
        if (accountManager.getAccountsByType(accountType).length == 0){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }


        Log.e("CONTEXT", rootFolder.getAbsolutePath());

        setContentView(R.layout.activity_main);
        mainLayout = (LinearLayout)findViewById(R.id.main_layout);
        loginCard = (CardView)findViewById(R.id.login_card);
        shareCard = (CardView)findViewById(R.id.share_card);
        userNameView = (TextView)findViewById(R.id.userNameTextView);

        // init for adding new invite
        recyclerViewList = (RecyclerView)findViewById(R.id.cardList);
        recyclerViewList.setHasFixedSize(true);
        recLayoutManager = new LinearLayoutManager(this);
        recyclerViewList.setLayoutManager(recLayoutManager);

        buttonAuthen = (Button)findViewById(R.id.authenticate);
        buttonAuthen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // get account's authen name
                    Account find = accountManager.getAccountsByType(accountType)[0];
                    String email = accountManager.getUserData(find,"email");
                    boolean authen = new SSHConnection((ShareContext)getApplicationContext()).execute(email).get();
                    showAuthenResult(authen);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        });

        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ShareAccessActivity.class);
                startActivity(intent);
            }
        });

        manageButton = (Button) findViewById(R.id.managerButton);
        manageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ReadKeyFromFileThread.ManagerActivity.class);
                startActivity(intent);
            }
        });

//        buttonBlueTooth = (Button)findViewById(R.id.bluetooth);
//        buttonBlueTooth.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View v) {
//
//                BluetoothDevice deviceConnection = null;
//                for (BluetoothDevice device : devices){
//                    Log.d("BLUTOOTH DEVICE",device.getName()+" "+device.getAddress());
//                    if ("STH".equals(device.getName())){
//                        Log.d("BLUTOOTH DEVICE","get STH");
//                        deviceConnection = device;
//                    }
//                }
//                Log.d("BLUTOOTH DEVICE","End list");
//                bluetoothAdapter.cancelDiscovery();
//                new BluetoothConnection(deviceConnection).start();
//            }
//        });
//
//        // If BT is not on, request that it be enabled.
//        // setupCommand() will then be called during onActivityResult
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (!bluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//        }
////        bluetoothAdapter.startDiscovery();
//        devices = bluetoothAdapter.getBondedDevices();
        buttonLogin = (Button)findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDialogFragment newFragment = new LoginDialogFragment();
                newFragment.setContext((ShareContext) getApplicationContext());
                newFragment.show(getFragmentManager(), "login");
            }
        });
        Bundle bundle = this.getIntent().getExtras();
        if (bundle!=null && bundle.getBoolean("isFromNewInvite")){
            String authenName = bundle.getString("authenName");
            if (authenName!=null){
                shareContext.addNbInviteMessage(authenName,bundle.getString("newMessage"));
            }
        }

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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_main) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_statement) {
            if(shareContext.getLoginState()) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ReadKeyFromFileThread.DeconnectActivity.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void dismissLoginCard(){
        this.loginCard.setVisibility(CardView.GONE);
    }

    public void showShareCard(){
        this.shareCard.setVisibility(CardView.VISIBLE);
    }

    public void createNewInviteCard(ContactInfo contactInfo){
        inviteCards.add(contactInfo);
        adapter = new ContactAdapter(inviteCards,this);
        recyclerViewList.setAdapter(adapter);
        mainLayout.removeView(recyclerViewList);
        mainLayout.addView(recyclerViewList);
        setContentView(mainLayout);
    }


    @Override
    public void loginStateChanged(LoginStateChangedEvent event) {
        if (event.isLogin()) {
            dismissLoginCard();
            showShareCard();
            userNameView.setText(getResources().getString(R.string.welcome)+" "+ shareContext.getUserName());
        }
    }

    @Override
    public void fireNewInvite(NewInviteEvent e) {
        Log.e("Main","NEW Invite");
        createNewInviteCard(new ContactInfo(e.getMessage(), R.drawable.profile_default_1));
    }

    private void showAuthenResult(boolean authen){
        if (authen) {
            Toast.makeText(this.getBaseContext(),R.string.authen_success,Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this.getBaseContext(),R.string.authen_failed,Toast.LENGTH_LONG).show();
        }

    }

}

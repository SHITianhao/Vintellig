package com.groupq.sth.vintellig;

import android.app.Application;
import android.util.Log;

import com.groupq.sth.vintellig.model.login.LoginStateChangedEvent;
import com.groupq.sth.vintellig.model.login.LoginStateChangedListener;
import com.groupq.sth.vintellig.model.newInvite.NewInviteEvent;
import com.groupq.sth.vintellig.model.newInvite.NewInviteListener;

/**
 * Created by feng on 09/06/2015.
 */
public class ShareContext extends Application{
    private boolean loginState = false;

    private boolean register = false;
    private String loginServerAddress = "192.168.1.77";
    private int loginServerPort = 1978;
    private String userName;

    private LoginStateChangedListener loginStateChangedListener;
    private NewInviteListener newInviteListener;

    private int nbInviteMessage = 0;

    public boolean getLoginState(){
        return loginState;
    }

    public void setLoginState(boolean loginState){
        loginStateChangedListener.loginStateChanged(new LoginStateChangedEvent(this,loginState));
        this.loginState = loginState;
        Log.d("LOGIN","login state change to "+Boolean.toString(this.loginState));
    }

    public boolean getRegister(){return register;}

    public void setRegister(boolean register){this.register = register;}

    public String getLoginServerAddress(){
        return this.loginServerAddress;
    }

    public int getLoginServerPort(){
        return this.loginServerPort;
    }

    public void setLoginListener(LoginStateChangedListener eventListener){
        this.loginStateChangedListener = eventListener;
    }

    public void setNewInviteListener(NewInviteListener eventListener){
        this.newInviteListener = eventListener;
    }

    public void addNbInviteMessage(String authenName, String newInviteMessage){
        this.nbInviteMessage++;
        this.newInviteListener.fireNewInvite(new NewInviteEvent(this,authenName,newInviteMessage));
    }

    public int getNbInviteMessage(){
        return this.nbInviteMessage;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

//    public void setAuthenName(String authenName){
//        this.authenName = authenName;
//    }

//    public String getAuthenName(){
//        return this.authenName;
//    }
}

package com.groupq.sth.vintellig.model.login;

import java.util.EventObject;

/**
 * Created by sth on 11/06/15.
 */
public class LoginStateChangedEvent extends EventObject {
    private boolean isLogin;
    /**
     * Constructs a new instance of this class.
     *
     * @param source the object which fired the event.
     */
    public LoginStateChangedEvent(Object source, boolean isLogin) {
        super(source);
        this.isLogin = isLogin;
    }

    public boolean isLogin(){
        return this.isLogin;
    }
}

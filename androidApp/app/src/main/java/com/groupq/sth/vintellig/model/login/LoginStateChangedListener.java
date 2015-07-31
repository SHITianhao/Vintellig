package com.groupq.sth.vintellig.model.login;

import java.util.EventListener;

/**
 * Created by sth on 11/06/15.
 */
public interface LoginStateChangedListener extends EventListener {
    public void loginStateChanged(LoginStateChangedEvent event);
}

package com.groupq.sth.vintellig.model.newInvite;

import java.util.EventObject;

/**
 * Created by sth on 11/06/15.
 */
public class NewInviteEvent extends EventObject {

    private String message;
    private String authenName;
    /**
     * Constructs a new instance of this class.
     *
     * @param source the object which fired the event.
     */
    public NewInviteEvent(Object source,String authenName,String message) {
        super(source);
        this.message = message;
        this.authenName = authenName;
    }

    public String getMessage(){
        return this.message;
    }

    public String getAuthenName(){
        return this.authenName;
    }

}

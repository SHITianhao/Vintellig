package com.groupq.sth.vintellig.model.newInvite;

import java.util.EventListener;

/**
 * Created by sth on 11/06/15.
 */
public interface NewInviteListener extends EventListener{
    void fireNewInvite(NewInviteEvent e);
}

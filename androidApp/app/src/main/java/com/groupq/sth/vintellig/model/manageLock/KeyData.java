package com.groupq.sth.vintellig.model.manageLock;

/**
 * Created by sth on 6/16/15.
 */
public class KeyData {
    private String ownerName, startTime, endTime;
    public KeyData(String name, String start, String end){
        this.ownerName = name;
        this.startTime = start;
        this.endTime = end;
    }

    public String getOwnerName(){
        return this.ownerName;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }
}

package com.roamtech.uc.jainsip.rxevent;

/**
 * Created by admin03 on 2016/8/31.
 */
public class BaseMessageEvent {
    protected String message;
    protected String from;
    protected String to;
    protected String displayname;
    protected String userid;
    public BaseMessageEvent(String from, String to, String message, String displayname, String userid) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.displayname = displayname;
        this.userid = userid;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

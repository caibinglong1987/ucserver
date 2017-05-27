package com.roamtech.uc.jainsip.rxevent;

/**
 * Created by admin03 on 2016/8/27.
 */
public class SendMessageEvent extends  BaseMessageEvent {
    public static final int NEWUSER_WELCOME=1;

    private Integer type;

    public SendMessageEvent(String from, String to, String message, String displayname, String userid, Integer type) {
        super(from,to,message,displayname,userid);
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}

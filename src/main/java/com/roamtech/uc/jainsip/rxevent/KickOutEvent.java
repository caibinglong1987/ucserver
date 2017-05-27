package com.roamtech.uc.jainsip.rxevent;

/**
 * Created by admin03 on 2016/8/27.
 */
public class KickOutEvent  extends  BaseMessageEvent {
    private String message;
    private String from;
    private String to;
    private String displayname;
    private String userid;

    public KickOutEvent(String to, String userid, String sessionid) {
        super("ucmsg",to,"{\"action\":\"kickout\",\"by\":\""+sessionid+"\",\"description\":\"您的帐号已在其他设备上登录\"}","系统消息",userid);
    }
}

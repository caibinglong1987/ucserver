package com.roamtech.uc.cache.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by roam-caochen on 2016/12/12.
 */
public class Token implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int tokenTimeout=86500;


    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "expires_in")
    private Integer expires;

    public Token() {

    }

    public Token(String accessToken) {
        this(accessToken, tokenTimeout);
    }

    public Token(String accessToken, Integer expires) {
        this.accessToken = accessToken;
        this.expires = expires;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }
}

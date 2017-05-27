package com.roamtech.uc.client;

import com.alibaba.fastjson.annotation.JSONField;
import com.roamtech.uc.cache.model.Token;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by admin03 on 2017/1/6.
 */
public class RoamtechTokenResponse implements Serializable {
    @JSONField(name = "error_no")
    private int errorNo;
    @JSONField(name = "error_info")
    private String errorInfo;
    @JSONField(name = "result")
    private Map<String, Token> attributes;

/*    class Token implements Serializable {
        @JSONField(name = "access_token")
        private String accessToken;
        @JSONField(name = "expires_in")
        private Long expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
        }
    }*/

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Map<String, Token> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Token> attributes) {
        this.attributes = attributes;
    }
}

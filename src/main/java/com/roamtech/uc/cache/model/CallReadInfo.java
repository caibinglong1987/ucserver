package com.roamtech.uc.cache.model;

import java.io.Serializable;

/**
 * Created by roam-caochen on 2017/2/20.
 */
public class CallReadInfo implements Serializable {

    private Long userId;
    private Long readId;//已读通话最后的id

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReadId() {
        return readId;
    }

    public void setReadId(Long readId) {
        this.readId = readId;
    }
}

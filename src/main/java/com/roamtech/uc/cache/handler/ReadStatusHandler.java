package com.roamtech.uc.cache.handler;

import com.roamtech.uc.cache.RDCache;
import com.roamtech.uc.cache.model.CallReadInfo;
import com.roamtech.uc.cache.model.MessageReadInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Created by roam-caochen on 2017/2/20.
 */
@Component
public class ReadStatusHandler implements InitializingBean {
    private RDCache callCache;
    private RDCache messageCache;
    @Autowired
    @Qualifier("ucCacheManager")
    private CacheManager cacheManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        callCache  = (RDCache)cacheManager.getCache("callReadInfo");
        messageCache  = (RDCache)cacheManager.getCache("messageReadInfo");
    }

    public CallReadInfo getCallReadInfo(Long userId) {
        return callCache.get(userId.toString(), CallReadInfo.class);
    }

    public MessageReadInfo getMessageReadInfo(Long userId) {
        return messageCache.get(userId.toString(), MessageReadInfo.class);
    }

    public void saveCallReadInfo(CallReadInfo callReadInfo) {
        callCache.put(callReadInfo.getUserId().toString(), callReadInfo);
    }

    public void saveMessageReadInfo(MessageReadInfo messageReadInfo) {
        messageCache.put(messageReadInfo.getUserId().toString(), messageReadInfo);
    }
}

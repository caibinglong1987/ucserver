package com.roamtech.uc.client;

import com.roamtech.uc.client.wx.WXToken;
import rx.Observable;


/**
 * Created by admin03 on 2017/1/6.
 */
public interface RoamtechApis {
    RoamtechTokenResponse getToken(String appId, String appKey, String phone);
    Observable<WXToken> getWXMPTokenAsync(String code, String state);
    Observable<WXToken> getWXMPRefreshTokenAsync(String refreshToken);
    WXToken getWXMPToken(String code, String state);
    WXToken getWXMPRefreshToken(String refreshToken);
}

package com.roamtech.uc.client.wx;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by admin03 on 2017/2/20.
 */
public interface WXMPService {
    @GET
    Observable<WXToken> GetAccessToken(@Url String url);

    @GET
    Observable<WXToken> GetRefreshToken(@Url String url);
}

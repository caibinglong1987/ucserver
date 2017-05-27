package com.roamtech.uc.client;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by admin03 on 2016/8/18.
 */
public interface HKUnicomApiService {
    @POST("addpack")
    Observable<HKUnicomResponse> addpack(@Body HKUnicomAddPackRequest params);
    @POST("delpack")
    Observable<HKUnicomResponse> delpack(@Body HKUnicomDelPackRequest params);
}

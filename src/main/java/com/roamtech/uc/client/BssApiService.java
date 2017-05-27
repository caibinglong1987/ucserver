package com.roamtech.uc.client;

import retrofit2.http.GET;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by admin03 on 2016/8/18.
 */
public interface BssApiService {
    @GET
    Observable<BatchJobStatus> GetAsyncBatchJobStatusAPI(@Url String url);
}

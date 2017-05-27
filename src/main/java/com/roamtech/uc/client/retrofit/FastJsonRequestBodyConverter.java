package com.roamtech.uc.client.retrofit;

import com.roamtech.uc.util.JSONUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import retrofit2.Converter;

import java.io.IOException;

/**
 * Created by admin03 on 2016/8/18.
 */
public class FastJsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(MEDIA_TYPE, JSONUtils.serialize(value));
    }
}

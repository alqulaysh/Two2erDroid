package com.se491.app.two2er;

import com.stormpath.sdk.Stormpath;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by pazra on 4/30/2017.
 */

public class OkHttpClientFactory {

    private OkHttpClientFactory() { }

    public static OkHttpClient Create() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Stormpath.logger().d(message);
            }
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();
    }
}

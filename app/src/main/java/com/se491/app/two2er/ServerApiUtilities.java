package com.se491.app.two2er;

import com.stormpath.sdk.utils.StringUtils;

import okhttp3.Headers;

/**
 * Created by pazra on 4/15/2017.
 */

public class ServerApiUtilities {

    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/";

    public static Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }

    public static String GetServerApiUrl() {
        return SERVER_API_URL;
    }
}

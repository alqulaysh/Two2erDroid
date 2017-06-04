package com.se491.app.two2er.Utilities;

import com.stormpath.sdk.utils.StringUtils;

import okhttp3.Headers;

/**
 * Created by pazra on 4/15/2017.
 */

public class ServerApiUtilities {


    //API Endpoint Routes:
    public static final String SERVER_API_URL_ROUTE_BOOKING = "booking/";
    public static final String SERVER_API_URL_ROUTE_BOOKING_CONFIRM = "confirm/";
    public static final String SERVER_API_URL_ROUTE_BOOKING_DELCINE = "decline/";
    public static final String SERVER_API_URL_ROUTE_BOOKING_CANCLE = "cancel/";
    public static final String SERVER_API_URL_ROUTE_BOOKING_GET_TUTOR_SCHEDULE = "getTutorSchedule/";
    public static final String SERVER_API_URL_ROUTE_USERS = "users/";
    public static final String SERVER_API_URL_ROUTE_TUTORS = "tutors/";
    public static final String SERVER_API_URL_ROUTE_USERS_ME = "me/";
    public static final String SERVER_API_URL_ROUTE_USERS_UPDATE = "update/";
    public static final String SERVER_API_URL_ROUTE_SUBJECTS = "subjects/";
    public static final String SERVER_API_URL_ROUTE_CHANGEPWD = "changepassword/";

    private static final String SERVER_API_URL_AUTH = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/";
    private static final String SERVER_API_URL_NOAUTH = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/api/";
    private static final String SERVER_API_URL_ROUTES_TEST = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/";
    private static final String SERVER_API_URL_S3 = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/s3";

    public static Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }

    public static String GetServerApiUrl() { return SERVER_API_URL_AUTH; }
    public static String GetServerS3Url() { return SERVER_API_URL_S3; }
    public static String GetServerApiUrl_NoAuth() {
        return SERVER_API_URL_NOAUTH;
    }
    public static String GetServerApiUrl_RoutesTest() {
        return SERVER_API_URL_ROUTES_TEST;
    }
}

package com.se491.app.two2er.Fragments.Bookings;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by eoliv on 3/1/2017.
 */

public class PostToBookings extends AsyncTask<Void, Void, Void> {
    private OkHttpClient okHttpClient;
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/booking/";
    private int responseStatus;
    private View currentRow;
    //Constructor for when we are posting to Request:
    public PostToBookings(String tutorUserId, String meetingDate) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Stormpath.logger().d(message);
            }
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();
        postToRequest(tutorUserId, meetingDate);
    }
    //Constructor for when we are posting to Accept:
    public PostToBookings(View finalRow, String bookingId, String type) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Stormpath.logger().d(message);
            }
        });

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        this.okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(httpLoggingInterceptor)
                .build();
        currentRow = finalRow;
        if(type.equals("accept")) {
            postToStatus(bookingId, type);
        }
        if(type.equals("reject")){
            postToStatus(bookingId, type);
        }
    }

    public void postToStatus(String bookingId, String type){
        RequestBody requestBody = new FormBody.Builder()
                .add("booking_id", bookingId)
                .build();
        postToApi(type, requestBody);
    }

    public void postToRequest(String tutorUserId, String meetingDate){
        RequestBody requestBody = new FormBody.Builder()
                .add("tutor_user_id", tutorUserId)
                .add("scheduledmeetingdate", meetingDate)
                .build();
        postToApi("request", requestBody);
    }

    public int postToApi(String endPoint, RequestBody requestBody) {
        Request request = new Request.Builder()
                .url(SERVER_API_URL + endPoint)
                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 2;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e("PostToBookings", "My Response: " + response.toString());
                responseStatus = 1;
            }
        });
        return responseStatus;
    }

    private Headers buildStandardHeaders(String accessToken) {

        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }

    @Override
    protected Void doInBackground(Void... params) {

        return null;
    }
}

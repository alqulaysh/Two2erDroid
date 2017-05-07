package com.se491.app.two2er.Fragments.Bookings;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.se491.app.two2er.OkHttpClientFactory;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;
import java.io.IOException;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eoliv on 3/1/2017.
 */

public class PostToBookings extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "PostToBookings";
    private OkHttpClient okHttpClient;
    private int responseStatus;

    //Constructor for when we are posting to Request:
    public PostToBookings(String tutorUserId, String meetingDate) {
        this.okHttpClient = OkHttpClientFactory.Create();
        postToRequest(tutorUserId, meetingDate);
    }

    //Constructor for when we are posting to Accept:
    public PostToBookings(View finalRow, String bookingId, String type) {

        this.okHttpClient = OkHttpClientFactory.Create();
        postToStatus(bookingId, type);

    }

    public void postToStatus(String bookingId, String type){
        RequestBody requestBody = new FormBody.Builder()
                .add("booking_id", bookingId)
                .build();
        Log.i(TAG, "Posting to Booking");
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
        Log.e(TAG, "PostToApi");

        Request request = new Request.Builder()
                .url(ServerApiUtilities.GetServerApiUrl() +
                        ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING +
                        endPoint)
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 2;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e(TAG, "My Response: " + response.toString());
                responseStatus = 1;
            }
        });
        return responseStatus;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}

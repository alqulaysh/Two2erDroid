/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.Fragments.Bookings;

import android.os.AsyncTask;
import android.util.Log;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class GetBookings extends AsyncTask<Object, Object, ArrayList<BookingObject>> {
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/booking";

    private ArrayList<BookingObject> bookingList = new ArrayList<BookingObject>();
    private String additionalURL = "";
    private OkHttpClient okHttpClient;
    private int responseStatus;
    public GetBookings() {
        additionalURL = "";
    }
    BookingsFragment myBookingsFragment;

    GetBookings(BookingsFragment myBookingsFragment) {
        this.myBookingsFragment = myBookingsFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    protected ArrayList<BookingObject> doInBackground(Object... arg0) {
        Request request = new Request.Builder()
                .url(SERVER_API_URL + additionalURL)
                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                .get()
                .build();


        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 2;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                JSONArray bookings;

                try {
                    String jsonResponse = response.body().string();
                    Log.e("Inside doInBackGround", "Response from url in GetBookings(): " + SERVER_API_URL + additionalURL );
                    Log.e("Inside doInBackGround", "Response from url in GetBookings(): " + jsonResponse );

                    bookings = new JSONArray(jsonResponse);
                    for (int i = 0; i < bookings.length(); i++) {
                        BookingObject booking = new BookingObject(bookings.getJSONObject(i));
                        bookingList.add(booking);
                    }
                    responseStatus = 1;
                }
                catch (final JSONException e) {
                    responseStatus = 1;
                    Log.e("Inside doInBackGround", "Json parsing error: " + e.getMessage());
                }
            }

        });

        while(responseStatus < 1){}

        return bookingList;
    }

    @Override
    protected void onPostExecute(ArrayList<BookingObject> result) {
        super.onPostExecute(result);

    }
}

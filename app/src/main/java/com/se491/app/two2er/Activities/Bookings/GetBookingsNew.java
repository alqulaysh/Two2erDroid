/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.Activities.Bookings;

import android.util.Log;

import com.se491.app.two2er.HelperObjects.BookingObject;
import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Request;
import okhttp3.Response;


public class GetBookingsNew extends Thread {

    public static String TAG = "GetBookingNew";
    private ArrayList<BookingObject> bookingList = new ArrayList<BookingObject>();

    private String getURL() {
        return ServerApiUtilities.GetServerApiUrl() + ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING;
    }
    public GetBookingsNew() {
    }

    public ArrayList<BookingObject> getBookingList() {
        return bookingList; }

    @Override
    public void run() {
            Request request = new Request.Builder()
                    .url(getURL())
                    .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                    .get()
                    .build();
            try {
                Response response = OkHttpClientFactory.Create().newCall(request).execute();

                String jsonResponse = response.body().string();
                Log.i(TAG, "URL used in GetBookingsNew: " + getURL());

                JSONArray bookings = new JSONArray(jsonResponse);
                for (int i = 0; i < bookings.length(); i++) {
                    BookingObject booking = new BookingObject(bookings.getJSONObject(i));
                    bookingList.add(booking);
                }
                Collections.reverse(bookingList);
                Log.i(TAG, "Size of bookingList: " + bookingList.size());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}

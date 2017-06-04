package com.se491.app.two2er.GetUsers;

import android.util.Log;

import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
import com.se491.app.two2er.Services.LocationRefreshService;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pazra on 5/3/2017.
 */

public class DistanceRefreshStrategy extends RefreshStrategyBase {
    private double distance = 100;
    private double dLong = LocationRefreshService.getLongitude();
    private double dLat = LocationRefreshService.getLatitude();

    private String getURL() {
        return ServerApiUtilities.GetServerApiUrl() + String.format("users/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f"
                , distance
                , dLong
                , dLat);
    }

    public void setDistance(double val) { distance = val;}
    public double getDistance() { return distance; }
    public void setLongitude(double val) { dLong = val; }
    public void setLatitude(double val) { dLat = val; }

    public DistanceRefreshStrategy() { }

    public DistanceRefreshStrategy(double dist) {
        distance = dist;
    }

    public DistanceRefreshStrategy(double dist, double longitude, double latitude) {
        distance = dist;
        dLong = longitude;
        dLat = latitude;
    }

    @Override
    public void run() {
        Log.i(TAG, "Running refresh users by distance");

        //dLong = LocationRefreshService.getLongitude();
        //dLat = LocationRefreshService.getLatitude();

        dLong = CurrentUser.getCurrentUser().dLong;
        dLat = CurrentUser.getCurrentUser().dLat;

        Request request = new Request.Builder()
                .url(getURL())
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .get()
                .build();
        try {
            Response response = OkHttpClientFactory.Create().newCall(request).execute();

            String jsonResponse = response.body().string();
            Log.i(TAG, "URL used in GetUsers(): " + getURL());

            JSONArray users = new JSONArray(jsonResponse);

            for (int i = 0; i < users.length(); i++) {
                UserObject user = new UserObject(users.getJSONObject(i));
                tempUsersList.put(user.id, user);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}

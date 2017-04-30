/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er;

import android.util.Log;

import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;


public class GetUsers extends Thread {
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users";

    private SideMenuActivity myMapActivity;
    private HashMap<String, UserObject> tempUsersList = new HashMap<String, UserObject>();

    private Runnable refreshStrategy;

    public GetUsers(SideMenuActivity myActivity) {
        myMapActivity = myActivity;
        refreshStrategy = new DistanceRefreshStrategy();
    }

    public void setDefaultSearchStrategy() {
        refreshStrategy = new DistanceRefreshStrategy();
    }

    public void setFilterSearchStrategy(String filter) {
        refreshStrategy = new RefreshUsersBySubjectFilter(filter);
    }

    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }

    @Override
    public void run() {
        refreshStrategy.run();
    }

    public class DistanceRefreshStrategy implements Runnable {
        private double distance = 100;
        private double dLong = -87.6254;
        private double dLat = 41.8782;

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

        public DistanceRefreshStrategy(double dist, double longitude, double latitude) {
            distance = dist;
            dLong = longitude;
            dLat = latitude;
        }

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
                Log.e("Inside doInBackGround", "URL used in GetUsers(): " + getURL());

                JSONArray users = new JSONArray(jsonResponse);

                for (int i = 0; i < users.length(); i++) {
                    UserObject user = new UserObject(users.getJSONObject(i));
                    tempUsersList.put(user.id, user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class RefreshUsersBySubjectFilter implements Runnable {
        private String filterValue = "";
        public RefreshUsersBySubjectFilter(String filter) {
            filterValue = filter;
        }

        // TODO get the correct URL
        private String getURL() {
            return ServerApiUtilities.GetServerApiUrl() + String.format("users/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f", filterValue);
        }

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
                Log.e("Inside doInBackGround", "URL used in GetUsers(): " + getURL());

                JSONArray users = new JSONArray(jsonResponse);
                for (int i = 0; i < users.length(); i++) {
                    UserObject user = new UserObject(users.getJSONObject(i));
                    tempUsersList.put(user.id, user);
//                    if (!myMapActivity.usersAround.containsKey(user.id)) {
//                        Log.e("Inside doInBackGround", "User to usersAround ID: " + user.id);
//                        Log.e("Inside doInBackGround", "User to usersAround First Name: " + user.fname);
//                        myMapActivity.usersAround.put(user.id, user);
//                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

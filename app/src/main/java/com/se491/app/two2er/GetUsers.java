/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.se491.app.two2er.Services.LocationRefreshService;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class GetUsers extends AsyncTask<Void, Void, Integer> {
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users";

    private SideMenuActivity myMapActivity;
    private HashMap<String, UserObject> tempUsersList = new HashMap<String, UserObject>();

    private Runnable refreshStrategy;

    public GetUsers(SideMenuActivity myActivity) {
        myMapActivity = myActivity;
        refreshStrategy = new DefaultRefreshStrategy();
    }

    public void setDefaultSearchStrategy() {
        refreshStrategy = new DefaultRefreshStrategy();
    }

    public void setFilterSearchStrategy(String filter) {
        refreshStrategy = new RefreshUsersBySubjectFilter(filter);
    }

    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    protected Integer doInBackground(Void... arg0) {
        refreshStrategy.run();
        return 1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        //myMapActivity.usersAround.clear();
        Log.e("Inside onPostExecute", "the size of TempUsers before Add: " + tempUsersList.size());
        //myMapActivity.usersAround.addAll(tempUsersList);
        Log.e("Inside onPostExecute", "the size of UsersAround after add: " + myMapActivity.usersAround.size());
        //myMapActivity.handleFindTutor();
        //addUsersToMap();
        super.onPostExecute(result);
    }

    //This function adds our users to the map after they are obtained from our API.
    private void addUsersToMap() {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tutormapicon));
        Iterator it = tempUsersList.entrySet().iterator();
        //myMapActivity.mGoogleMap.clear();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            UserObject user = (UserObject) pair.getValue();
            LatLng lNewLocation = new LatLng(user.dLat, user.dLong);
            //Get the users name:
            String sTitle = user.fname + " " + user.lname;

            //We store the users id on the marker snippet.
            markerOptions.position(lNewLocation).title(sTitle).snippet(user.id);
            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(myMapActivity.resizeMapIcons("genuser", 100, 100))); //icon and size of tutors icons inside Google Map
            //Add the markers on the map:
            myMapActivity.mGoogleMap.addMarker(markerOptions);
        }
    }

    // This method for changing the size of the tutors icons inside Google Map _ DEPRECATED due to performance issues.
    private Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(myMapActivity.getResources(), myMapActivity.getResources().getIdentifier(iconName, "drawable", myMapActivity.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



    public class DefaultRefreshStrategy implements Runnable {
        private double distance = 100;
        private String getURL() {
            return ServerApiUtilities.GetServerApiUrl() + String.format("users/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f"
                    , distance
                    , -87.6254
                    , 41.8782);
        }

        @Override
        public void run() {
            Request request = new Request.Builder()
                    .url(getURL())
                    .headers(buildStandardHeaders(Stormpath.getAccessToken()))
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
                    .headers(buildStandardHeaders(Stormpath.getAccessToken()))
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

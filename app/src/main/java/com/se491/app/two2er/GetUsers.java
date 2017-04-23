/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
    private String additionalURL = "";
    private int typeOfCall;
    private UserObject myTempUser;
    private OkHttpClient okHttpClient;
    private int responseStatus;

    public GetUsers(SideMenuActivity myActivity) {
        additionalURL = "/me";
        myMapActivity = myActivity;
        typeOfCall = 2;
    }

    public GetUsers(SideMenuActivity myActivity, Double distance, Double lon, Double lat) {
        additionalURL = String.format("/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f", distance, lon, lat);
        myMapActivity = myActivity;
        typeOfCall = 1;
    }

    public GetUsers(SideMenuActivity myActivity, String filter) {
        additionalURL = String.format("/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f", filter);
        myMapActivity = myActivity;
        typeOfCall = 1;
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
    protected Integer doInBackground(Void... arg0) {
        Request request = new Request.Builder()
                .url(SERVER_API_URL + additionalURL)
                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                .get()
                .build();


        try {
            Response response = okHttpClient.newCall(request).execute();

                String jsonResponse = response.body().string();
                Log.e("Inside doInBackGround", "URL used in GetUsers(): " + SERVER_API_URL + additionalURL );

                if(typeOfCall == 1 ){
                    JSONArray users = new JSONArray(jsonResponse);
                    for (int i = 0; i < users.length(); i++) {
                        UserObject user = new UserObject(users.getJSONObject(i));
                        tempUsersList.put(user.id, user);
                        if (!myMapActivity.usersAround.containsKey(user.id)){
                            Log.e("Inside doInBackGround", "User to usersAround ID: " + user.id);
                            Log.e("Inside doInBackGround", "User to usersAround First Name: " + user.fname);
                            myMapActivity.usersAround.put(user.id, user);
                        }

                    }
                }

                else{
                    JSONObject myUser = new JSONObject(jsonResponse);
                    myTempUser = new UserObject(myUser);
                    myMapActivity.myUserProfile = new UserObject(myUser);
                    Log.e("Inside doInBackGround", "My user ID: " + myTempUser.id);
                    Log.e("Inside doInBackGround", "My user name: " + myTempUser.fname);
                    Log.e("Inside doInBackGround", "My user img URL: " + myTempUser.userImage);
                }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                responseStatus = 1;
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, Response response) throws IOException {
//                JSONObject myUser;
//                JSONArray users;
//
//                try {
//                    String jsonResponse = response.body().string();
//                    Log.e("Inside doInBackGround", "Response from url in Get2Body(): " + SERVER_API_URL + additionalURL );
//
//                    if(typeOfCall == 1 ){
//                        users = new JSONArray(jsonResponse);
//                        for (int i = 0; i < users.length(); i++) {
//                            UserObject user = new UserObject(users.getJSONObject(i));
//                            tempUsersList.put(user.id, user);
//                            Log.e("Inside doInBackGround", "Added tempUser: " + tempUsersList.size());
//                            if (!myMapActivity.usersAround.containsKey(user.id)){
//                                Log.e("Inside doInBackGround", "Added tempUser: " + user.id);
//                                Log.e("Inside doInBackGround", "Added tempUser: " + user.fname);
//                                myMapActivity.usersAround.put(user.id, user);
//                            }
//
//                        }
//                    }
//
//                    else{
//                        myUser = new JSONObject(jsonResponse);
//                        myTempUser = new UserObject(myUser);
//                        //myMapActivity.myUserProfile = myTempUser;
//                        Log.e("Inside doInBackGround", "My user ID: " + myTempUser.id);
//                        Log.e("Inside doInBackGround", "My user img URL: " + myTempUser.userImage);
//
//
//                    }
//                }
//                catch (final JSONException e) {
//                    Log.e("Inside doInBackGround", "Json parsing error: " + e.getMessage());
//                    myMapActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(myMapActivity.getApplicationContext(),
//                                    "Json parsing error: " + e.getMessage(),
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }
//                responseStatus = 2;
//            }
    //});

        //while(responseStatus < 1){}

        return 1;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if(typeOfCall == 1){
            //myMapActivity.usersAround.clear();
            Log.e("Inside onPostExecute", "the size of TempUsers before Add: " + tempUsersList.size());
            //myMapActivity.usersAround.addAll(tempUsersList);
            Log.e("Inside onPostExecute", "the size of UsersAround after add: " + myMapActivity.usersAround.size());
            //myMapActivity.handleFindTutor();
            addUsersToMap();

        }
        else{
            if(typeOfCall == 2) {
                //myMapActivity.myUserProfile = myTempUser;
            }
        }
    }

    //This function adds our users to the map after they are obtained from our API.
    private void addUsersToMap(){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.tutormapicon));
        Iterator it = tempUsersList.entrySet().iterator();
        myMapActivity.mGoogleMap.clear();
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
    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(myMapActivity.getResources(),myMapActivity.getResources().getIdentifier(iconName, "drawable", myMapActivity.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}

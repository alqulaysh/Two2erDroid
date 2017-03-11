/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class GetUsers extends AsyncTask<Void, Void, Integer> {
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users";
    //http://server.scilingo.net:8080/api/users/findWithin/milesLonLat/1000/-83/53

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

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 1;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                JSONObject myUser;
                JSONArray users;

                try {
                    String jsonResponse = response.body().string();
                    Log.e("Inside doInBackGround", "Response from url in Get2Body(): " + SERVER_API_URL + additionalURL );

                    if(typeOfCall == 1 ){
                        users = new JSONArray(jsonResponse);
                        for (int i = 0; i < users.length(); i++) {
                            UserObject user = new UserObject(users.getJSONObject(i));
                            tempUsersList.put(user.id, user);
                            Log.e("Inside doInBackGround", "Added tempUser: " + tempUsersList.size());
                            if (!myMapActivity.usersAround.containsKey(user.id)){
                                myMapActivity.usersAround.put(user.id, user);
                            }

                        }
                    }

                    else{
                        myUser = new JSONObject(jsonResponse);
                        myTempUser = new UserObject(myUser);
                        myMapActivity.myUserProfile = myTempUser;
                        Log.e("Inside doInBackGround", "My user ID: " + myTempUser.id);
                        Log.e("Inside doInBackGround", "My user img URL: " + myTempUser.userImage);


                    }
                }
                catch (final JSONException e) {
                    Log.e("Inside doInBackGround", "Json parsing error: " + e.getMessage());
                    myMapActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(myMapActivity.getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
                responseStatus = 2;
            }

        });

        while(responseStatus < 1){}

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
            myMapActivity.handleFindTutor();
        }
        else{
            if(typeOfCall == 2) {
                myMapActivity.myUserProfile = myTempUser;
            }
        }
    }
}

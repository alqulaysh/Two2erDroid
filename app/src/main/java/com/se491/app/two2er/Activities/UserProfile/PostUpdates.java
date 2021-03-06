package com.se491.app.two2er.Activities.UserProfile;

import android.os.AsyncTask;

import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eoliv on 3/1/2017.
 * //Used to Update our UserProfile:
 */

public class PostUpdates  extends AsyncTask<Void, Void, Void> {
    private OkHttpClient okHttpClient;

    public PostUpdates(UserObject user) {
        this.okHttpClient = OkHttpClientFactory.Create();
        postToApi(user);
    }

    //{user_id, name, age, location, education, usergroups, image_url, about, defaultlocation, userMode}
    public void postToApi(UserObject user) {
        RequestBody requestBody = new FormBody.Builder()
                .add("name", user.fname + " " + user.lname)
                .add("age", user.age)
                .add("education", buildEducationArrayString(user))
                //.add("usergroups", user.Education)
                .build();

        Request request = new Request.Builder()
                .url(ServerApiUtilities.GetServerApiUrl() +
                        ServerApiUtilities.SERVER_API_URL_ROUTE_USERS +
                        ServerApiUtilities.SERVER_API_URL_ROUTE_USERS_UPDATE)
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {

            }

        });
    }

    private String buildEducationArrayString(UserObject user) {
        String toRtn = "";
        try {
            JSONArray arEducation = new JSONArray();
            arEducation.put(0, user.Education.toJson());
            toRtn = arEducation.toString();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return toRtn;
    }

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}

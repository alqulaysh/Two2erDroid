package com.se491.app.two2er.Activities;

import android.os.AsyncTask;

import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
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
 * //Used to Update our UserProfile:
 */

public class PostCreateTutor extends AsyncTask<Void, Void, Void> {
    private OkHttpClient okHttpClient;

    public PostCreateTutor() {
        this.okHttpClient = OkHttpClientFactory.Create();
        postToApi();
    }

    //{user_id, name, age, location, education, usergroups, image_url, about, defaultlocation, userMode}
    public void postToApi() {
        RequestBody requestBody = new FormBody.Builder()
                .add("usergroups", "[Student,Tutor]")
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

    @Override
    protected Void doInBackground(Void... params) {
        return null;
    }
}

package com.se491.app.two2er;

import android.os.AsyncTask;
import android.util.Log;

import com.se491.app.two2er.HelperObjects.UserObject;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by eoliv on 3/1/2017.
 * //Used to Update our UserProfile:
 */

public class PostUpdates  extends AsyncTask<Void, Void, Void> {
    private OkHttpClient okHttpClient;
    private static final String SERVER_API_URL = "http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users";
    private int responseStatus;
    public PostUpdates(UserObject user) {
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
        postToApi(user);
    }

    //{user_id, name, age, location, education, usergroups, image_url, about, defaultlocation, userMode}
    public void postToApi(UserObject user) {
        RequestBody requestBody = new FormBody.Builder()
                .add("name", user.fname + " " + user.lname)
                //.add("age", user.age)
                //.add("education", user.)
                //.add("usergroups", name)
                .build();

        Request request = new Request.Builder()
                .url(SERVER_API_URL + "/update")
                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 2;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e("Inside PostUpdates", "Json parsing error: " + response.message());
                responseStatus = 1;
            }

        });
        while(responseStatus < 1){}
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
    protected Void doInBackground(Void... params) {

        return null;
    }
}

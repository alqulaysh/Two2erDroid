package com.se491.app.two2er;

import android.util.Log;

import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pazra on 4/30/2017.
 */

public class CurrentUser {
    private static UserObject currentUser;
    private static String TAG = "CurrentUser";

    private CurrentUser() { }

    public static synchronized UserObject getCurrentUser() {
        if (currentUser == null)
            currentUser = new UserObject();

        return currentUser;
    }

    public static synchronized void setCurrentUser(UserObject obj) {
        currentUser = obj;
    }

    public static void Init() {
        if (currentUser != null)
            return;

        Refresh();
    }

    public static void Refresh() {
        Request request = new Request.Builder()
                .url(ServerApiUtilities.GetServerApiUrl() + "/me")
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .get()
                .build();
        try {
            OkHttpClient okHttpClient = OkHttpClientFactory.Create();
            Response response = okHttpClient.newCall(request).execute();

            String jsonResponse = response.body().string();

            Log.e(TAG, "URL used in CurrentUser: " + ServerApiUtilities.GetServerApiUrl() + "/me");

            JSONObject myUser = new JSONObject(jsonResponse);
            UserObject myTempUser = new UserObject(myUser);
            setCurrentUser(myTempUser);

            Log.e(TAG, "My user ID: " + myTempUser.id);
            Log.e(TAG, "My user name: " + myTempUser.fname);
            Log.e(TAG, "My user img URL: " + myTempUser.userImage);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

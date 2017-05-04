package com.se491.app.two2er.GetUsers;

import android.util.Log;

import com.se491.app.two2er.OkHttpClientFactory;
import com.se491.app.two2er.UserObject;
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

public class RefreshUsersBySubjectFilter extends GetUsers {
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

package com.se491.app.two2er.GetUsers;

import android.util.Log;

import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.SideMenuActivity;
import com.se491.app.two2er.Utilities.ServerApiUtilities;

import java.util.HashMap;

/**
 * Created by pazra on 5/3/2017.
 */

public class RefreshUsersBySubjectFilter extends RefreshStrategyBase {
    private String filterValue = "";
    private SideMenuActivity seActivity;

    public RefreshUsersBySubjectFilter(SideMenuActivity myActivity, String filter) {
        seActivity = myActivity;
        filterValue = filter;
    }

    // TODO get the correct URL
    private String getURL() {
        return ServerApiUtilities.GetServerApiUrl() + String.format("users/findWithin/milesLonLat/%1$.4f/%2$.4f/%3$.4f", filterValue);
    }

    @Override
    public void run() {
        Log.i(TAG, "Running refresh users by subject filter");
        HashMap<String, UserObject> currentUsers = seActivity.getTempRecUsers();
        for(UserObject u : currentUsers.values()) {
            if (u.Tutor.Subjects.contains(filterValue.toLowerCase())) {
                tempUsersList.put(u.id, u);
            }
        }
    }

//    @Override
//    public void run() {
//        Log.i(TAG, "Running refresh users by subject filter");
//        Request request = new Request.Builder()
//                .url(getURL())
//                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
//                .get()
//                .build();
//        try {
//            Response response = OkHttpClientFactory.Create().newCall(request).execute();
//
//            String jsonResponse = response.body().string();
//            Log.e("Inside doInBackGround", "URL used in GetUsers(): " + getURL());
//
//            JSONArray users = new JSONArray(jsonResponse);
//            for (int i = 0; i < users.length(); i++) {
//                UserObject user = new UserObject(users.getJSONObject(i));
//                tempUsersList.put(user.id, user);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
}

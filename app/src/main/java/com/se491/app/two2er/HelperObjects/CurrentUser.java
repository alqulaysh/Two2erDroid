package com.se491.app.two2er.HelperObjects;

import android.util.Log;
import android.widget.ImageView;

import com.se491.app.two2er.Activities.UserProfile.DownloadImageTask;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONObject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pazra on 4/30/2017.
 */

public class CurrentUser {
    private static UserObject currentUser;
    private static String TAG = "CurrentUser";
    private static boolean IsSuccessOnRefresh = false;

    private CurrentUser() {
    }

    public static synchronized UserObject getCurrentUser() {
        if (currentUser == null)
            currentUser = new UserObject();

        return currentUser;
    }

    public static synchronized void setCurrentUser(UserObject obj) {
        currentUser = obj;
    }

    public static synchronized boolean IsReady() { return IsSuccessOnRefresh; }

    private static String getURL() {
        return ServerApiUtilities.GetServerApiUrl() +
            ServerApiUtilities.SERVER_API_URL_ROUTE_USERS +
            ServerApiUtilities.SERVER_API_URL_ROUTE_USERS_ME;
    }

    //This method will update the provided image view with the Current Users userImage:
    public static void updateProfilePics(ImageView profileImageView){
        new DownloadImageTask(profileImageView)
                .execute(CurrentUser.getCurrentUser().userImage);
    }

    public static void Init() {
        getCurrentUser();
        Refresh();
    }

    public static void Refresh() {
        try {

            Thread t = new Thread(new Runnable() {
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

                        Log.i(TAG, "URL used in CurrentUser: " + getURL());
                        Log.i(TAG, "Response:\n" + jsonResponse);

                        JSONObject myUser = new JSONObject(jsonResponse);
                        UserObject myTempUser = new UserObject(myUser);
                        myTempUser.setCountOfBookings();
                        setCurrentUser(myTempUser);

                        Log.i(TAG, "My user ID: " + myTempUser.id);
                        Log.i(TAG, "My user name: " + myTempUser.fname);
                        Log.i(TAG, "My user img URL: " + myTempUser.userImage);

                        IsSuccessOnRefresh = true;
                    }
                    catch (Exception ex) {
                        Log.e(TAG, ex.toString());
                        ex.printStackTrace();

                        IsSuccessOnRefresh = false;
                    }
                }
            });
            t.start();
            t.join();

            Log.i(TAG, String.format("CurrentUser long: %f lat: %f", currentUser.dLong, currentUser.dLat));
            Log.i(TAG, String.format("CurrentUser long: %f lat: %f", currentUser.dLong, currentUser.dLat));
        }
        catch(Exception ex) {
            Log.e(TAG, ex.toString());
            ex.printStackTrace();
        }

    }
}


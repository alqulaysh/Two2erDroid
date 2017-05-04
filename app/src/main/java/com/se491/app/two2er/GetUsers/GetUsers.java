/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.GetUsers;

import android.util.Log;

import com.se491.app.two2er.OkHttpClientFactory;
import com.se491.app.two2er.UserObject;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Request;
import okhttp3.Response;


public class GetUsers extends Thread {
    protected HashMap<String, UserObject> tempUsersList = new HashMap<String, UserObject>();

    public GetUsers() { }

    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }
}




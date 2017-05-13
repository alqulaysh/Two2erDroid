package com.se491.app.two2er.GetUsers;

import com.se491.app.two2er.HelperObjects.UserObject;

import java.util.HashMap;

/**
 * Created by pazra on 5/7/2017.
 */

public abstract class RefreshStrategyBase implements Runnable {
    protected String TAG = "RefreshStrategy";

    protected HashMap<String, UserObject> tempUsersList = new HashMap<String, UserObject>();
    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }
}

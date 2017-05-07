/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.GetUsers;

import com.se491.app.two2er.HelperObjects.UserObject;

import java.util.HashMap;


public class GetUsers extends Thread {
    protected HashMap<String, UserObject> tempUsersList = new HashMap<String, UserObject>();

    public GetUsers() { }

    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }
}




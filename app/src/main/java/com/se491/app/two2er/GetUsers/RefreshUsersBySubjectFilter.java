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
    private HashMap<String, UserObject> userList;

    public RefreshUsersBySubjectFilter(HashMap<String, UserObject> users, String filter) {
        userList = users;
        filterValue = filter;
    }

    @Override
    public void run() {
        Log.i(TAG, "Running refresh users by subject filter");
        for(UserObject u : userList.values()) {
            for (String sub : u.Tutor.Subjects) {
                if (sub.toLowerCase().equals(filterValue.toLowerCase())) {
                    tempUsersList.put(u.id, u);
                }
            }
        }
    }
}

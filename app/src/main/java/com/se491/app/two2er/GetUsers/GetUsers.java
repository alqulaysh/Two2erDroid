/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.GetUsers;

import com.se491.app.two2er.HelperObjects.UserObject;

import java.util.HashMap;


public class GetUsers extends Thread {
    private HashMap<String, UserObject> tempUsersList = new HashMap<>();
    public HashMap<String, UserObject> getUsersList() { return tempUsersList; }

    private RefreshStrategyBase refreshStrategy = new DistanceRefreshStrategy();

    public GetUsers(RefreshStrategyBase refreshStrat) {
        refreshStrategy = refreshStrat;
    }

    @Override
    public void run() {
        refreshStrategy.run();
        tempUsersList = refreshStrategy.getUsersList();
    }
}




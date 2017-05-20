package com.se491.app.two2er;

import android.util.Log;

import com.se491.app.two2er.Enums.eUserMode;
import com.se491.app.two2er.HelperObjects.CurrentUser;

/**
 * Created by pazra on 4/23/2017.
 */

class SessionState {
    private static String TAG = "SessionState";

    private static eUserMode _userMode = eUserMode.STUDENT;
    public static synchronized eUserMode getUserMode() {
        return _userMode;
    }
    public static synchronized void setUserMode(eUserMode val) { _userMode = val; }
    public static synchronized void toggleUserMode() {
        if (getUserMode() == eUserMode.STUDENT) {
            setUserMode(eUserMode.TUTOR);
            Log.i(TAG, "Session set to Tutor. " + CurrentUser.getCurrentUser().userGroups);
        }
        else if (getUserMode() == eUserMode.TUTOR) {
            setUserMode(eUserMode.STUDENT);
            Log.i(TAG, "Session set to Student.");
        }
    }
}

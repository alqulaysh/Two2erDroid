package com.se491.app.two2er;

import android.util.Log;

import com.se491.app.two2er.Enums.eUserMode;

/**
 * Created by pazra on 4/23/2017.
 */

public class SessionState {
    private static String Tag = "SessionState";

    private static eUserMode _userMode = eUserMode.STUDENT;

    public static synchronized eUserMode UserMode() {
        return _userMode;
    }
    public static synchronized void setUserMode(eUserMode val) {
        Log.i(Tag, "User Mode updated to: " + _userMode);
        _userMode = val;
    }
    public static synchronized void toggleUserMode() {
        if (UserMode() == eUserMode.STUDENT) {
            setUserMode(eUserMode.TUTOR);
        }
        else if (UserMode() == eUserMode.TUTOR) {
            setUserMode(eUserMode.STUDENT);
        }
    }
}

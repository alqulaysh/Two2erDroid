package com.se491.app.two2er;

import com.se491.app.two2er.Enums.eUserMode;

/**
 * Created by pazra on 4/23/2017.
 */

class SessionState {
    private static String Tag = "SessionState";

    private static eUserMode _userMode = eUserMode.STUDENT;
    public static synchronized eUserMode getUserMode() {
        return _userMode;
    }
    public static synchronized void setUserMode(eUserMode val) { _userMode = val; }
    public static synchronized void toggleUserMode() {
        if (getUserMode() == eUserMode.STUDENT) {
            setUserMode(eUserMode.TUTOR);
        }
        else if (getUserMode() == eUserMode.TUTOR) {
            setUserMode(eUserMode.STUDENT);
        }
    }
}

package com.se491.app.two2er;

/**
 * Created by pazra on 4/23/2017.
 */

public class SessionState {
    private static eUserMode _userMode = eUserMode.STUDENT;
    public static synchronized eUserMode UserMode() {
        return _userMode;
    }
    public static synchronized void setUserMode(eUserMode val) {
        _userMode = val;
    }
}

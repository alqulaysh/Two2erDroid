package com.se491.app.two2er;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by ajscilingo on 3/4/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
public MyFirebaseInstanceIDService(){

}
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyFireBaseToken", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);

    }
}

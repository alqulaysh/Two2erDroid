package com.se491.app.two2er.Activities;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.se491.app.two2er.R;

public class AdditionalProfileActivity extends AppCompatActivity implements
        DialogInterface.OnDismissListener
        {
    private static String TAG = "UserProfileActivity";
    Fragment myThis;
    ImageView userProfileImage;
    EditText userfName;
    EditText userlName;
    EditText userEmail;
    EditText userUni;
    String userImgURL;
    View v;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_profile);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG, "Pressed back button: " );
                finish();
            }
        });


    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


}

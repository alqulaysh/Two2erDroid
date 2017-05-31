package com.se491.app.two2er.Activities.Settings;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.se491.app.two2er.Activities.SchduleActivity;
import com.se491.app.two2er.Activities.StartPage.LoginActivity;
import com.se491.app.two2er.Activities.UserProfile.PWDDialogFragment;
import com.se491.app.two2er.Activities.UserProfile.UserProfileActivity;
import com.se491.app.two2er.R;
import com.se491.app.two2er.SideMenuActivity;
import com.stormpath.sdk.Stormpath;

/**
 * Created by SINNER on 5/20/17.
 */

public class SettingsActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG, "Pressed back button: " );
                finish();
            }
        });




        final GridLayout textview1 = (GridLayout) findViewById(R.id.logoooot);



        textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stormpath.logout();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                finish();
            }
        });


        final GridLayout textview2 = (GridLayout) findViewById(R.id.SwitchtoUserProfile);



        textview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, UserProfileActivity.class));
            }
        });


    }



}

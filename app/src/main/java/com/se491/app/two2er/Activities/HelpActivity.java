package com.se491.app.two2er.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.se491.app.two2er.CurrentUser;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.R;

/**
 * Created by SINNER on 5/3/17.
 */

public class HelpActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG, "Pressed back button: " );
                finish();
            }
        });

        UserObject currentUser = CurrentUser.getCurrentUser();

        String [] UserProfileInfo = currentUser.getListArray();
        ListAdapter LA = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,UserProfileInfo);

        ListView LAA = (ListView) findViewById(R.id.listviewProfile);
        //LAA.setAdapter(LA);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LAA.setAdapter(new UserProfileListAdapter(inflater));

        if (currentUser.Tutor != null) {
            ((TextView)findViewById(R.id.TutorRate)).setText(String.valueOf(CurrentUser.getCurrentUser().Tutor.Rating));
        }

        ((TextView)findViewById(R.id.TutorExperience)).setText(String.valueOf(CurrentUser.getCurrentUser().BookingsCount));
    }
}

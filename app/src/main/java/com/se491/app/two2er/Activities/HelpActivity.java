package com.se491.app.two2er.Activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

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


//        String [] UserProfileInfo={ "First Name", "Last Name", "Email", "University", "Subject"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.activity_list_item, UserProfileInfo);
//        getListView().setAdapter(adapter);




    }
}

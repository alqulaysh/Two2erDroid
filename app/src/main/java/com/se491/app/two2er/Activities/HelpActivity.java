package com.se491.app.two2er.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.se491.app.two2er.CurrentUser;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.PostUpdates;
import com.se491.app.two2er.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by SINNER on 5/3/17.
 */

public class HelpActivity extends AppCompatActivity {
    private UserProfileListAdapter LA;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Button updateButton = (Button) findViewById(R.id.update_btn);

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

        ListView LAA = (ListView) findViewById(R.id.listviewProfile);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LA = new UserProfileListAdapter(inflater, this);

        LAA.setAdapter(LA);

        if (currentUser.Tutor != null) {
            ((TextView)findViewById(R.id.TutorRate)).setText(String.valueOf(CurrentUser.getCurrentUser().Tutor.Rating));
        }

        ((TextView)findViewById(R.id.TutorExperience)).setText(String.valueOf(CurrentUser.getCurrentUser().BookingsCount));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateMyUser();
            }
        });

    }

    public void upDateMyUser() {
        try {
            new PostUpdates(CurrentUser.getCurrentUser()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

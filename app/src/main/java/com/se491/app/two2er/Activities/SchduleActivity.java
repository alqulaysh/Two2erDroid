package com.se491.app.two2er.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.se491.app.two2er.R;

/**
 * Created by SINNER on 5/16/17.
 */


    /**
     * Created by SINNER on 5/3/17.
     */

    public class SchduleActivity extends AppCompatActivity {


        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_schdule);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar9);
            setSupportActionBar(toolbar);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e(TAG, "Pressed back button: " );
                    finish();
                }
            });



    }



}

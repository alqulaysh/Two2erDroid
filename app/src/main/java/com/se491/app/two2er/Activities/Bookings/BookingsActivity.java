package com.se491.app.two2er.Activities.Bookings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.se491.app.two2er.R;

public class BookingsActivity extends AppCompatActivity{

    private ListView mAPListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        //Get our ListView
        mAPListView = (ListView) findViewById(R.id.list_ap);

        //Get our toolbar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.bookingsToolBar);
        setSupportActionBar(toolbar);

        //If button is clicked on ToolBar finish the activity and go back to SideMenuActivity:
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(mAPListView != null) {
            updateUI();
        }
        //updateUI();
    }

    //This function just updates our UI whenever there is a change:
    private void updateUI() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAPListView.setAdapter(new BookingListAdapterNew(inflater));
    }

}

package com.se491.app.two2er.Activities.Bookings;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.se491.app.two2er.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by Nithun on 4/5/2017.
 */

public class CreateBooking  extends DialogFragment {

    private DatePicker dpResult;
    private TimePicker tpResult;
    private Button createBookingBtn;
    TextView dateDisplay;
    TextView timeDisplay;

    public CreateBooking(){
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_booking, container);
        // Do something else
        return rootView;
    }
    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        dpResult = (DatePicker) view.findViewById(R.bookingId.bookingDatePicker);
//        tpResult = (TimePicker) view.findViewById(R.bookingId.boookingTimePicker);
        createBookingBtn = (Button) view.findViewById(R.id.createBookingBtn);
//        dateDisplay = (TextView) view.findViewById(R.bookingId.dateText);
//        timeDisplay = (TextView) view.findViewById(R.bookingId.timeText);
//
//        dateDisplay.setText(getCurrentDate());
//        //timeDisplay.setText(getCurrentTime());
        Bundle mArgs = getArguments();
        final String selectedUserId = mArgs.getString("uID");
//        timeDisplay.setText(myValue);



        createBookingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                dateDisplay.setText(getCurrentDate());
//                timeDisplay.setText(getCurrentTime());
                try {
                    xCreateMyBookingRequest(selectedUserId);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


    }

    public void xCreateMyBookingRequest(String selectedUserId) throws ExecutionException, InterruptedException {
        int randomNum = 1 + (int)(Math.random() * ((50 - 1) + 1));
        new PostToBookings(selectedUserId, "2017-06-04T16:" + randomNum +":00.000Z").execute().get();
    }

    private String getCurrentDate() {
        StringBuilder builder=new StringBuilder();
        builder.append("Selected Date: ");
        builder.append((dpResult.getMonth() + 1)+"/");//month is 0 based
        builder.append(dpResult.getDayOfMonth()+"/");
        builder.append(dpResult.getYear());
        return builder.toString();
    }

    private String getCurrentTime() {
        StringBuilder builder=new StringBuilder();
        builder.append("Selected Time: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.append((tpResult.getHour())+":");
            builder.append(tpResult.getMinute());
        }
        return builder.toString();
    }


}

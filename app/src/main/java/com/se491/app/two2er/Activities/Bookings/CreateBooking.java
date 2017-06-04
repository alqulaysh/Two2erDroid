package com.se491.app.two2er.Activities.Bookings;


import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.se491.app.two2er.HelperObjects.TimeBlockObject;
import com.se491.app.two2er.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
/**
 * Created by Nithun on 4/5/2017.
 */

public class CreateBooking extends DialogFragment implements MonthLoader.MonthChangeListener, WeekView.EventClickListener {

    private DatePicker dpResult;
    private TimePicker tpResult;
    private Button createBookingBtn;
    TextView dateDisplay;
    TextView timeDisplay;
    private static GetTutorSchedule tutorSchedule = null;
    private static String TAG = "CreateBooking";
    private String selectedUserID = null;
    // Used for posting to API endpoint
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static View rootView = null;
    private static WeekView mWeekView = null;
    public CreateBooking(){
        // Empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_booking, container);
        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        // Do something else
        return rootView;
    }
    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        dpResult = (DatePicker) view.findViewById(R.bookingId.bookingDatePicker);
//        tpResult = (TimePicker) view.findViewById(R.bookingId.boookingTimePicker);
//        createBookingBtn = (Button) view.findViewById(R.id.createBookingBtn);
//        dateDisplay = (TextView) view.findViewById(R.bookingId.dateText);
//        timeDisplay = (TextView) view.findViewById(R.bookingId.timeText);
//
//        dateDisplay.setText(getCurrentDate());
//        //timeDisplay.setText(getCurrentTime());
        Bundle mArgs = getArguments();
        selectedUserID = mArgs.getString("uID");
//        timeDisplay.setText(myValue);



/*        createBookingBtn.setOnClickListener(new View.OnClickListener() {

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
*/

        // get the tutor's schedule from timekit
        tutorSchedule = new GetTutorSchedule(selectedUserID);
        //spawn thread
        tutorSchedule.start();

        // wait until thread finishes
        try{
            tutorSchedule.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth){
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        int currentMonth = (Calendar.getInstance().get(Calendar.MONTH) + 1);

        if(newMonth == currentMonth){
            Log.i(TAG, "newMonth: " + newMonth + " currentMonth: " + currentMonth);
            int eventID = 0;

            // create references for StartTime, EndTime and WeekViewEvent
            Calendar startTime = null;
            Calendar endTime = null;
            WeekViewEvent event = null;

            for (TimeBlockObject timeblock : tutorSchedule.getTimeBlockList()) {
                startTime = Calendar.getInstance();
                endTime = (Calendar) startTime.clone();
                startTime.setTime(timeblock.getStart());
                endTime.setTime(timeblock.getEnd());
                event = new WeekViewEvent(eventID, getEventTitle(startTime, true), startTime, endTime);
                // increment eventID for next event
                event.setColor(getResources().getColor(R.color.event_color_03));
                events.add(event);
                Log.i(TAG, "ID: " + event.getId() + " StartTime: " + event.getStartTime().get(Calendar.YEAR) + "-" + event.getStartTime().get(Calendar.MONTH) + "-" + event.getStartTime().get(Calendar.DAY_OF_MONTH) + "T" + event.getStartTime().get(Calendar.HOUR) + ":" + event.getStartTime().get(Calendar.MINUTE) + " EndTime: " + event.getEndTime().get(Calendar.YEAR) + "-" + event.getEndTime().get(Calendar.MONTH) + "-" + event.getEndTime().get(Calendar.DAY_OF_MONTH) + "T" + event.getEndTime().get(Calendar.HOUR) + ":" + event.getEndTime().get(Calendar.MINUTE));
                eventID++;
            }
        }

        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent weekViewEvent, RectF rectF) {
        try {
            xCreateMyBookingRequest(selectedUserID, weekViewEvent.getStartTime());
            weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
            weekViewEvent.setName("Booking Pending...");
            // update the weekViewEvent so that the new color is reflected
            // when clicked on.
            mWeekView.invalidate();
        }
        catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    private String getEventTitle(Calendar time, boolean open) {

        if(open)
            return "Booking Available";
        else
            return "Booking Unavailable";
    }

    public void xCreateMyBookingRequest(String selectedUserId, Calendar date) throws ExecutionException, InterruptedException {
        //int randomNum = 1 + (int)(Math.random() * ((50 - 1) + 1));
        //new PostToBookings(selectedUserId, "2017-06-04T16:" + randomNum + ":00.000Z").execute().get();
        String myBookingDate = dateFormat.format(date.getTime());
        new PostToBookings(selectedUserID, myBookingDate).execute().get();

        //if(tutorSchedule.getState()
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

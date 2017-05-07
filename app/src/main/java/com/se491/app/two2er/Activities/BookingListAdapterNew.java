package com.se491.app.two2er.Activities;

/**
 * Created by eoliv on 5/3/2017.
 */

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.se491.app.two2er.CurrentUser;
import com.se491.app.two2er.Fragments.Bookings.PostToBookings;
import com.se491.app.two2er.GetBookingsNew;
import com.se491.app.two2er.HelperObjects.BookingObject;
import com.se491.app.two2er.R;
import com.se491.app.two2er.Utilities.ServerApiUtilities;

import java.util.ArrayList;


/**
 * Created by eoliv on 3/3/2017.
 */

public class BookingListAdapterNew extends BaseAdapter {


    private LayoutInflater inflater;
    private ArrayList<BookingObject> apList = getBookings();
    public static String TAG = "BookingListAdapterNew";

    //Constructor:
    public BookingListAdapterNew(LayoutInflater inflater){this.inflater = inflater;}

    //Get the objects from the database:
    public ArrayList<BookingObject> getBookings() {

        final GetBookingsNew getBookings = new GetBookingsNew();

        Log.i(TAG, "Starting thread to GetBookings");
        getBookings.start();

        try {
            getBookings.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "Returning a list of size: " + getBookings.getBookingList().size());
        return getBookings.getBookingList();
    }
    @Override
    public int getCount() {
        return apList.size();
    }

    @Override
    public Object getItem(int i) {
        return apList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            if (inflater != null)
                row = inflater.inflate(R.layout.notification_list, parent, false);
        }
        //Get our views for the main list view:
        TextView msgDesc = (TextView) row.findViewById(R.id.ap_title);
        TextView mDate = (TextView) row.findViewById(R.id.MeetingDate);

        //Set our views for the List view:
        String tutor_name = apList.get(position).tutor_name;
        String student_name = apList.get(position).student_name;
        String meetingDate = apList.get(position).MeetingDate;
        String currStatus = apList.get(position).status;
        final String bookingId = apList.get(position).bookingId;
        final String timekit_booking_id = apList.get(position).timekit_booking_id;
        Button acceptButton = (Button) row.findViewById(R.id.button_accept);
        final Button declineButton = (Button) row.findViewById(R.id.button_decline);

        row.setBackgroundColor(Color.YELLOW);

        if(currStatus.equals("confirmed")){
            row.setBackgroundColor(Color.GREEN);
            declineButton.setText("Cancel");
        }

        if(currStatus.equals("declined")){
            row.setBackgroundColor(Color.RED);
        }

        if(currStatus.equals("completed")){
            row.setBackgroundColor(Color.GREEN);
            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.GONE);
        }

        //set as the tag the position parameter
        acceptButton.setTag(new Integer(position));
        declineButton.setTag(new Integer(position));

        final View finalRow = row;
        //Handle the case where the accept button is clicked:
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Do the stuff you want for the case when the row TextView is clicked
                // you may want to set as the tag for the TextView the position paremeter of the `getView` method and then retrieve it here
                Integer realPosition = (Integer) v.getTag();
                System.out.println("This is my timekit_booking_id for this row: " + timekit_booking_id);
                new PostToBookings(finalRow, timekit_booking_id, ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING_CONFIRM);
                finalRow.setBackgroundColor(Color.GREEN);
                //Set the decline button to Cancel:
                declineButton.setText("Cancel");
                // using realPosition , now you know the row where this TextView was clicked
            }
        });

        //Handle the case where the decline button is clicked:
        declineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Do the stuff you want for the case when the row TextView is clicked
                // you may want to set as the tag for the TextView the position paremeter of the `getView` method and then retrieve it here
                Integer realPosition = (Integer) v.getTag();
                if(declineButton.getText().toString() == "Decline"){
                    new PostToBookings(finalRow, timekit_booking_id, ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING_DELCINE);
                    finalRow.setBackgroundColor(Color.RED);
                }
                else {
                        new PostToBookings(finalRow, timekit_booking_id, ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING_CANCLE);
                        finalRow.setBackgroundColor(Color.RED);
                }
                // using realPosition , now you know the row where this TextView was clicked
            }
        });


        //Get the Drawable resource assoicated with the type:
        String[] date = meetingDate.split("T");
        //Set our views:
        //Show a student the tutors name and show the tutor the students name:
        System.out.println("CurrentUser.getCurrentUser().userMode: " + CurrentUser.getCurrentUser().userMode);

        if(CurrentUser.getCurrentUser().userMode.equals("Student")){
            msgDesc.setText("Session with: " + tutor_name);
        }
        else{
            msgDesc.setText("Session with: " + student_name);
        }
        mDate.setText(date[0].trim());
        return row;
    }
}


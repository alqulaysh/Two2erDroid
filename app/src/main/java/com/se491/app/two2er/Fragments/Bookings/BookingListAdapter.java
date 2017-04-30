package com.se491.app.two2er.Fragments.Bookings;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.se491.app.two2er.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by eoliv on 3/3/2017.
 */

class BookingListAdapter extends BaseAdapter {

    public interface ViewClickListener {

        void onViewClick(View view);
    }

    private ViewClickListener listener;

    private LayoutInflater inflater;
    private ArrayList<BookingObject> apList = getBookings();

    public BookingListAdapter(LayoutInflater v, ViewClickListener viewClickListener) throws ExecutionException, InterruptedException {
        inflater = v;
    }

    //Get the objects from the database:
    public ArrayList<BookingObject> getBookings() throws ExecutionException, InterruptedException {
        ArrayList<BookingObject> taskList;
        taskList = new GetBookings().execute().get();

        return taskList;
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
        String name = apList.get(position).tutor_name;
        String meetingDate = apList.get(position).MeetingDate;
        String currStatus = apList.get(position).status;
        final String bookingId = apList.get(position).bookingId;
        Button acceptButton = (Button) row.findViewById(R.id.button_accept);
        Button declineButton = (Button) row.findViewById(R.id.button_decline);

        row.setBackgroundColor(Color.YELLOW);

        if(currStatus.equals("confirmed")){
            row.setBackgroundColor(Color.GREEN);
        }

        if(currStatus.equals("declined")){
            row.setBackgroundColor(Color.RED);
        }

        //set as the tag the position parameter
        acceptButton.setTag(new Integer(position));
        declineButton.setTag(new Integer(position));
        System.out.println("Current Status: " + currStatus + " booking " + bookingId);


        final View finalRow = row;
        //Handle the case where the accept button is clicked:
        acceptButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Do the stuff you want for the case when the row TextView is clicked
                // you may want to set as the tag for the TextView the position paremeter of the `getView` method and then retrieve it here
                Integer realPosition = (Integer) v.getTag();
                System.out.println("This is my booking ID for this row: " + bookingId);
                new PostToBookings(finalRow, bookingId, "accept");
                finalRow.setBackgroundColor(Color.GREEN);
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
                new PostToBookings(finalRow, bookingId, "reject");
                finalRow.setBackgroundColor(Color.RED);
                // using realPosition , now you know the row where this TextView was clicked
            }
        });


        //Get the Drawable resource assoicated with the type:
        String[] date = meetingDate.split("T");
        //Set our views:
        msgDesc.setText("Session with: " + name);
        mDate.setText(date[0].trim());
        return row;
    }
}

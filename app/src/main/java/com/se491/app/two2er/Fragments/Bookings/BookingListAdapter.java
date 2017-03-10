package com.se491.app.two2er.Fragments.Bookings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.se491.app.two2er.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by eoliv on 3/3/2017.
 */

class BookingListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<BookingObject> apList = getBookings();

    public BookingListAdapter(LayoutInflater v) throws ExecutionException, InterruptedException {
        inflater = v;
    }

    //Get the objects from the database:
    public ArrayList<BookingObject> getBookings() throws ExecutionException, InterruptedException {
        ArrayList<BookingObject> taskList = new ArrayList<BookingObject>();
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
            if (inflater == null)
                System.out.print("THIS IS MY +++++++++++++++++++++++++++++++++++++++++++++");
            //inflater = (LayoutInflater) ((SideMenuActivity).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.notification_list, parent, false);
        }

        //Get our views for the main list view:
        TextView msgDesc = (TextView) row.findViewById(R.id.ap_title);
        TextView mDate = (TextView) row.findViewById(R.id.MeetingDate);

        //Set our views for the List view:
        String name = apList.get(position).studentUserId;
        String meetingDate = apList.get(position).MeetingDate;

        //Get the Drawable resource assoicated with the type:

        //Set our views:
        msgDesc.setText(meetingDate + " - " + name);
        mDate.setText("");
        return row;
    }
}

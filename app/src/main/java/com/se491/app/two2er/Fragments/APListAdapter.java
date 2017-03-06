package com.se491.app.two2er.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.se491.app.two2er.R;

import java.util.ArrayList;

/**
 * Created by eoliv on 3/3/2017.
 */

class APListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> apList = getDBObj();

    public APListAdapter(LayoutInflater v) {
        inflater = v;
    }

    //Get the objects from the database:
    public ArrayList getDBObj(){
        ArrayList<String> taskList = new ArrayList<>();
        taskList.add("Tom session...");
        taskList.add("Jon session...");
        taskList.add("Ted session...");
        taskList.add("Dan session...");
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
        TextView movieDesc = (TextView) row.findViewById(R.id.ap_title);

        //Set our views for the List view:
        String name = apList.get(position);

        //Get the Drawable resource assoicated with the type:

        //Set our views:
        movieDesc.setText(name);
        return row;
    }
}

package com.se491.app.two2er.Activities;

/**
 * Created by eoliv on 5/3/2017.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.se491.app.two2er.CurrentUser;
import com.se491.app.two2er.HelperObjects.FieldPair;
import com.se491.app.two2er.R;

import java.util.List;


/**
 * Created by eoliv on 3/3/2017.
 */

public class UserProfileListAdapter extends BaseAdapter {


    private LayoutInflater inflater;
    private List<FieldPair> fieldPairList = getUserFields();
    public static String TAG = "UserProfileListAdapter";

    //Constructor:
    public UserProfileListAdapter(LayoutInflater inflater){this.inflater = inflater;}

    //Get the objects from the database:
    public List<FieldPair> getUserFields() {
        return CurrentUser.getCurrentUser().getListFieldPair();
    }
    @Override
    public int getCount() {
        return fieldPairList.size();
    }

    @Override
    public Object getItem(int i) {
        return fieldPairList.get(i);
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
                row = inflater.inflate(R.layout.userprofile_list, parent, false);
        }
        //Get our views for the main list view:
        TextView tvFieldTitle = (TextView) row.findViewById(R.id.FieldTitle);
        TextView tvFieldValue = (TextView) row.findViewById(R.id.FieldValue);

        //Show a student the tutors name and show the Tutor the students name:
        tvFieldTitle.setText(fieldPairList.get(position).FieldTitle);
        tvFieldValue.setText(fieldPairList.get(position).FieldValue);
        return row;
    }
}


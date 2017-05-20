package com.se491.app.two2er.Activities.UserProfile;

/**
 * Created by eoliv on 5/3/2017.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.FieldPair;
import com.se491.app.two2er.R;

import java.util.List;


/**
 * Created by eoliv on 3/3/2017.
 */

public class UserProfileListAdapter extends BaseAdapter implements DialogInterface.OnDismissListener {


    private LayoutInflater inflater;
    private Context myContext;
    private List<FieldPair> fieldPairList = getUserFields();
    public static String TAG = "UserProfileListAdapter";

    //Constructor:
    public UserProfileListAdapter(LayoutInflater inflater, Context applicationContext){this.inflater = inflater; this.myContext = applicationContext;}

    //Get the objects from the database:
    private List<FieldPair> getUserFields() {
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
    public View getView (final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        final int row_position = position;
        if (convertView == null) {
            if (inflater != null)
                row = inflater.inflate(R.layout.userprofile_list, parent, false);
        }
        //Get our views for the main list view:
        TextView tvFieldTitle = (TextView) row.findViewById(R.id.FieldTitle);
        TextView tvFieldValue = (TextView) row.findViewById(R.id.FieldValue);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View alertLayout = inflater.inflate(R.layout.change_profile_value, null, false);

                //Create our info dialog and display it:
                final AlertDialog.Builder db = new AlertDialog.Builder(myContext);
                final String orgFieldTitle = fieldPairList.get(row_position).FieldTitle;
                final String orgFieldValue = fieldPairList.get(row_position).FieldValue;
                final EditText value_change = (EditText) alertLayout.findViewById(R.id.changed_value);
                db.setView(alertLayout);
                value_change.setText(orgFieldValue);
                db.setTitle("Change " + orgFieldTitle);


                db.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        notifyDataSetChanged();
                    }
                });

                db.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!orgFieldValue.equals(value_change.getText().toString())) {
                            String currentValue = value_change.getText().toString();
                            fieldPairList.get(row_position).FieldValue = currentValue;

                            //Update our user object:
                            switch (orgFieldTitle.trim()) {
                                case "First Name:": CurrentUser.getCurrentUser().fname = currentValue;
                                    break;
                                case "Last Name:": CurrentUser.getCurrentUser().lname = currentValue;
                                    break;
                                case "Email:":  CurrentUser.getCurrentUser().email = currentValue;
                                    break;
                                case "Age:":  CurrentUser.getCurrentUser().age = currentValue;
                                    break;
                                case "School:":  CurrentUser.getCurrentUser().Education.School = currentValue;
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                });

                db.show();
            }
        });

        //Show a student the tutors name and show the Tutor the students name:
        tvFieldTitle.setText(fieldPairList.get(position).FieldTitle);
        tvFieldValue.setText(fieldPairList.get(position).FieldValue);
        return row;
    }

    public void Test() {
        notifyDataSetChanged();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        notifyDataSetChanged();
    }
}


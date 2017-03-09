package com.se491.app.two2er.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.se491.app.two2er.R;

/**
 * Created by eoliv on 3/3/2017.
 */

public class NotificationFragment extends Fragment {
    private ListView mAPListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        mAPListView = (ListView) v.findViewById(R.id.list_ap);
        mAPListView.setAdapter(new APListAdapter(inflater));

        return v;
    }

}

package com.se491.app.two2er.Fragments.Bookings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.se491.app.two2er.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by eoliv on 3/3/2017.
 */

public class BookingsFragment extends Fragment {
    private ListView mAPListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        mAPListView = (ListView) v.findViewById(R.id.list_ap);

        try {
            mAPListView.setAdapter(new BookingListAdapter(inflater, new BookingListAdapter.ViewClickListener() {
                @Override
                public void onViewClick(View view) {
                    // do what you want with view

                }
            })
            );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return v;
    }

}

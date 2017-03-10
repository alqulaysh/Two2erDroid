package com.se491.app.two2er.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.se491.app.two2er.GetUsers;
import com.se491.app.two2er.PostUpdates;
import com.se491.app.two2er.R;
import com.se491.app.two2er.SideMenuActivity;

import java.util.concurrent.ExecutionException;

public class UserProfileFragment extends Fragment {

    EditText userName;
    EditText userUni;
    private String myName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //R.nameField
        View v = inflater.inflate(R.layout.fragment_userprofile, container, false);
        userName = (EditText) v.findViewById(R.id.nameField);
        userUni = (EditText) v.findViewById(R.id.univField);
        String userNameVal =  getArguments().getString("fname");
        String userEmail =  getArguments().getString("email");
        userName.setText(userNameVal);
        userUni.setText(userEmail);

        try {
            new GetUsers(((SideMenuActivity) getActivity())).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Button updateButton = (Button) v.findViewById(R.id.update_btn);
        Button changePWDbutton = (Button) v.findViewById(R.id.changepwd_btn);

        changePWDbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = getFragmentManager();
                PWDDialogFragment dialogFragment = new PWDDialogFragment();
                dialogFragment.show(fm, "Change User Password!");

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Get my current username thats entered in.
                myName = userName.getText().toString();
                try {
                    upDateMyUser();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Wait until we get our User Info before continuing:
                try {
                    Integer result = new GetUsers(((SideMenuActivity) getActivity())).execute().get();
                    while(result != 1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    public void upDateMyUser() throws ExecutionException, InterruptedException {
        new PostUpdates(myName).execute().get();
    }
}

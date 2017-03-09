package com.se491.app.two2er.Fragments;

import android.app.Fragment;
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
    private String myName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //R.nameField
        View v = inflater.inflate(R.layout.fragment_userprofile, container, false);
        userName = (EditText) v.findViewById(R.id.nameField);
        String userNameVal =  getArguments().getString("fname");
        userName.setText(userNameVal);

        Button button = (Button) v.findViewById(R.id.update_btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
                    new GetUsers(((SideMenuActivity) getActivity())).execute().get();
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

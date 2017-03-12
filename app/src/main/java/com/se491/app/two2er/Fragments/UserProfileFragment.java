package com.se491.app.two2er.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.se491.app.two2er.GetUsers;
import com.se491.app.two2er.PostUpdates;
import com.se491.app.two2er.R;
import com.se491.app.two2er.SideMenuActivity;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class UserProfileFragment extends Fragment {

    EditText userfName;
    EditText userlName;
    EditText userEmail;
    EditText userUni;
    private String myName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //R.nameField
        View v = inflater.inflate(R.layout.fragment_userprofile, container, false);
        userfName = (EditText) v.findViewById(R.id.nameField);
        userlName = (EditText) v.findViewById(R.id.lNameField);
        userEmail = (EditText) v.findViewById(R.id.emailField);
        userUni = (EditText) v.findViewById(R.id.univField);
        String userfNameVal =  getArguments().getString("fname");
        String userlNameVal =  getArguments().getString("lname");
        String sUserEmail =  getArguments().getString("email");
        String userImgURL =  getArguments().getString("userImage");
        userfName.setText(userfNameVal);
        userlName.setText(userlNameVal);
        userEmail.setText(sUserEmail );

        Log.e("Inside UserFrag", "userEmail: " + userImgURL);

        if(!userImgURL.isEmpty() || userImgURL == "") {
            try {
                new DownloadImageTask((ImageView) v.findViewById(R.id.userProfileImgV))
                        .execute(userImgURL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
                userfName = (EditText) v.findViewById(R.id.nameField);
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}

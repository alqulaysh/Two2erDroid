package com.se491.app.two2er.Fragments.UserProfile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
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

import com.se491.app.two2er.Fragments.PWDDialogFragment;
import com.se491.app.two2er.R;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class UserProfileFragment extends Fragment implements
        DialogInterface.OnDismissListener,
        ChangeProfileImageDialog.onDialogDismissListener {

    Fragment myThis;
    ImageView userProfileImage;
    EditText userfName;
    EditText userlName;
    EditText userEmail;
    EditText userUni;
    String userImgURL;
    View v;
    private String myName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //R.nameField


        myThis = this;


        v = inflater.inflate(R.layout.fragment_userprofile, container, false);
        userProfileImage = (ImageView) v.findViewById(R.id.userProfileImgV);
        userfName = (EditText) v.findViewById(R.id.nameField);
        userlName = (EditText) v.findViewById(R.id.lNameField);
        userEmail = (EditText) v.findViewById(R.id.emailField);
        userUni = (EditText) v.findViewById(R.id.univField);
        String userfNameVal =  getArguments().getString("fname");
        String userlNameVal =  getArguments().getString("lname");
        String sUserEmail =  getArguments().getString("email");
        userImgURL =  getArguments().getString("userImage");
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

        userProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = getFragmentManager();
                ChangeProfileImageDialog changeImgdialogFragment = new ChangeProfileImageDialog();
                changeImgdialogFragment.setTargetFragment(myThis,1);

                changeImgdialogFragment.show(fm, "UserProfileFragment");

            }
        });

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
//                try {
//                    //Integer result = new GetUsers(((SideMenuActivity) getActivity())).execute().get();
//                    //while(result != 1);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
            }
        });

        return v;
    }



    public void upDateMyUser() throws ExecutionException, InterruptedException {
        //new PostUpdates(myName).execute().get();
    }

    @Override
    public void onDismissDialog(String userImgURL) {
        Log.e("Android : ", "This is at onDismissDialogUserProfile");

        try {
            new DownloadImageTask((ImageView) v.findViewById(R.id.userProfileImgV))
                    .execute(userImgURL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

//        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
//        //Set the sidemenu image to users profile picture:
//        View hView =  navigationView.getHeaderView(0);
//        ImageView nav_user = (ImageView)hView.findViewById(R.id.Nav_imageView);
//
//        if(!userImgURL.isEmpty() || userImgURL == "") {
//            try {
//                new DownloadImageTask(nav_user)
//                        .execute(userImgURL).get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

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

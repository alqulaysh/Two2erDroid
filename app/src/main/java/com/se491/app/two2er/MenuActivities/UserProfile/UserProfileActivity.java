package com.se491.app.two2er.MenuActivities.UserProfile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.se491.app.two2er.CurrentUser;
import com.se491.app.two2er.Fragments.PWDDialogFragment;
import com.se491.app.two2er.Fragments.UserProfile.ChangeProfileImageDialog;
import com.se491.app.two2er.Fragments.UserProfile.UserProfileFragment;
import com.se491.app.two2er.PostUpdates;
import com.se491.app.two2er.R;
import com.se491.app.two2er.SideMenuActivity;
import com.se491.app.two2er.UserObject;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class UserProfileActivity extends AppCompatActivity implements
        DialogInterface.OnDismissListener,
        ChangeProfileImageDialog.onDialogDismissListener {
    private static String TAG = "UserProfileActivity";
    Fragment myThis;
    ImageView userProfileImage;
    EditText userfName;
    EditText userlName;
    EditText userEmail;
    EditText userUni;
    String userImgURL;
    View v;
    private String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_userprofile);

        UserObject myCurrentUser = CurrentUser.getCurrentUser();
        userProfileImage = (ImageView) findViewById(R.id.userProfileImgV);
        userfName = (EditText) findViewById(R.id.nameField);
        userlName = (EditText) findViewById(R.id.lNameField);
        userEmail = (EditText) findViewById(R.id.emailField);
        userUni = (EditText) findViewById(R.id.univField);
        String userfNameVal =  myCurrentUser.fname;
        String userlNameVal =  myCurrentUser.lname;
        String sUserEmail =  myCurrentUser.email;
        userImgURL = myCurrentUser.userImage;

        userfName.setText(userfNameVal);
        userlName.setText(userlNameVal);
        userEmail.setText(sUserEmail);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Pressed back button: " );
                finish();
            }
        });


        Log.e(TAG, "userEmail: " + userImgURL);

        if(!userImgURL.isEmpty() || userImgURL == "") {
            try {
                new DownloadImageTask((ImageView) findViewById(R.id.userProfileImgV))
                        .execute(userImgURL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        Button updateButton = (Button) findViewById(R.id.update_btn);
        Button changePWDbutton = (Button) findViewById(R.id.changepwd_btn);

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
                try {
                    upDateMyUser();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Wait until we get our User Info before continuing:
                CurrentUser.Refresh();
            }
        });



    }

//    // Switch to map fragment
//    public void BacktoMap(View v){
//
//        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//
//    }



    public void upDateMyUser() throws ExecutionException, InterruptedException {
        myName = userfName.getText().toString() + " " + userlName.getText().toString();
        new PostUpdates(myName).execute().get();
    }

    @Override
    public void onDismissDialog(String userImgURL) {
        Log.e("Android : ", "This is at onDismissDialogUserProfile");

        try {
            new DownloadImageTask((ImageView) findViewById(R.id.userProfileImgV))
                    .execute(userImgURL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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

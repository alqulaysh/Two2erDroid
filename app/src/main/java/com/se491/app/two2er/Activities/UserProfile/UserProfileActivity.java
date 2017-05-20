package com.se491.app.two2er.Activities.UserProfile;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.UserObject;
import com.se491.app.two2er.R;

import java.util.concurrent.ExecutionException;

/**
 * Created by SINNER on 5/3/17.
 */

public class UserProfileActivity extends AppCompatActivity {
    private UserProfileListAdapter LA;
    private ChangeProfImageDialog changeProfilePic;
    String imgDecodableString;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final Button updateButton = (Button) findViewById(R.id.update_btn);
        final Button changepwdButton = (Button) findViewById(R.id.changepwd_btn);

        //Get references to the fields:
        ImageView userProfileImage = (ImageView) findViewById(R.id.circleImageView);
        String userImgURL = CurrentUser.getCurrentUser().userImage;
        if(!userImgURL.isEmpty() || !userImgURL.equals("")) {
            try {
                new DownloadImageTask(userProfileImage)
                        .execute(userImgURL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        final UserProfileActivity myContext = this;
        final UserProfileActivity activity = this;
        userProfileImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                changeProfilePic = new ChangeProfImageDialog(myContext, activity, inflater);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG, "Pressed back button: " );
                setResult(RESULT_OK);
                finish();
            }
        });

        UserObject currentUser = CurrentUser.getCurrentUser();

        ListView LAA = (ListView) findViewById(R.id.listviewProfile);
        LA = new UserProfileListAdapter(inflater, this);

        LAA.setAdapter(LA);

        if (currentUser.Tutor != null) {
            ((TextView)findViewById(R.id.TutorRate)).setText(String.valueOf(CurrentUser.getCurrentUser().Tutor.Rating));
        }

        ((TextView)findViewById(R.id.TutorExperience)).setText(String.valueOf(CurrentUser.getCurrentUser().BookingsCount));

        //Update profile fields:
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateMyUser();
            }
        });

        //Change password button:
        changepwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PWDDialogFragment dialogFragment = new PWDDialogFragment();
                dialogFragment.show(fm, "Change User Password!");
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == ChangeProfImageDialog.RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = this.getContentResolver().query(selectedImage,
                        filePathColumn, null,    null, null);
                // Move to first spinner_item
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                 changeProfilePic.fileName = cursor.getString(columnIndex);

                Log.d("UserActivity", "changeProfilePic.fileName" + changeProfilePic.fileName);

                cursor.close();
                ImageView imgView = changeProfilePic.profileImage;
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }


    public void upDateMyUser() {
        try {
            new PostUpdates(CurrentUser.getCurrentUser()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}

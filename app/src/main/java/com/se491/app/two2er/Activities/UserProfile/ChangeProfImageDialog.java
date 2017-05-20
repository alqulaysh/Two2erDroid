package com.se491.app.two2er.Activities.UserProfile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.se491.app.two2er.HelperObjects.CurrentUser;
import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
import com.se491.app.two2er.R;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eoliv on 3/12/2017.
 */

public class ChangeProfImageDialog extends Dialog {

    public static int RESULT_LOAD_IMG = 1;
    public ImageView profileImage;
    private String TAG = "ChangeProfDialog";
    private Activity myActivity;
    String fileName = "";
    File fileToUpload;
    String extension = "";
    public String urlImg = "";


    public ChangeProfImageDialog(@NonNull Context context, Activity activity, LayoutInflater inflater) {
        super(context);
        myActivity = activity;
        final View alertLayout = inflater.inflate(R.layout.fragment_upload_img, null, false);
        profileImage = (ImageView) alertLayout.findViewById(R.id.imgView);
        final Button loadImageButton = (Button) alertLayout.findViewById(R.id.buttonLoadPicture);

        //Update the users profile pic:
        CurrentUser.updateProfilePics(profileImage);

        loadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImagefromGallery();
            }
        });


        new AlertDialog.Builder(context)
                .setTitle("Load a new Profile Image.")
                .setMessage("Are you sure you want to change your profile image?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            saveImage();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setView(alertLayout)
                .show();
    }

    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        myActivity.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void saveImage() throws InterruptedException {

        Thread myThread = new Thread() {
            public void run() {
                if (fileName != null & !fileName.equals("")) {
                    fileToUpload = new File(fileName);

                    int i = fileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = fileName.substring(i + 1);
                    }

                    Request request = new Request.Builder()
                            .url(ServerApiUtilities.GetServerS3Url())
                            .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                            .get()
                            .build();
                    try {
                        Response responseFromS3 = OkHttpClientFactory.Create().newCall(request).execute();

                        String responseS3Str = responseFromS3.body().string();
                        String userId = CurrentUser.getCurrentUser().id;

                        String AccessKey = responseS3Str.split(":")[0];
                        String SecretKey = responseS3Str.split(":")[1];

                        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(AccessKey, SecretKey));
                        s3.setRegion(Region.getRegion(Regions.US_EAST_2));

                        PutObjectRequest putObj = new PutObjectRequest("two2er", "images/" + userId + "." + extension, fileToUpload);
                        putObj.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3.putObject(putObj);

                        urlImg = "https://s3.us-east-2.amazonaws.com/two2er/images/" + userId + "." + extension;

                        RequestBody requestBody = new FormBody.Builder()
                                    .add("user_id", userId)
                                    .add("image_url", urlImg)
                                    .build();

                        Request request2 = new Request.Builder()
                                    .url(ServerApiUtilities.GetServerApiUrl() +
                                            ServerApiUtilities.SERVER_API_URL_ROUTE_USERS +
                                            ServerApiUtilities.SERVER_API_URL_ROUTE_USERS_UPDATE)
                                    .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                                    .post(requestBody)
                                    .build();

                        Response response2 = OkHttpClientFactory.Create().newCall(request2).execute();
                        String responseForMe = response2.body().string();
                        Log.d("Android responseForMe: ", responseForMe);
                                    //Update the Users profile pic:
                                    //CurrentUser.Refresh();
                                    CurrentUser.getCurrentUser().userImage = urlImg;
                                    CurrentUser.updateProfilePics((ImageView) myActivity.findViewById(R.id.circleImageView));
                                    dismiss();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        myThread.start();
        //myThread.join();
        if (fileName.equals("")) {
            Toast.makeText(myActivity, "No image was selected!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(myActivity, "Image Saved", Toast.LENGTH_LONG).show();
        }
    }
}

package com.se491.app.two2er.Fragments.UserProfile;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.se491.app.two2er.R;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by eoliv on 3/12/2017.
 */

public class ChangeProfileImageDialog extends DialogFragment {

    private Button buttonLoadPicture;
    private Button buttonImportPicture;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String fileName = "";
    File fileToUpload;
    String extension = "";
    public String urlImg = "";
    View rootView;
    onDialogDismissListener mListener;

    // Container Activity must implement this interface
    public interface onDialogDismissListener {
        void onDismissDialog(String imgURL);
    }


    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e("Android : ", "This is at ChangeProfileImageDialog");

        Fragment parentFragment = getTargetFragment();
        if (parentFragment instanceof DialogInterface.OnDismissListener) {
            if(urlImg != "") {
                mListener = (onDialogDismissListener) parentFragment;
                mListener.onDismissDialog(urlImg);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upload_img, container, false);
        getDialog().setTitle("Change Profile Image");

        buttonLoadPicture = (Button)rootView.findViewById(R.id.buttonLoadPicture);
        buttonImportPicture = (Button)rootView.findViewById(R.id.buttonImportPicture);

        buttonLoadPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });

        buttonImportPicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    saveImage(v);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = rootView.getContext().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                fileName = cursor.getString(columnIndex);

                Log.d("Android : ", fileName);

                cursor.close();
                ImageView imgView = (ImageView) rootView.findViewById(R.id.imgView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(rootView.getContext(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(rootView.getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }



    public void saveImage(View view) throws InterruptedException {
        Thread myThread = new Thread() {
            public void run() {
                if(fileName != null & !fileName.equals("")){
                    fileToUpload = new File(fileName);

                    int i = fileName.lastIndexOf('.');
                    if (i > 0) {
                        extension = fileName.substring(i + 1);
                    }
                    Log.d("Android : ", extension);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .build();

                    Request request = new Request.Builder()
                            .url("http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users/me")
                            .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                            .get()
                            .build();

                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("Android : ", e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response)
                                throws IOException {
                            final String responseStr = response.body().string();
                            Log.d("Android : ", responseStr);

                            String userId = "";

                            try {
                                JSONObject userData = new JSONObject(responseStr);
                                userId = userData.getString("_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("Android : ", "User ID=" + userId);

                            AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials("AKIAIQI5PG7PW7ZEOQ7A", "7PvprdK3qErADi10IOmHZyK+KMazfdCu+9iBfdss"));
                            s3.setRegion(Region.getRegion(Regions.US_EAST_2));

                            PutObjectRequest putObj = new PutObjectRequest("two2er", "images/" + userId + "." + extension, fileToUpload);
                            putObj.setCannedAcl(CannedAccessControlList.PublicRead);
                            s3.putObject(putObj);

                            urlImg = "https://s3.us-east-2.amazonaws.com/two2er/images/" + userId + "." + extension;

                            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                    .build();

                            RequestBody requestBody = new FormBody.Builder()
                                    .add("user_id", userId)
                                    .add("image_url", urlImg)
                                    .build();

                            Request request = new Request.Builder()
                                    .url("http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users/update")
                                    .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                                    .post(requestBody)
                                    .build();

                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("Android : ", e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response)
                                        throws IOException {
                                    final String responseStr = response.body().string();
                                    Log.d("Android responseStr : ", responseStr);
                                    dismiss();
                                }
                            });
                        }
                    });
            }
        }
        };
        myThread.start();
        if(fileName.equals("")){
            Toast.makeText(rootView.getContext(), "No image was selected!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(rootView.getContext(), "Image Saved", Toast.LENGTH_LONG).show();
        }

    }

    private Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }
        return builder.build();
    }
}
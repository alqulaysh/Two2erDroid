package com.se491.app.two2er;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.Account;
import com.stormpath.sdk.models.StormpathError;
import com.stormpath.sdk.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadImageActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String fileName;
    File fileToUpload;
    String extension = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                fileName = cursor.getString(columnIndex);

                Log.d("Android : ", fileName);

                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
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

    public void saveImage(View view) {
        new Thread()
        {
            public void run()
            {
                fileToUpload = new File(fileName);

                int i = fileName.lastIndexOf('.');
                if (i > 0) {
                    extension = fileName.substring(i+1);
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
                    @Override public
                    void onFailure(Call call, IOException e) {
                        Log.d("Android : ", e.getMessage());
                    }

                    @Override public void onResponse(Call call, Response response)
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

                        PutObjectRequest putObj=new PutObjectRequest("two2er", "images/" + userId + "." + extension, fileToUpload);
                        putObj.setCannedAcl(CannedAccessControlList.PublicRead);
                        s3.putObject(putObj);

                        String imageUrl = "https://s3.us-east-2.amazonaws.com/two2er/images/" + userId + "." + extension;

                        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                .build();

                        RequestBody requestBody = new FormBody.Builder()
                                .add("user_id", userId)
                                .add("image_url", imageUrl)
                                .build();

                        Request request = new Request.Builder()
                                .url("http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users/update")
                                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                                .post(requestBody)
                                .build();

                        okHttpClient.newCall(request).enqueue(new Callback() {
                            @Override public
                            void onFailure(Call call, IOException e) {
                                Log.d("Android : ", e.getMessage());
                            }

                            @Override public void onResponse(Call call, Response response)
                                    throws IOException {
                                final String responseStr = response.body().string();
                                Log.d("Android : ", responseStr);

                                //startActivity(new Intent(UploadImageActivity.this, SideMenuActivity.class));
                                //Log.d("Android : ", "Started Side Menu");
                                //finish();
                            }
                        });

                        startActivity(new Intent(UploadImageActivity.this, SideMenuActivity.class));
                        Log.d("Android : ", "Started Side Menu");
                        finish();
                    }
                });
            }
        }.start();

        Toast.makeText(UploadImageActivity.this, "Image Saved", Toast.LENGTH_LONG).show();
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

package com.se491.app.two2er.Activities;

import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;

import java.io.File;
import java.net.URL;
import java.util.Date;

public class UploadImageToAmazonServer extends AsyncTask<String, Integer, String> {

    private String urlStr;
    private String imageName;
    File image;
    WebServiceInterface<String, String> mInterface;

    public UploadImageToAmazonServer(String imageName, File image) {

        this.imageName = imageName;
        this.image = image;
    }

    public void result(WebServiceInterface<String, String> myInterface) {
        this.mInterface = myInterface;
    }

    @Override
    protected String doInBackground(String... params) {
        URL url = null;

        AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials("AKIAIQI5PG7PW7ZEOQ7A", "7PvprdK3qErADi10IOmHZyK+KMazfdCu+9iBfdss"));
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_2));

        try {
            String imagePath =  imageName;
            PutObjectRequest por = new PutObjectRequest("two2er/images", imagePath, image);
            s3Client.putObject(por);
            ResponseHeaderOverrides override = new ResponseHeaderOverrides();
            override.setContentType("image/jpeg");
            //publishProgress(30);
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest("bucketname/foldername", imagePath);
            urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));  // Added an hour's worth of milliseconds to the current time.
            urlRequest.setResponseHeaders(override);
            url=s3Client.generatePresignedUrl(urlRequest);

            urlStr = url + "";

        } catch (com.amazonaws.AmazonClientException amazonExp) {
            Log.d("setImageSync", amazonExp.getLocalizedMessage());
        }

        return urlStr;
    }


    @Override
    protected void onPostExecute(String result) {
        //   Utils.hideDialog();
        //progressBar.setVisibility(View.GONE);
        //Utility.hideProgressBar();
        mInterface.success(this.urlStr);
        //Toast.makeText(UploadImageActivity.this, "Image Saved", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPreExecute() {
        //Utility.showProgressDialog(mContext);
    }


    @Override
    protected void onProgressUpdate(Integer... vaIntegers) {
//        progressBar.setProgress(vaIntegers[0]);

    }

    public interface WebServiceInterface<E, R> {
        void success(E reslut);

        void error(R Error);
    }
}
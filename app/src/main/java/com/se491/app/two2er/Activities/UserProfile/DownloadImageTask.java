package com.se491.app.two2er.Activities.UserProfile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by eoliv on 5/17/2017.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>  {
        ImageView bmImage;
        String TAG = "DownloadImageTask";

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
            Log.i(TAG, "Inside OnPostExecute" + bmImage.toString());
            bmImage.setImageBitmap(result);
        }
}

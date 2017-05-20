package com.se491.app.two2er.Activities.Bookings;

import android.util.Log;


import com.se491.app.two2er.HelperObjects.OkHttpClientFactory;
import com.se491.app.two2er.HelperObjects.TimeBlockObject;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONException;
import java.io.IOException;
import java.text.ParseException;

import java.util.ArrayList;
import org.json.JSONArray;

import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by ajscilingo on 5/17/17.
 */

public class GetTutorSchedule extends Thread {

    private static String TAG = "GetTutorSchedule";

    private ArrayList<TimeBlockObject> _timeBlockList = new ArrayList<TimeBlockObject>();
    private String _tutorId = "";

    public GetTutorSchedule(String tutorId) throws RuntimeException{
        if(tutorId == null)
            throw new RuntimeException("tutorId is not set");

        _tutorId = tutorId;
    }

    private String getURL() throws RuntimeException {
        if(_tutorId.isEmpty())
            throw new RuntimeException("tutorId is not set");
        return ServerApiUtilities.GetServerApiUrl() + ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING + ServerApiUtilities.SERVER_API_URL_ROUTE_BOOKING_GET_TUTOR_SCHEDULE + _tutorId;
    }

    public ArrayList<TimeBlockObject> getTimeBlockList(){
        return _timeBlockList;
    }

    @Override
    public void run() {
        Request request = new Request.Builder()
                .url(getURL())
                .headers(ServerApiUtilities.buildStandardHeaders(Stormpath.getAccessToken()))
                .get()
                .build();
        try {
            Response response = OkHttpClientFactory.Create().newCall(request).execute();

            String jsonResponse = response.body().string();
            Log.i(TAG, "URL used in GetTutorSchedule: " + getURL());

            JSONArray timeBlocks = new JSONArray(jsonResponse);
            for (int i=0; i< timeBlocks.length(); i++){
                TimeBlockObject timeBlock = new TimeBlockObject(timeBlocks.getJSONObject(i));
                _timeBlockList.add(timeBlock);
            }
            Log.i(TAG, "Size of _timeBlockList: " + _timeBlockList.size());
        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

}

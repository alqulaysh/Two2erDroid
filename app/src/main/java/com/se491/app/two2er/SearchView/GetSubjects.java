/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er.SearchView;

import android.util.Log;

import com.se491.app.two2er.OkHttpClientFactory;
import com.se491.app.two2er.SearchView.Data.SubjectSuggestion;
import com.se491.app.two2er.Utilities.ServerApiUtilities;
import com.stormpath.sdk.Stormpath;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;


public class GetSubjects extends Thread {

    private static String TAG = "GetSubjects";
    private ArrayList<SubjectSuggestion> subjectList = new ArrayList<SubjectSuggestion>();

    private String getURL() {
        return ServerApiUtilities.GetServerApiUrl() + ServerApiUtilities.SERVER_API_URL_ROUTE_SUBJECTS;
    }
    public GetSubjects() {
    }

    public ArrayList<SubjectSuggestion> getSubjectList() { return subjectList; }

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
                Log.i(TAG, "URL used in GetBookingsNew: " + getURL());

                JSONArray subjects = new JSONArray(jsonResponse);
                for (int i = 0; i < subjects.length(); i++) {
                    String subjectName = subjects.getJSONObject(i).getString("name");
                    SubjectSuggestion subject = new SubjectSuggestion(subjectName);
                    subjectList.add(subject);
                }
                Log.i(TAG, "Size of subjectList: " + subjectList.size());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
    }
}

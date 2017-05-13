package com.se491.app.two2er.HelperObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pazra on 5/9/2017.
 */

public class TutorObject {
    public ArrayList<String> Subjects = new ArrayList<>();
    public int Rating = 0;

    public TutorObject() { }
    public TutorObject(JSONObject obj) throws JSONException {
        if (obj.has("subjects")) {
            JSONArray subs = obj.getJSONArray("subjects");
            for(int i = 0; i< subs.length(); i++) {
                Subjects.add(String.valueOf(subs.get(i)).toLowerCase());
            }
        }

        if (obj.has("rating")) {
            Rating = obj.getInt("rating");
        }
    }
}

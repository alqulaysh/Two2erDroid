package com.se491.app.two2er.HelperObjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pazra on 5/10/2017.
 */

public class EducationObject {
    public String School = "";
    public String Degree = "";
    public String Field = "";
    public int Year = 2000;
    public boolean InProgress = true;

    public EducationObject() {

    }

    public String getSchoolAndDegree(){
        String result = "";
        if(!School.isEmpty())
            result = School;
        if(!Degree.isEmpty())
            result = result + ", " + Degree;
        return result;
    }
    public EducationObject(JSONObject obj) throws JSONException {
        if (obj.has("school")) {
            School = obj.getString("school");
        }

        if (obj.has("degree")) {
            Degree = obj.getString("degree");
        }

        if (obj.has("field")) {
            Field = obj.getString("field");
        }

        if (obj.has("year")) {
            Year = obj.getInt("year");
        }

        if (obj.has("inprogress")) {
            InProgress = obj.getBoolean("inprogress");
        }
    }

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("school", School);
        obj.put("degree", Degree);
        obj.put("field", Field);
        obj.put("year", Year);
        obj.put("inprogress", InProgress);
        return obj;
    }
}

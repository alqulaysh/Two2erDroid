package com.se491.app.two2er.HelperObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by eoliv on 2/28/2017.
 */

public class UserObject {
    public String id = "";
    public String fname = "";
    public String lname = "";
    public String age = "";
    public String email = "";
    public String userImage = "";
    public String userMode = "";
    public double dLong = 0.0;
    public double dLat = 0.0;
    public String[] userGroups = new String[10];
    public ArrayList<String> subjects = new ArrayList<String>();

    public UserObject(){}

    public UserObject(String id, String fname, String lname, String age, String email, String userImage){
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.email = email;
        this.userImage = userImage;
    }

    public String getUserFullName(){
        return fname + " " + lname;
    }

    public boolean userGroupsContains(String mode){
        for(int i = 0; i < userGroups.length; i++){
            if(userGroups[i].equals(mode)){
                return true;
            }
        }
        return false;
    }

    public String[] getListArray(){
        return new String[]{fname, lname, email, age};
    }

    public UserObject(JSONObject user) throws JSONException {
        this.id = user.getString("_id");
        String[] fullName = user.getString("name").split(" ");

        if(fullName.length >= 1) {
            this.fname = fullName[0];
        }
        if(fullName.length == 2) {
            this.lname = fullName[1];
        }

        this.age = user.getString("age");
        this.email = user.getString("email");
        this.userImage = user.getString("image_url");

        this.userMode = user.getString("userMode");

        //Get an array of our usergorups:
        String tempuserGroups = user.getString("usergroups");
        this.userGroups = tempuserGroups.replace("[", "").replace("]", "").split(",");

        // GeoJSON node in JSON Object
        JSONObject location = user.getJSONObject("location");
        String tempCoords = location.getString("coordinates");
        String[] coords = tempCoords.replace("[", "").replace("]", "").split(",");
        //Split the array into the proper double varaibales:
        this.dLong = Double.parseDouble(coords[0]);
        this.dLat = Double.parseDouble(coords[1]);

        if(user.has("tutor")) {
            JSONObject tutorObj = user.getJSONObject("tutor");
            JSONArray subs = tutorObj.getJSONArray("subjects");
            for(int i = 0; i< subs.length(); i++) {
                subjects.add(String.valueOf(subs.get(i)).toLowerCase());
            }
        }
    }
}

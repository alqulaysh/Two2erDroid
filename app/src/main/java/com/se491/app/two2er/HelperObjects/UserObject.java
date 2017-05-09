package com.se491.app.two2er.HelperObjects;

import android.util.Log;

import com.se491.app.two2er.GetBookingsNew;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eoliv on 2/28/2017.
 */

public class UserObject {
    public String[] userGroups = new String[10];
    public TutorObject Tutor = new TutorObject();

    public String id = "";
    public String fname = "";
    public String lname = "";
    public String age = "";
    public String email = "";
    public String userImage = "";
    public String userMode = "";
    public double dLong = 0.0;
    public double dLat = 0.0;
    public int BookingsCount = 0;

    private String TAG = "UserObject";

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

        if(user.has("Tutor")) {
            JSONObject tutorObj = user.getJSONObject("Tutor");
            JSONArray subs = tutorObj.getJSONArray("subjects");
            for(int i = 0; i< subs.length(); i++) {
                Tutor.Subjects.add(String.valueOf(subs.get(i)).toLowerCase());
            }
        }
    }

    public void setCountOfBookings() {
        GetBookingsNew task = new GetBookingsNew();
        task.start();
        try {
            task.join();
        }
        catch (Exception ex) {
            Log.e(TAG, "Error in setCountOfBookings: " + ex.toString() + "\n" + ex.getStackTrace());
        }

        BookingsCount = task.getBookingList().size();
    }
}

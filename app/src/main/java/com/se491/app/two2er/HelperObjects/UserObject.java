package com.se491.app.two2er.HelperObjects;

import android.graphics.Bitmap;
import android.util.Log;

import com.se491.app.two2er.Activities.Bookings.GetBookingsNew;
import com.se491.app.two2er.Activities.UserProfile.DownloadImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by eoliv on 2/28/2017.
 */

public class UserObject {
    public ArrayList<String> userGroups = new ArrayList<>();
    public TutorObject Tutor = new TutorObject();
    public EducationObject Education = new EducationObject();

    public String id = "";
    public String fname = "";
    public String lname = "";
    public String age = "";
    public String email = "";
    public String userImage = "";
    public Bitmap userImageBitMap = null;
    public String userMode = "";
    public double dLong = -87.6298;
    public double dLat = 41.8781;
    public int BookingsCount = 0;
    public float Rating = 0.0f;
    public boolean IsValidTimekitUser = false;

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

    //Check if the UserGroups array contains the desired string you are looking for:
    public boolean userGroupsContains(String mode){
        for(String group : userGroups){
            if(group.toLowerCase().equals(mode.toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    //Return the current users BitMap;
    public Bitmap getUserBitMap() {
        if(userImageBitMap == null){
            xDownloadUserBitMap();
        }
        return userImageBitMap;
    }

    //Download the UsersBitMap:
    public void xDownloadUserBitMap() {
        if(!userImage.isEmpty()) {
            try {
                this.userImageBitMap = new DownloadImageTask()
                        .execute(userImage).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public List<FieldPair> getListFieldPair(){
        List<FieldPair> myFields = new ArrayList<>();
        myFields.add(new FieldPair("First Name: ", fname));
        myFields.add(new FieldPair("Last Name: ", lname));
        myFields.add(new FieldPair("Email: ", email));
        myFields.add(new FieldPair("Age: ", age));
        myFields.add(new FieldPair("School: ", Education.School));
        return myFields;
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
        String[] tempArray = tempuserGroups.replace("[", "").replace("]", "").replace("\"", "").split(",");
        Collections.addAll(userGroups, tempArray);

        // GeoJSON node in JSON Object
        JSONObject location = user.getJSONObject("location");
        String tempCoords = location.getString("coordinates");
        String[] coords = tempCoords.replace("[", "").replace("]", "").split(",");
        //Split the array into the proper double varaibales:
        this.dLong = Double.parseDouble(coords[0]);
        this.dLat = Double.parseDouble(coords[1]);

        if(!JsonValueIsNull(user, "tutor")) {
            JSONObject tutorObj = user.getJSONObject("tutor");
            Tutor = new TutorObject(tutorObj);
        }

        if (!JsonValueIsNull(user, "education")) {
            JSONArray eduObj = user.getJSONArray("education");
            if (eduObj.length() > 0) {
                Education = new EducationObject(eduObj.getJSONObject(0));
            }
        }

        if (!JsonValueIsNull(user, "timekit_token")) {
            IsValidTimekitUser = true;
        }
    }

    private boolean JsonValueIsNull(JSONObject obj, String name) throws JSONException {
        return !(obj.has(name) && !obj.isNull(name) && !obj.getString(name).equals("[null]"));
    }

    public void setCountOfBookings() {
        GetBookingsNew task = new GetBookingsNew();
        task.start();
        try {
            task.join();
        }
        catch (Exception ex) {
            Log.e(TAG, "Error in setCountOfBookings: " + ex.toString());
            ex.printStackTrace();
        }

        BookingsCount = task.getBookingList().size();
    }

}

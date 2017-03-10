package com.se491.app.two2er;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by eoliv on 2/28/2017.
 */

public class UserObject {
    String id;
    String name;
    String age;
    String email;
    String userImage;
    Double dLong;
    Double dLat;
    public UserObject(){}
    public UserObject(JSONObject user) throws JSONException {
        this.id = user.getString("_id");
        this.name = user.getString("name");
        this.age = user.getString("age");
        this.email = user.getString("email");
        this.userImage = "" + R.drawable.genuser;

        if(user.getString("image_url") != ""){
            this.userImage = user.getString("image_url");
        }


        // GeoJSON node in JSON Object
        JSONObject location = user.getJSONObject("location");
        String tempCoords = location.getString("coordinates");

        String[] coords = tempCoords.replace("[", "").replace("]", "").split(",");
        //Split the array into the proper double varaibales:
        this.dLong = Double.parseDouble(coords[0]);
        this.dLat = Double.parseDouble(coords[1]);
    }
}

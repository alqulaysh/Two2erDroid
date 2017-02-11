/**
 * Created by Nithun on 2/1/2017.
 */
package com.se491.app.two2er;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class GetUsers extends AsyncTask<Void, Void, Void> {
    private static final String SERVER_API_URL = "http://server.scilingo.net:8080/api/users";

    private SideMenuActivity myMapActivity;

    public GetUsers(SideMenuActivity myActivity) {
        myMapActivity = myActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(myMapActivity,"Connecting to the Two2er Server.",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String url = SERVER_API_URL;
        String jsonStr = sh.makeServiceCall(url);

        //Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            try {
                JSONArray users = new JSONArray(jsonStr);
                // Getting JSON Array node
                //JSONArray contacts = jsonObj.getJSONArray("");

                System.out.println(users.length());
                // looping through All Contacts
                for (int i = 0; i < users.length(); i++) {

                    JSONObject c = users.getJSONObject(i);
                    //System.out.println(c.getString("_id"));
                    String id = c.getString("_id");
                    String name = c.getString("name");
                    String age = c.getString("age");


                    // Phone node is JSON Object
                    JSONObject phone = c.getJSONObject("location");
                    //System.out.println(c.getString("_id"));

                    String coords = phone.getString("coordinates");
                    //System.out.println(coords);
                    // tmp hash map for single contact
                    HashMap<String, String> user = new HashMap<>();

                    // adding each child node to HashMap key => value
                    user.put("id", id);
                    user.put("name", name);
                    user.put("email", age);
                    user.put("cords", coords);

                    // adding contact to contact list
                    myMapActivity.usersAround.add(user);
                }
            } catch (final JSONException e) {
                //Log.e(TAG, "Json parsing error: " + e.getMessage());
                myMapActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(myMapActivity.getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

        } else {
            //Log.e(TAG, "Couldn't get json from server.");
            myMapActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(myMapActivity.getApplicationContext(),
                            "Couldn't get json from server. Check LogCat for possible errors!",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

    }
}

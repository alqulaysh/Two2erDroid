package com.se491.app.two2er;

import android.util.Log;

import com.google.gson.JsonObject;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathConfiguration;
import com.stormpath.sdk.StormpathLogger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
/**
 * Created by pazra on 4/23/2017.
 */

public class NodeServerApiTest {

    private static OkHttpClient okHttpClient;
    private int responseStatus;

    @BeforeClass
    public static void setupTestClass() {
        okHttpClient = new OkHttpClient.Builder()
                .build();
    }

    @Before
    public void setup() {

    }

    @Test
    public void Test() {
        JSONObject obj = mock(JSONObject.class);
        try {
            obj.put("location", "test");
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        System.out.println(obj.toString());
    }

    // must mock everything...
    @Test
    public void TestGetAllUsers() {
        JsonObject obj = new JsonObject();

        RequestBody requestBody = new FormBody.Builder()
                .add("location", obj.getAsString())
                .build();

        Request request = new Request.Builder()
                .url(ServerApiUtilities.GetServerApiUrl_NoAuth() + "studentlocations")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                responseStatus = 2;
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Log.e("Inside PostUpdates", "Json parsing error: " + response.message());
                responseStatus = 1;
            }

        });
        while(responseStatus < 1){}

        System.out.print("asdf");
    }
}

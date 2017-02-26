package com.se491.app.two2er;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button changePassword;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        changePassword = (Button)findViewById(R.id.btnChangePassword);
        passwordInput = (EditText) findViewById(R.id.change_password);

        changePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String newPassword = passwordInput.getText().toString();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();

        RequestBody requestBody = new FormBody.Builder()
                .add("password", newPassword)
                .build();

        Request request = new Request.Builder()
                .url("http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users/changepassword")
                .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override public
            void onFailure(Call call, IOException e) {
                Log.d("Android : ", e.getMessage());
            }

            @Override public void onResponse(Call call, Response response)
                    throws IOException {
                final String responseStr = response.body().string();
                Log.d("Android : ", responseStr);

                startActivity(new Intent(ChangePasswordActivity.this, SideMenuActivity.class));
                finish();
            }
        });
    }

    private Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");

        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }
}

package com.se491.app.two2er;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.RegistrationForm;
import com.stormpath.sdk.models.StormpathError;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set View to register.xml
        setContentView(R.layout.activity_register);

        register = (Button)findViewById(R.id.btnRegister);
        firstNameInput = (EditText) findViewById(R.id.reg_firstname);
        lastNameInput = (EditText) findViewById(R.id.reg_lastname);
        emailInput = (EditText) findViewById(R.id.reg_email);
        passwordInput = (EditText) findViewById(R.id.reg_password);

        register.setOnClickListener(this);

        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        RegistrationForm registrationData = new RegistrationForm(emailInput.getText().toString(),
                passwordInput.getText().toString()).setGivenName(firstNameInput.getText().toString())
                .setSurname(lastNameInput.getText().toString());

        Stormpath.register(registrationData, new StormpathCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                login();
            }

            @Override
            public void onFailure(StormpathError error) {
                Toast.makeText(RegisterActivity.this, error.message(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        Stormpath.login(emailInput.getText().toString(), passwordInput.getText().toString(), new StormpathCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("name", firstNameInput.getText().toString() + " " + lastNameInput.getText().toString())
                        .add("email", emailInput.getText().toString())
                        .build();

                Request request = new Request.Builder()
                        .url("http://10.0.0.37:8080/apiauth/users")
                        .headers(buildStandardHeaders(Stormpath.getAccessToken()))
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override public
                    void onFailure(Call call, IOException e) {
                        Log.d("Android : ", "E-"+e.getMessage());
                    }

                    @Override public void onResponse(Call call, Response response)
                            throws IOException {
                        final String responseStr = response.body().string();
                        Log.d("Android : ", responseStr);

                        new Thread()
                        {
                            public void run()
                            {
                                RegisterActivity.this.runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        Toast.makeText(RegisterActivity.this, responseStr, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }.start();
                    }
                });
            }

            @Override
            public void onFailure(StormpathError error) {
                Toast.makeText(RegisterActivity.this, "LI-"+error.message(), Toast.LENGTH_LONG).show();


            }
        });

        //startActivity(new Intent(this, MainActivity.class));
        //finish();
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

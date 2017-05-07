package com.se491.app.two2er.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.se491.app.two2er.R;
import com.se491.app.two2er.SideMenuActivity;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener  {
    private Button register;
    private String userMode;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;

    //////////////////////////////////////////////////////////
    //////////////////// Spinner by Aziz /////////////////////
    //////////////////////////////////////////////////////////
    // Initializing a String Array for the Spinner
    String[] plants = new String[]{
            "Select Type of User",
            "I want to be a student",
            "I want to be a tutor",
            //////////////////////////////////////////////////////////
            ///////////////////////END//////////////////////////////
            //////////////////////////////////////////////////////////


    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //////////////////////////////////////////////////////////
        //////////////////// Spinner by Aziz /////////////////////
        //////////////////////////////////////////////////////////

        // Set View to register.xml
        setContentView(R.layout.activity_register);


        // Get reference of widgets from XML layout
        Spinner spinner = (Spinner) findViewById(R.id.spinner);


        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, plants
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);

        //////////////////////////////////////////////////////////
        ///////////////////////END//////////////////////////////
        //////////////////////////////////////////////////////////

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
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .add("name", firstNameInput.getText().toString() + " " + lastNameInput.getText().toString())
                        .add("email", emailInput.getText().toString())
                        .add("userMode", userMode)
                        .build();

                Request request = new Request.Builder()
                        .url("http://lowcost-env.niuk5squp9.us-east-2.elasticbeanstalk.com/apiauth/users")
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

                        startActivity(new Intent(RegisterActivity.this, SideMenuActivity.class));
                        Log.d("Android : ", "Started Side Menu");
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(StormpathError error) {
                Toast.makeText(RegisterActivity.this, error.message(), Toast.LENGTH_LONG).show();
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
    //////////////////////////////////////////////////////
    /////////////////Spinner By Aziz/////////////////////
    //////////////////////////////////////////////////////
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView myText= (TextView) view;
        if( myText.getText() == "Select Type of User" ){


        }
        else if( myText.getText() == "I want to be a student" ){
            Toast.makeText(this,"Student account selected" , Toast.LENGTH_SHORT).show();
            userMode = "Student";}
        else if( myText.getText() == "I want to be a tutor" ) {
            Toast.makeText(this, "Tutor account selected", Toast.LENGTH_SHORT).show();
            userMode = "Tutor";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////
    //////////////////////////////////////////////////////


}

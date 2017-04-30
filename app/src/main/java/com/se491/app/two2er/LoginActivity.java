package com.se491.app.two2er;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.se491.app.two2er.Services.LocationRefreshService;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.StormpathError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private EditText emailInput;
    private EditText passwordInput;
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    static final Integer GPS_SETTINGS = 0x7;
    static final Integer READ_EXST = 0x4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.btnLogin);
        emailInput = (EditText) findViewById(R.id.login_email);
        passwordInput = (EditText) findViewById(R.id.editText2);

        login.setOnClickListener(this);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

        askForPermission(new String []{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION | READ_EXST);

        while(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        TextView forgotPasswordScreen = (TextView) findViewById(R.id.forgot_pw_link);

        // Listening to forgot password link
        forgotPasswordScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void askForPermission(String[] permissions, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permissions[0]) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissions[0]) || ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permissions[1])) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LoginActivity.this, permissions, requestCode);

            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, permissions, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public void handleLogin(View v){
        startActivity(new Intent(LoginActivity.this, SideMenuActivity.class));
    }

    @Override
    public void onClick(View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.contentEquals(""))
            email = "AndroidDeviceTestUser@mail.com";

        if (password.contentEquals(""))
            password = "Password1";

        Stormpath.login(email, password, new StormpathCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent loginIntent = new Intent(LoginActivity.this, SideMenuActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
            }

            @Override
            public void onFailure(StormpathError error) {
                Toast.makeText(LoginActivity.this, error.message(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
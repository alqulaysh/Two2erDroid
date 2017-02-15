package com.se491.app.two2er;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.StormpathError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting default screen to login.xml
        setContentView(R.layout.activity_login);

        login = (Button)findViewById(R.id.btnLogin);
        emailInput = (EditText) findViewById(R.id.login_email);
        passwordInput = (EditText) findViewById(R.id.login_password);

        login.setOnClickListener(this);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);

        // Listening to register new account link
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    public void handleLogin(View v){
        startActivity(new Intent(LoginActivity.this, SideMenuActivity.class));
    }

    @Override
    public void onClick(View v) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.contentEquals(""))
            email = "dummy@test.com";

        if (password.contentEquals(""))
            password = "FakePassword123";

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
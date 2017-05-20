package com.se491.app.two2er.Activities.StartPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.se491.app.two2er.R;
import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.StormpathCallback;
import com.stormpath.sdk.models.StormpathError;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button forgotPassword;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        forgotPassword = (Button)findViewById(R.id.btnForgotPassword);
        emailInput = (EditText) findViewById(R.id.forgot_password_email);

        forgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String email = emailInput.getText().toString();

        Stormpath.resetPassword(email, new StormpathCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ForgotPasswordActivity.this, "A reset email will be sent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(StormpathError error) {
                Toast.makeText(ForgotPasswordActivity.this, error.message(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

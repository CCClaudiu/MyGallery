package com.claudiu.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private Button btnLogin;
    View.OnClickListener loginListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logIn(view);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Login");
        setContentView(R.layout.activity_login);
        btnLogin=findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(loginListener);
        logIn(findViewById(R.id.create_account_button));
    }

    public void logIn(View v) {
        EditText pin = (EditText) findViewById(R.id.pin);
        String attempted = pin.getText().toString();
        SharedPreferences preferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String savedPin = preferences.getString("pin", null);

        if (savedPin != null) {
            if (attempted.equals(savedPin)) {
                pin.setText("");
                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);
            } else if (attempted.length() > 0) {
                Toast.makeText(getApplicationContext(), "Incorrect Pin",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Intent createAccount = new Intent(this, CreateAccountActivity.class);
            startActivity(createAccount);
        }
    }
}

package com.claudiu.mygallery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccountActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle("Create Account");
    }

    public void savePin(View v) {
        EditText createPin = findViewById(R.id.pin_create);
        EditText confirmPin = findViewById(R.id.pin_confirm);

        String createString = createPin.getText().toString();
        String confirmString = confirmPin.getText().toString();

        if (createString.equals(confirmString)) {
            if (createPin.length() > 2 && createPin.length() < 11) {
                SharedPreferences.Editor editor = getSharedPreferences("LOGIN", MODE_PRIVATE).edit();
                editor.putString("pin", createString);
                editor.apply();
                Intent login = new Intent(this, LoginActivity.class);
                startActivity(login);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Pin must be 3 to 10 characters long", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Pins must match, please try again", Toast.LENGTH_LONG).show();
        }

    }
}

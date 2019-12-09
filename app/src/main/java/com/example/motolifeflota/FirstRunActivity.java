package com.example.motolifeflota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirstRunActivity extends AppCompatActivity {

    private EditText password;
    private Button checkPassword;
    private SharedPreferences sharedPreferences;

    private final static String appPassword="Haslo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        password=findViewById(R.id.app_password_EditText);
        checkPassword=findViewById(R.id.checkPassword_Button);

        sharedPreferences = getApplicationContext().getSharedPreferences("Password", Context.MODE_PRIVATE); // 0 - for private mode

        if(isLoggedIn())
        {
            Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
            startActivity(goToMainActivityIntent);
            finish();
        }

        checkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(appPassword))
                {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Password", true); // Storing boolean - true/false
                    editor.commit(); // commit changes


                    Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
                    startActivity(goToMainActivityIntent);
                    finish();
                }
                else
                {
                    password.setError(password.getText().toString());
                }
            }
        });



    }

    private boolean isLoggedIn() {
        sharedPreferences = getApplicationContext().getSharedPreferences("Password", Context.MODE_PRIVATE); // 0 - for private mode

        if(!sharedPreferences.getBoolean("Password", false))
        {
            return false;
        }
        else
        return true;
    }
}

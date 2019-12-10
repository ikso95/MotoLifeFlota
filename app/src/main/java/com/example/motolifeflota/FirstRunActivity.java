package com.example.motolifeflota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FirstRunActivity extends AppCompatActivity {

    private EditText password;
    private Button checkPassword, password_visibility;
    private SharedPreferences sharedPreferences;

    //private boolean isPasswordVisible = false;

    private final static String appPassword="Haslo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        password=findViewById(R.id.app_password_EditText);
        checkPassword=findViewById(R.id.checkPassword_Button);
        //password_visibility=findViewById(R.id.password_visibility_button);

        sharedPreferences = getApplicationContext().getSharedPreferences("Password", Context.MODE_PRIVATE); // 0 - for private mode

        if(isLoggedIn())
        {
            Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
            startActivity(goToMainActivityIntent);
            finish();
        }


        /*password_visibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordVisible ==false)
                {
                    isPasswordVisible = true;
                    password_visibility.setBackground(getResources().getDrawable(R.drawable.ic_visibility));
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(null);
                    password.setSelection(password.getText().length()); //ustawienie kursora na koniec tekstu przy zmianie widoczności wracał na początek
                }
                else
                {
                    isPasswordVisible=false;
                    password_visibility.setBackground(getResources().getDrawable(R.drawable.ic_visibility_off));
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(password.getText().length()); //ustawienie kursora na koniec tekstu przy zmianie widoczności wracał na początek

                }


            }
        });*/


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

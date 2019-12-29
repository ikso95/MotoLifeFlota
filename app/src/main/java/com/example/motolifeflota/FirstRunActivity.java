package com.example.motolifeflota;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myloadingbutton.MyLoadingButton;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

import at.markushi.ui.CircleButton;

public class FirstRunActivity extends AppCompatActivity {

    private EditText password;
    private CircleButton checkPassword;
    private MyLoadingButton myLoadingButton;

    private SharedPreferences sharedPreferences;



    private final static String appPassword="Haslo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        password=findViewById(R.id.app_password_EditText);
        checkPassword=findViewById(R.id.checkPassword_Button);
        myLoadingButton=findViewById(R.id.my_loading_button);


        sharedPreferences = getApplicationContext().getSharedPreferences("Password", Context.MODE_PRIVATE); // 0 - for private mode

        if(isLoggedIn())
        {
            Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
            startActivity(goToMainActivityIntent);
            finish();
        }



        checkPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
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

            }
        });

        myLoadingButton.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(password.getText().toString().equals(appPassword))
                        {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("Password", true); // Storing boolean - true/false
                                    editor.commit(); // commit changes

                                    Intent goToMainActivityIntent = new Intent(FirstRunActivity.this, MainActivity.class);
                                    startActivity(goToMainActivityIntent);
                                    finish();
                                }
                            }, 1000);
                            myLoadingButton.showDoneButton();
                        }
                        else
                        {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    myLoadingButton.showNormalButton();
                                }
                            }, 1000);
                            myLoadingButton.showErrorButton();
                        }
                    }
                }, 1000);
                myLoadingButton.showLoadingButton();

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
        return false;   //ZMIENIC NA TRUE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
}

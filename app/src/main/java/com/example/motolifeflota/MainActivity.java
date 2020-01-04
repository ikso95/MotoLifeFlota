package com.example.motolifeflota;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.motolifeflota.Email.GMailSender;
import com.example.motolifeflota.Vertical_form_steps.DescriptionStep;
import com.example.motolifeflota.Vertical_form_steps.NameStep;
import com.example.motolifeflota.Vertical_form_steps.PhoneNumberStep;

import com.example.motolifeflota.Vertical_form_steps.PhotoStep.PhotoStep;
import com.example.motolifeflota.Vertical_form_steps.RegistrationNumberStep;
import com.example.motolifeflota.Vertical_form_steps.SelectDateStep;
import com.example.motolifeflota.Vertical_form_steps.SelectTimeStep;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class MainActivity extends AppCompatActivity implements PickiTCallbacks, StepperFormListener {


    private final static String phoneNumberUri = "tel:+48664135806";

    private MaterialDialog mDialog;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GET_SINGLE_FILE = 3;


    private boolean isAttachment = false;
    private List<String> storageFilesPathsList;
    private boolean doubleBackToExitPressedOnce = false;
    private PickiT pickiT;
    private Button callButton;

    private NameStep nameStep;
    private PhoneNumberStep phoneNumberStep;
    private RegistrationNumberStep registrationNumberStep;
    private DescriptionStep descriptionStep;
    private SelectDateStep selectDateStep;
    private SelectTimeStep selectTimeStep;
    private PhotoStep photoStep;

    private VerticalStepperFormView verticalStepperForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearDirectory();

        verticalStepperForm = findViewById(R.id.stepper_form);
        storageFilesPathsList = new ArrayList<>();
        pickiT = new PickiT(this, this);

        nameStep = new NameStep("Imię i nazwisko");
        phoneNumberStep = new PhoneNumberStep("Numer telefonu");
        registrationNumberStep = new RegistrationNumberStep("Numer rejestracyjny");
        descriptionStep = new DescriptionStep("Opis usterki");
        selectDateStep = new SelectDateStep("Data");
        selectTimeStep = new SelectTimeStep("Godzina");
        photoStep = new PhotoStep("Zdjęcie", MainActivity.this, registrationNumberStep);


        verticalStepperForm.setup(this, nameStep, phoneNumberStep,
                registrationNumberStep, descriptionStep, selectDateStep, selectTimeStep, photoStep)
                .stepNextButtonText("Dalej")
                .displayCancelButtonInLastStep(true)
                .lastStepNextButtonText("Wyślij")
                .confirmationStepTitle("Wyślij zgłoszenie")
                .lastStepCancelButtonText("Anuluj")
                .init();


        callButton = findViewById(R.id.zadzwon_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialNumber = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumberUri));
                startActivity(dialNumber);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Jeżeli zrobiono zdjęcie
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                isAttachment = true;

                storageFilesPathsList.add("/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures/" + photoStep.getImageName());

                setNewGalleryAdapter();
        }


        //Jeżeli wybrano plik z pamięci telefonu
        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);
            }
        }




    }


    private String makeEmailBody() {

        return "Zgłaszający: " + nameStep.getUserName() + "\n" +
                "Dane kontaktowe zgłaszającego: " + phoneNumberStep.getPhoneNumber() + "\n" +
                "Numer rejestracyjny: " + registrationNumberStep.getRegistrationNumber() + "\n" +
                "Data usterki: " + selectDateStep.getDate() + "  " + (selectTimeStep.getTime().matches("") ? selectTimeStep.getTime() : "") + "\n" +
                "Opis usterki: " + descriptionStep.getDescription();

    }


    private void sendEmail(final String email_body) {


        new Thread(new Runnable() {

            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("rozproszonebazy@gmail.com", "MotoLifeFlota");

                    sender.sendMail(getBaseContext().getString(R.string.Email_title) + registrationNumberStep.getRegistrationNumber(),               //title - subject
                            email_body,                                                    //body message
                            "lisuoskar@gmail.com",                                 //sender
                            "oskail@wp.pl",                                      //recipent
                            storageFilesPathsList);

                    mDialog.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mDialog.dismiss();
                                    Intent reloadActivity = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(reloadActivity);
                                    finish();
                                }
                            }, 1500);
                            mDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .setTitle("Gratulacje!")

                                    .setMessage("Zgłoszenie zostało wysłane")
                                    .setCancelable(false)
                                    .setAnimation(R.raw.check_mark_success_animation)
                                    .build();
                            mDialog.show();
                        }
                    });


                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                    mDialog.dismiss();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .setTitle("Błąd!")
                                    .setMessage("Wystąpił błąd podczas wysyłania, sprawdź połączenie z internetem i spróbuj ponownie")
                                    .setCancelable(false)
                                    .setAnimation(R.raw.unapproved_cross)
                                    .setNegativeButton("Anuluj", new MaterialDialog.OnClickListener() {
                                        @Override
                                        public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                            dialogInterface.dismiss();
                                            Intent reloadActivity = new Intent(MainActivity.this, MainActivity.class);
                                            startActivity(reloadActivity);
                                            finish();
                                        }
                                    })
                                    .setPositiveButton("Wróć", new MaterialDialog.OnClickListener() {
                                        @Override
                                        public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                            dialogInterface.dismiss();
                                            verticalStepperForm.cancelFormCompletionOrCancellationAttempt();
                                        }
                                    })
                                    .build();
                            mDialog.show();
                        }
                    });

                }
            }
        }).start();

    }


    private void clearDirectory() {
        File dir = new File("/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }


    @Override
    protected void onDestroy() {
        //Przy wyłączeniu aktywności usuwam całą zawartość naszego folderu, żeby unikać gromadzenia danych i związanych z tym bugów
        super.onDestroy();
        File dir = new File("/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
    }


    @Override
    public void PickiTonStartListener() {

    }

    @Override
    public void PickiTonProgressUpdate(int progress) {

    }

    private void setNewGalleryAdapter() {
        photoStep.setStorageFilesPathsList(storageFilesPathsList);
        photoStep.setAdapter();
    }

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {

        isAttachment = true;
        storageFilesPathsList.add(path);
        Log.d("pickItPath", path + "   <--- wygenerwowane dzieki bibliotece pickit");
        setNewGalleryAdapter();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onCompletedForm() {

       /* mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Proszę czekać ...");
        mDialog.show();*/

        mDialog = new MaterialDialog.Builder(MainActivity.this)
                .setTitle("Wysyłanie!")
                .setMessage("Proszę czekać, w zależności od ilości zdjęć, może to zająć chwilę")
                .setCancelable(false)
                .setAnimation(R.raw.mail_send_as_paper_plane)
                .build();
        mDialog.show();

        sendEmail(makeEmailBody());


    }

    @Override
    public void onCancelledForm() {

        clearDirectory();
        Intent reloadActivity = new Intent(MainActivity.this, MainActivity.class);
        startActivity(reloadActivity);
        finish();

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Aby wyjść wciśnij przycisk ponownie", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


}

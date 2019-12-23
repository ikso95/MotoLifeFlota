package com.example.motolifeflota;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.motolifeflota.Email.GMailSender;
import com.example.motolifeflota.PhotosRecyclerView.SliderAdapter;
import com.example.motolifeflota.Vertical_form_steps.DescriptionStep;
import com.example.motolifeflota.Vertical_form_steps.NameStep;
import com.example.motolifeflota.Vertical_form_steps.PhoneNumberStep;
import com.example.motolifeflota.Vertical_form_steps.PhotoStep;
import com.example.motolifeflota.Vertical_form_steps.RegistrationNumberStep;
import com.example.motolifeflota.Vertical_form_steps.SelectDateStep;
import com.example.motolifeflota.Vertical_form_steps.SelectTimeStep;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class MainActivity extends AppCompatActivity implements PickiTCallbacks, StepperFormListener {

    //master 1
    //bra 123

    private EditText nr_rejestracyjny, imie_i_nazwisko, opis, nr_telefonu;
    private TextView dzien_textView, godzina_textView;
    private Button wyslij, zadzwon, dzien_button, godzina_button, zrob_zdjecie, wczytaj_zdjecie, deletePhoto;
    private DatePickerDialog picker;
    private ImageView imageView;

    private final static String myPhoneNumberUri = "tel:+48664135806";

    public ProgressDialog mDialog;

    private String email_body;
    private String selMinute;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GET_SINGLE_FILE = 3;

    private String currentPhotoPath = null;

    private Uri imageUri;
    private File imageFile;

    private boolean isAttachment = false;
    private int nrOfTakenPhotos =0;
    private int nrOfStorageFiles =0;
    private List<String> storageFilesPathsList;




    //-------------------------------------------
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private int mCurrentPage;
    //-------------------------------------------
    private PickiT pickiT;


    private NameStep nameStep;
    private PhoneNumberStep phoneNumberStep;
    public RegistrationNumberStep registrationNumberStep;
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

        nameStep=new NameStep("Imię i nazwisko");
        phoneNumberStep = new PhoneNumberStep("Numer telefonu");
        registrationNumberStep = new RegistrationNumberStep("Numer rejestracyjny");
        descriptionStep = new DescriptionStep("Opis usterki");
        selectDateStep = new SelectDateStep("Data");
        selectTimeStep = new SelectTimeStep("Godzina");
        photoStep = new PhotoStep("Zdjęcie",MainActivity.this, registrationNumberStep);



        verticalStepperForm.setup(this, nameStep, phoneNumberStep,
                registrationNumberStep,descriptionStep,selectDateStep,selectTimeStep, photoStep)
                .stepNextButtonText("Dalej")
                .lastStepNextButtonText("Wyślij")
                .confirmationStepTitle("Wyślij zgłoszenie")
                .displayCancelButtonInLastStep(true)
                //.lastStepCancelButtonColors(Color.red())
                .lastStepCancelButtonText("Anuluj")
                .init();




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Jeżeli zrobiono zdjęcie
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                isAttachment = true;
                nrOfTakenPhotos++;
                imageUri = photoStep.getImageUri(); //FileProvider.getUriForFile(MainActivity.this, "com.example.motolifeflota.fileprovider", imageFile);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);    //Stworzenie bitmap znajac uri

                Matrix matrix = new Matrix();           //obrocenie zdjecia o 90 stopni
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);



                //imageView.setVisibility(View.VISIBLE);
                //imageView.setImageBitmap(rotatedBitmap);


                /*deletePhoto.setVisibility(View.VISIBLE);
                deletePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isAttachment = false;
                        if (isAttachment) {
                            imageFile.delete();
                        }

                        imageView.setVisibility(View.GONE);
                        deletePhoto.setVisibility(View.GONE);
                        clearDirectory();
                        nrOfTakenPhotos = 0;
                    }
                });*/




            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Jeżeli wybrano plik z pamięci telefonu
        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                pickiT.getPath(data.getData(), Build.VERSION.SDK_INT);


            }
        }

        //jezeli cos nie zadziala wyswietl komunikat
        if(resultCode!=RESULT_OK)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)//set icon
                    .setTitle("Error")//set title
                    .setMessage("Wystąpił nieoczekiwany błąd, spróbuj ponownie")     //set message
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {//set positive button
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }


    }





    private String makeEmailBody() {

        return email_body = "Zgłaszający: " + nameStep.getUserName() + "\n" +
                "Dane kontaktowe zgłaszającego: " + phoneNumberStep.getPhoneNumber() + "\n" +
                "Numer rejestracyjny: " + registrationNumberStep.getRegistrationNumber() + "\n" +
                "Data usterki: " + selectDateStep.getDate() + "  " + (selectTimeStep.getTime().matches("") ? selectTimeStep.getTime() : "") + "\n" +
                "Opis usterki: " + descriptionStep.getDescription();

    }


    private void sendEmail(final String email_body) {


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("rozproszonebazy@gmail.com", "MotoLifeFlota");

                    if (isAttachment) {
                        sender.sendMail(getBaseContext().getString(R.string.Email_title) + registrationNumberStep.getRegistrationNumber(),               //title - subject
                                email_body,                                                    //body message
                                "lisuoskar@gmail.com",                                 //sender
                                "oskail@wp.pl",                                      //recipent
                                storageFilesPathsList);

                        mDialog.dismiss();

                    } else {
                        sender.sendMail(getBaseContext().getString(R.string.Email_title) + registrationNumberStep.getRegistrationNumber(),               //title
                                email_body,                                                    //body message
                                "lisuoskar@gmail.com",                                 //sender
                                "oskail@wp.pl");                                     //recipent

                        mDialog.dismiss();

                    }


                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }
        }).start();

    }



    private void clearDirectory()
    {
        File dir = new File("/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }


    @Override
    protected void onDestroy() {
        //Przy wyłączeniu aktywności usuwam całą zawartość naszego folderu, żeby unikać gromadzenia danych i związanych z tym bugów
        super.onDestroy();
        File dir = new File("/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
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

    @Override
    public void PickiTonCompleteListener(String path, boolean wasDriveFile, boolean wasUnknownProvider, boolean wasSuccessful, String Reason) {

        isAttachment=true;
        storageFilesPathsList.add(path);
        Log.d("path",path+"   <--- wygenerwowane dzieki bibliotece pickit");
    }



    @Override
    public void onCompletedForm() {

        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Proszę czekać ...");
        mDialog.show();

        sendEmail(makeEmailBody());

    }

    @Override
    public void onCancelledForm() {

        clearDirectory();

    }




}

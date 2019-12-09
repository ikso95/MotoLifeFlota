package com.example.motolifeflota;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.motolifeflota.Email.GMailSender;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private EditText nr_rejestracyjny, imie_i_nazwisko, opis, nr_telefonu;
    private TextView dzien_textView, godzina_textView;
    private Button wyslij, zadzwon, dzien_button, godzina_button, zrob_zdjecie, wczytaj_zdjecie;
    private DatePickerDialog picker;
    private ImageView imageView;

    private final static String myPhoneNumberUri = "tel:+48664135806";

    public ProgressDialog mDialog;

    private String email_body;
    private String selMinute;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    String currentPhotoPath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nr_rejestracyjny = findViewById(R.id.numer_rejestracyjny_pojazdu_editText);
        imie_i_nazwisko = findViewById(R.id.imie_i_nazwisko_editText);
        opis = findViewById(R.id.opis_editText);
        opis.setSingleLine(false);
        opis.setMinLines(6);
        nr_telefonu = findViewById(R.id.nr_tel_editText);

        dzien_button = findViewById(R.id.dzien_button);
        dzien_textView = findViewById(R.id.dzien_textView);

        godzina_button = findViewById(R.id.godzina_button);
        godzina_textView = findViewById(R.id.godzina_textView);

        zrob_zdjecie = findViewById(R.id.zrob_zdjecie_button);
        wczytaj_zdjecie = findViewById(R.id.wczytaj_zdjecie_button);

        wyslij = findViewById(R.id.wyslij_button);
        zadzwon = findViewById(R.id.zadzwon_button);


        dzien_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dzien_textView.setVisibility(View.VISIBLE);
                                dzien_textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.show();
                dzien_textView.setError(null);
            }
        });


        godzina_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        godzina_textView.setVisibility(View.VISIBLE);
                        if (selectedMinute < 10) {
                            selMinute = "0" + String.valueOf(selectedMinute);
                        } else
                            selMinute = String.valueOf(selectedMinute);
                        godzina_textView.setText(selectedHour + ":" + selMinute);
                        selMinute = "";
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                godzina_textView.setError(null);

            }
        });


        zrob_zdjecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File imageFile = null;
                    try {
                        imageFile = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (imageFile != null) {
                        Uri imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.motolifeflota.fileprovider", imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    }


                }

            }
        });

        wczytaj_zdjecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput() == true) {

                    mDialog = new ProgressDialog(MainActivity.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();

                    Toast.makeText(MainActivity.this, "eheh", Toast.LENGTH_LONG).show();
                    email_body = makeEmailBody();
                    sendEmail(email_body);
                }
            }
        });


        zadzwon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialNumber = new Intent(Intent.ACTION_DIAL, Uri.parse(myPhoneNumberUri));
                startActivity(dialNumber);
            }
        });


    }




    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MotoLifeFlota_usterka";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        //File storageDir = new File(Environment.getExternalStorageDirectory().getPath()+ "/MyAppFolder/MyApp");//getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File image = File.createTempFile(
        //        imageFileName,  /* prefix */
        //        ".jpg",         /* suffix */
        //        storageDir      /* directory */
        //);                                            //ta metoda tworzy pliki o niestandardowych nazwach, dodaje ciągi liczb na końcu

        File image = new File(storageDir, imageFileName + ".jpg");


        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private boolean checkInput() {

        if (!imie_i_nazwisko.getText().toString().trim().matches("") && !nr_rejestracyjny.getText().toString().trim().matches("")
                && !dzien_textView.getText().toString().matches("")
                && !opis.getText().toString().trim().matches("") && !nr_telefonu.getText().toString().trim().matches("")) {
            Log.d("imieInazw", imie_i_nazwisko.getText().toString().trim());
            return true;
        } else {

            if (imie_i_nazwisko.getText().toString().trim().equals("")) {
                imie_i_nazwisko.setError("Prosze wypełnić dane");
            }

            if (nr_rejestracyjny.getText().toString().trim().equals("")) {
                nr_rejestracyjny.setError("Proszę wypełnić dane");
            }

            if (dzien_textView.getText().toString().equals("")) {
                dzien_textView.setError("Proszę wypełnić dane");
            }

            if (opis.getText().toString().trim().equals("")) {
                opis.setError("Proszę wypełnić dane");
            }

            if (nr_telefonu.getText().toString().trim().equals("")) {
                nr_telefonu.setError("Proszę wypełnić dane");
            }

            //Toast.makeText(MainActivity.this, "asd", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private String makeEmailBody() {

        return email_body = "Zgłaszający: " + imie_i_nazwisko.getText().toString().trim() + "\n" +
                "Numer rejestracyjny: " + nr_rejestracyjny.getText().toString().trim() + "\n" +
                "Data usterki: " + dzien_textView.getText().toString() + "  " + godzina_textView.getText().toString() + "\n" +
                "Opis usterki: " + opis.getText().toString().trim() + "\n" +
                "Dane kontaktowe zgłaszającego: " + nr_telefonu.getText().toString().trim();

    }


    private void sendEmail(final String email_body) {


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("rozproszonebazy@gmail.com",
                            "MotoLifeFlota");
                    sender.sendMail(getBaseContext().getString(R.string.Email_title),               //title
                            email_body,                                                    //body message
                            "lisuoskar@gmail.com",                                 //sender
                            "oskail@wp.pl",
                            "/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures/MotoLifeFlota_usterka.jpg");                                     //recipent
                    mDialog.dismiss();

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }
        }).start();

    }


}

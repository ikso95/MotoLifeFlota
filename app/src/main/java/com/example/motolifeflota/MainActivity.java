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

import com.example.motolifeflota.Email.GMailSender;
import com.example.motolifeflota.PhotosRecyclerView.SliderAdapter;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PickiTCallbacks {

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
    static final int CAMERA_PERMISSION_CODE = 2;
    static final int REQUEST_GET_SINGLE_FILE = 3;
    static final int STORAGE_PERMISSION_CODE = 4;

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
    PickiT pickiT;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearDirectory();

        pickiT = new PickiT(this, this);

        //-------------------------------------------
        /*mSlideViewPager = (ViewPager)findViewById(R.id.slideViewPager_start);
        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.addOnPageChangeListener(viewListener);*/


        //automatyczne czasowe przesuwanie się slajdów co określony okres czasu
        //po przejsciu wszystkich okien wraca spowrotem do pierwszego slajdu
        /*final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCurrentPage++;
                if(mCurrentPage==4)
                {
                    mCurrentPage=0;
                    mSlideViewPager.setCurrentItem(getItem(-4));
                }
                else
                {
                    mSlideViewPager.setCurrentItem(getItem(1));
                }
                refreshHandler.postDelayed(this,  5000);
            }
        };
        refreshHandler.postDelayed(runnable, 5000);*/
        //-------------------------------------------



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
        imageView = findViewById(R.id.imageView);
        deletePhoto = findViewById(R.id.deletePhoto);
        wczytaj_zdjecie = findViewById(R.id.wczytaj_zdjecie_button);

        wyslij = findViewById(R.id.wyslij_button);
        zadzwon = findViewById(R.id.zadzwon_button);

        storageFilesPathsList = new ArrayList<String>();


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
                                dzien_textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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

                //Check Camera permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //Permission not granted, request permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);

                } else {
                    // Permission has already been granted
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);      //wywołanie uruchomienia aparatu
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        imageFile = null;
                        try {
                            imageFile = createImageFile();                                  //stworzenie pliku do którego zostanie zapisane zdjecie - bitmapa
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (imageFile != null) {                                            //jezeli plik istnieje podajemy Uri-adres pod ktorym ma byc zapisany obraz, miejsce na dysku
                            imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.motolifeflota.fileprovider", imageFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);   //dodajemy obraz do intent  - przez to ze dodajemy uri mamy adres zdjecia, nie otrzymamy w extras thumbnail obrazu (miniaturki)
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);    //uruchamiamy intnet
                        }


                    }
                }





            }
        });


        wczytaj_zdjecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted, request permission
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

                } else {
                    //Permission granted
                    Intent intent = new Intent();
                    intent.setType("image/* video/*");                  //to choose all files image/* or image/jpg or video/* or video/mp4
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
                }
            }
        });


        wyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInput() == true) {

                    mDialog = new ProgressDialog(MainActivity.this);
                    mDialog.setMessage("Proszę czekać ...");
                    mDialog.show();


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


    //----------------------------------------------------------------
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {

        }


        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private int getItem(int i) {
        return mSlideViewPager.getCurrentItem() + i;
    }
    //------------------------------------------------------------------------------------



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        //Jeżeli zrobiono zdjęcie
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {
                isAttachment = true;
                nrOfTakenPhotos++;

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), imageUri);    //Stworzenie bitmap znajac uri

                Matrix matrix = new Matrix();           //obrocenie zdjecia o 90 stopni
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(rotatedBitmap);


                deletePhoto.setVisibility(View.VISIBLE);
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
                });

                //-------------------------------------------
                mSlideViewPager = (ViewPager)findViewById(R.id.slideViewPager_start);
                sliderAdapter = new SliderAdapter(MainActivity.this,"/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures/MotoLifeFlota_usterka", nrOfTakenPhotos);
                mSlideViewPager.setAdapter(sliderAdapter);

                mSlideViewPager.addOnPageChangeListener(viewListener);
                //-------------------------------------------


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




    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MotoLifeFlota_usterka"+String.valueOf(nrOfTakenPhotos);
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
                "Dane kontaktowe zgłaszającego: " + nr_telefonu.getText().toString().trim() + "\n" +
                "Numer rejestracyjny: " + nr_rejestracyjny.getText().toString().trim() + "\n" +
                "Data usterki: " + dzien_textView.getText().toString() + "  " + godzina_textView.getText().toString() + "\n" +
                "Opis usterki: " + opis.getText().toString().trim();


    }


    private void sendEmail(final String email_body) {


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("rozproszonebazy@gmail.com", "MotoLifeFlota");

                    if (isAttachment) {
                        sender.sendMail(getBaseContext().getString(R.string.Email_title) + nr_rejestracyjny.getText().toString(),               //title
                                email_body,                                                    //body message
                                "lisuoskar@gmail.com",                                 //sender
                                "oskail@wp.pl",                                      //recipent
                                "/storage/emulated/0/Android/data/com.example.motolifeflota/files/Pictures/MotoLifeFlota_usterka",//.jpg",  //sciezka do zrobionych zdjec
                                nrOfTakenPhotos,                                                //nrOfTakenPhotos liczone od 0!
                                storageFilesPathsList);
                        clearInput();
                        mDialog.dismiss();

                    } else {
                        sender.sendMail(getBaseContext().getString(R.string.Email_title) + nr_rejestracyjny.getText().toString(),               //title
                                email_body,                                                    //body message
                                "lisuoskar@gmail.com",                                 //sender
                                "oskail@wp.pl");                                     //recipent
                        clearInput();
                        mDialog.dismiss();

                    }


                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);

                }
            }
        }).start();

    }

    private void clearInput() {

        // modyfikowac widgety z UI mozna zmieniac tylko w glownym watku
        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                isAttachment = false;
                imie_i_nazwisko.setText("");
                nr_rejestracyjny.setText("");
                opis.setText("");
                dzien_textView.setText("");
                godzina_textView.setText("");
                nr_telefonu.setText("");

                storageFilesPathsList.clear();


                if (isAttachment) {
                    nrOfTakenPhotos =0;
                    imageView.setVisibility(View.GONE);
                    clearDirectory();
                    imageFile.delete();
                    deletePhoto.setVisibility(View.GONE);
                }

            } // This is your code
        };
        mainHandler.post(myRunnable);


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
}

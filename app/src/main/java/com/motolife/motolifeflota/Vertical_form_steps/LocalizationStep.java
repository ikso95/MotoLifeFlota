package com.motolife.motolifeflota.Vertical_form_steps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.motolife.motolifeflota.MainActivity;
import com.motolife.motolifeflota.R;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ernestoyaquello.com.verticalstepperform.Step;

public class LocalizationStep extends Step<String> implements LocationListener {


    private TextView addressTextView;
    private AppCompatImageButton localizationButton;
    private LayoutInflater inflater;
    private View view;
    static final int LOCALIZATION_PERMISSION_CODE = 5;
    private FusedLocationProviderClient mFusedLocationClient;
    private  Context context;
    private String address;
    private MaterialDialog mDialog;
    private MainActivity mainActivity;


    public LocalizationStep(String stepTitle, Context context, MainActivity mainActivity) {
        super(stepTitle);
        this.context=context;
        this.mainActivity=mainActivity;
    }



    @Override
    protected View createStepContentLayout() {
        // Here we generate the view that will be used by the library as the content of the step.
        // In this case we do it programmatically, but we could also do it by inflating an XML layout.
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.step_localization,null);

        localizationButton = view.findViewById(R.id.localization_button);

        addressTextView = view.findViewById(R.id.address_TextView);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        localizationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted, request permission
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCALIZATION_PERMISSION_CODE);

                } else {

                    getLastLocation();
                }
            }
        });


        //markAsCompletedOrUncompleted(true);
        return view;
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){

            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    Geocoder geocoder;
                                    List<Address> addresses = null;
                                    geocoder = new Geocoder(context, Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    //String city = addresses.get(0).getLocality();
                                    //String state = addresses.get(0).getAdminArea();
                                    //String country = addresses.get(0).getCountryName();
                                    //String postalCode = addresses.get(0).getPostalCode();
                                    //String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                                    addressTextView.setVisibility(View.VISIBLE);
                                    addressTextView.setText(address);
                                }
                            }
                        }
                );
            } else {
                //Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //context.startActivity(intent);

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mDialog = new MaterialDialog.Builder(mainActivity)
                                .setTitle("Błąd!")
                                .setMessage("Nie udało się pobrać lokalizacji ponieważ ta opcja jest wyłączona na tym telefonie. \n Wróć lub włącz GPS, odczekaj chwilę i spróbuj ponownie.")
                                .setCancelable(false)
                                .setAnimation(R.raw.unapproved_cross)
                                .setNegativeButton("Wróc", new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setPositiveButton("Włącz", new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        context.startActivity(intent);
                                    }
                                })
                                .build();
                        mDialog.show();
                    }
                });

            }

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    public String getAddress() {
        return address;
    }

    @Override
    protected IsDataValid isStepDataValid(String stepData) {
        // The step's data (i.e., the user name) will be considered valid only if it is longer than
        // three characters. In case it is not, we will display an error message for feedback.
        // In an optional step, you should implement this method to always return a valid value.
        
        boolean isTimeValid = true;


        return new IsDataValid(isTimeValid);
    }

    @Override
    public String getStepData() {
        // We get the step's data from the value that the user has typed in the EditText view.
        return address != null ? address : "";
    }

    @Override
    public String getStepDataAsHumanReadableString() {
        // Because the step's data is already a human-readable string, we don't need to convert it.
        // However, we return "(Empty)" if the text is empty to avoid not having any text to display.
        // This string will be displayed in the subtitle of the step whenever the step gets closed.
        return address != null ? address : "";
    }

    @Override
    protected void onStepOpened(boolean animated) {
        // This will be called automatically whenever the step gets opened.

    }

    @Override
    protected void onStepClosed(boolean animated) {
        // This will be called automatically whenever the step gets closed.
    }

    @Override
    protected void onStepMarkedAsCompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as completed.
    }

    @Override
    protected void onStepMarkedAsUncompleted(boolean animated) {
        // This will be called automatically whenever the step is marked as uncompleted.
    }

    @Override
    public void restoreStepData(String stepData) {
        // To restore the step after a configuration change, we restore the text of its EditText view.
        addressTextView.setText(stepData);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
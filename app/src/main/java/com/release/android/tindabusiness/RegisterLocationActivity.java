package com.release.android.tindabusiness;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

 
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterLocationActivity extends AppCompatActivity {

    private ImageView getLocation, back;
    private TextView next;
    private EditText latitude, longitude;
    private ArrayList<String> businessDetails;
    private static final int REQUEST_GPS = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String latitudeText,longitudeText,addressText;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_location);

        setupViews();
        setupGps();
        activateListeners();

    }

    private void setupViews() {

        getLocation = (ImageView) findViewById(R.id.imageViewRegisterLocationGetCurrentLocation);
        next = (TextView) findViewById(R.id.textViewRegisterLocationContinue);
        latitude = (EditText) findViewById(R.id.editTextRegisterLocationLatitude);
        longitude = (EditText) findViewById(R.id.editTextRegisterLocationLongitude);
        back = (ImageView) findViewById(R.id.imageViewRegisterLocationBack);

        businessDetails = new ArrayList<>();
        businessDetails = getIntent().getStringArrayListExtra("businessDetails");

    }

    private void activateListeners() {

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(RegisterLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(RegisterLocationActivity.this,new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_GPS);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        latitude.setText(String.valueOf(location.getLatitude()));
                        longitude.setText(String.valueOf(location.getLongitude()));


                    }
                });

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean isValid = checkIfValid();
                if(isValid){

                    getAddressFromLocation();

                    businessDetails.set(1,addressText);
                    businessDetails.add(latitudeText);
                    businessDetails.add(longitudeText);

                    Intent intent = new Intent(RegisterLocationActivity.this,RegisterAboutActivity.class);
                    intent.putStringArrayListExtra("businessDetails",businessDetails);
                    startActivity(intent);
                    finish();

                }else
                    return;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getAddressFromLocation() {

        geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {

            addresses = geocoder.getFromLocation(Double.valueOf(latitudeText),Double.valueOf(longitudeText),1);
            addressText = addresses.get(0).getAddressLine(0);

            Log.d("ADDRESS_CHECK","ADDRESS LINE IS " + addressText);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Boolean checkIfValid() {

        latitudeText = latitude.getText().toString();
        longitudeText = longitude.getText().toString();

        if(latitudeText.length() == 0){
            latitude.setError("This cannot be left empty!");
            latitude.requestFocus();
            return false;
        }

        if(longitudeText.length() == 0){
            longitude.setError("This cannot be left empty!");
            longitude.requestFocus();
            return false;
        }

        return true;

    }

    private void setupGps() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_GPS);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_GPS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            setupGps();
        }

    }



}

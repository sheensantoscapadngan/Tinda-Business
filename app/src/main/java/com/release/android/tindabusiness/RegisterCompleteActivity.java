package com.release.android.tindabusiness;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class RegisterCompleteActivity extends AppCompatActivity {

    private TextView email,next;
    private ArrayList<String> businessDetails;
    private DatabaseReference rootRef;
    private Uri pictureUri, permitUri;
    private StorageReference storageReference;
    private String pictureDownloadUrl,permitDownloadUrl;
    private static final int REQUEST_READ = 1;
    private DatabaseReference applicationRef;
    private ProgressDialog progressDialog;
    private int permitState = 0, pictureState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complete);

        //setup Progress Dialog//
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading application request...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        /////////////////////////

        setupViews();
        checkReadPermission();

    }

    private void checkReadPermission() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ);

        }else{

            uploadImages();
            uploadDataToDB();
            activateListeners();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_READ){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                uploadImages();
                uploadDataToDB();
                activateListeners();

            }
        }

    }

    private void uploadImages() {
        //upload Pictures to DB and get URL
        pictureUri = Uri.parse(businessDetails.get(8));
        permitUri = Uri.parse(businessDetails.get(9));

        final StorageReference pictureRef = storageReference.child("Business_pictures").child(pictureUri.getLastPathSegment());
        pictureRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pictureDownloadUrl = uri.toString();
                        applicationRef.child("Business_picture").setValue(pictureDownloadUrl);

                        if(permitState == 1)
                                progressDialog.dismiss();
                        else
                            pictureState = 1;

                    }
                });
            }
        });

        final StorageReference permitRef = storageReference.child("Business_permits").child(permitUri.getLastPathSegment());
        permitRef.putFile(permitUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                permitRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        permitDownloadUrl = uri.toString();
                        applicationRef.child("Business_permit").setValue(permitDownloadUrl);
                        progressDialog.dismiss();

                        if(pictureState == 1)
                                progressDialog.dismiss();
                        else
                            permitState = 1;

                    }
                });
            }
        });

    }

    private void uploadDataToDB() {

        applicationRef.child("Business_name").setValue(businessDetails.get(0));
        applicationRef.child("Business_address").setValue(businessDetails.get(1));
        applicationRef.child("Business_number").setValue(businessDetails.get(2));
        applicationRef.child("Business_email").setValue(businessDetails.get(3));
        applicationRef.child("Business_password").setValue(businessDetails.get(4));
        applicationRef.child("Business_latitude").setValue(businessDetails.get(5));
        applicationRef.child("Business_longitude").setValue(businessDetails.get(6));
        applicationRef.child("Business_description").setValue(businessDetails.get(7));


    }

    private void setupViews() {

        email = (TextView) findViewById(R.id.textViewRegisterCompleteEmail);
        next = (TextView) findViewById(R.id.textViewRegisterCompleteContinue);

        businessDetails = new ArrayList<>();
        businessDetails = getIntent().getStringArrayListExtra("businessDetails");

        rootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        applicationRef = rootRef.child("Business_Applications").push();

        email.setText(businessDetails.get(3));

    }

    private void activateListeners() {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}

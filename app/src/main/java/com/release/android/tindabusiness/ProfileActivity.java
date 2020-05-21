package com.release.android.tindabusiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private ImageView back,image,editPicture;
    private EditText name,number;
    private TextView save;
    private String nameText,numberText,imageText,originalImageText,genreText,downloadUrl;
    private FirebaseAuth firebaseAuth;
    private String uid;
    private DatabaseReference rootRef,profileRef;
    private StorageReference storageReference;
    private static final int REQUEST_GALLERY = 1;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupViews();
        initializeVariables();
        activateListeners();

    }

    private void initializeVariables() {

        nameText = getIntent().getStringExtra("nameText");
        numberText = getIntent().getStringExtra("numberText");
        imageText = getIntent().getStringExtra("imageText");
        originalImageText = getIntent().getStringExtra("imageText");
        genreText = getIntent().getStringExtra("genreText");

        name.setText(nameText);
        number.setText(numberText);
        Glide.with(this).load(imageText).into(image);

    }

    private void activateListeners() {

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nameText = name.getText().toString();
                numberText = number.getText().toString();

                Boolean isValid = validateInput();

                if(isValid){

                    saveInformationToDB();

                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_GALLERY);

            }
        });

    }

    private void saveInformationToDB() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving changes...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //this function is for saving name and number//
        profileRef = rootRef.child("Businesses").child(genreText).child(uid);
        profileRef.child("Business_name").setValue(nameText);
        profileRef.child("Business_number").setValue(numberText);

        saveNewImage();
    }

    private void saveNewImage() {

        //this is for uploading new profile picture and deleting old//

        if(!originalImageText.equals(imageText)){

            Uri pictureUri = Uri.parse(imageText);
            final StorageReference pictureRef = storageReference.child("Business_pictures").child(pictureUri.getLastPathSegment());
            pictureRef.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUrl = uri.toString();
                            profileRef.child("Business_picture").setValue(downloadUrl);

                            Intent intent = new Intent("finish_activity");
                            sendBroadcast(intent);

                            deletePreviousImageFromStorage();

                        }
                    });
                }
            });

        }else{

            progressDialog.dismiss();
            Intent intent = new Intent("finish_activity");
            sendBroadcast(intent);
            finish();
        }

    }

    private void deletePreviousImageFromStorage() {

        //this function is for deleting the previous picutre//
        StorageReference photoRef = firebaseStorage.getReferenceFromUrl(originalImageText);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                progressDialog.dismiss();
                finish();

            }
        });

    }

    private Boolean validateInput() {

        //this function is for checking if inputted data is valid//

        if(nameText.length() == 0){
            name.setError("This cannot be left blank");
            name.requestFocus();
            return false;
        }

        if(numberText.length() != 13){
            number.setError("Invalid contact number");
            number.requestFocus();
            return false;
        }

        return true;

    }

    private void setupViews() {

        back = (ImageView) findViewById(R.id.imageViewProfileBack);
        image = (ImageView) findViewById(R.id.imageViewProfileImage);
        name = (EditText) findViewById(R.id.editTextProfileName);
        number = (EditText) findViewById(R.id.editTextProfileNumber);
        save = (TextView) findViewById(R.id.textViewProfileSave);
        editPicture = (ImageView) findViewById(R.id.imageViewProfileEditPicture);

        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        uid = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_GALLERY){

            if(resultCode == RESULT_OK){

                imageText = data.getData().toString();
                Glide.with(this).load(imageText).into(image);

            }

        }

    }
}

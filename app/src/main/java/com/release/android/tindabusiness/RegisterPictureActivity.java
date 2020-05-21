package com.release.android.tindabusiness;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

public class RegisterPictureActivity extends AppCompatActivity {

    private ImageView back,picture;
    private TextView next;
    private ArrayList<String> businessDetails;
    private static final int REQUEST_GALLERY = 1;
    private Uri imageUri;
    private String pictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_picture);

        setupViews();
        activateListeners();
    }

    private void setupViews() {

        back = (ImageView) findViewById(R.id.imageViewRegisterPictureBack);
        picture = (ImageView) findViewById(R.id.imageViewRegisterPicturePicture);
        next = (TextView) findViewById(R.id.textViewRegisterPictureContinue);

        businessDetails = new ArrayList<>();
        businessDetails = getIntent().getStringArrayListExtra("businessDetails");

    }

    private void activateListeners() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean isValid = checkIfValid();

                if(isValid){

                    businessDetails.add(pictureUri);

                    Intent intent = new Intent(RegisterPictureActivity.this,RegisterPermitActivity.class);
                    Log.d("PICTURE_CHECK","URI IS " + pictureUri);
                    intent.putStringArrayListExtra("businessDetails",businessDetails);
                    startActivity(intent);
                    finish();

                }else
                    return;

            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_GALLERY);

            }
        });

    }

    private Boolean checkIfValid() {

        if(pictureUri.length() == 0){
            Toast.makeText(this, "Please select a picture", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_GALLERY){

            if(resultCode == RESULT_OK){

                imageUri = data.getData();
                pictureUri = imageUri.toString();
                picture.setImageURI(imageUri);

            }

        }

    }
}

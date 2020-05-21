package com.release.android.tindabusiness;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

public class RegisterPermitActivity extends AppCompatActivity {

    private ImageView picture,back;
    private TextView next;
    private static final int REQUEST_GALLERY = 1;
    private Uri imageUri;
    private String permitUri;
    private ArrayList<String> businessDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_permit);

        setupViews();
        activateListeners();
    }

    private void setupViews() {

        picture = (ImageView) findViewById(R.id.imageViewRegisterPermitPicture);
        back = (ImageView) findViewById(R.id.imageViewRegisterPermitBack);
        next = (TextView) findViewById(R.id.textViewRegisterPermitContinue);

        businessDetails = new ArrayList<>();
        businessDetails = getIntent().getStringArrayListExtra("businessDetails");

    }

    private void activateListeners() {

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_GALLERY);

            }
        });

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

                    businessDetails.add(permitUri);

                    Intent intent = new Intent(RegisterPermitActivity.this,RegisterCompleteActivity.class);
                    intent.putStringArrayListExtra("businessDetails",businessDetails);
                    startActivity(intent);
                    finish();

                }else{
                    return;
                }

            }
        });

    }

    private Boolean checkIfValid() {

        if(permitUri.length() == 0){
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
                picture.setImageURI(imageUri);
                permitUri = imageUri.toString();

            }
        }

    }
}

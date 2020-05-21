package com.release.android.tindabusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

public class RegisterAboutActivity extends AppCompatActivity {

    private ImageView back;
    private EditText about;
    private TextView next,count;
    private ArrayList<String> businessDetails;
    private String aboutText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_about);

        setupViews();
        activateListeners();

    }

    private void setupViews() {

        back = (ImageView) findViewById(R.id.imageViewRegisterAboutBack);
        about = (EditText) findViewById(R.id.editTextRegisterAboutAbout);
        next = (TextView) findViewById(R.id.textViewRegisterAboutContinue);
        count = (TextView) findViewById(R.id.textViewRegisterAboutCount);

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

                aboutText = about.getText().toString();
                Boolean isValid = checkifValid();

                if(isValid){

                    businessDetails.add(aboutText);

                    Intent intent = new Intent(RegisterAboutActivity.this,RegisterPictureActivity.class);
                    intent.putStringArrayListExtra("businessDetails",businessDetails);
                    startActivity(intent);
                    finish();

                }else
                    return;

            }
        });

        about.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                count.setText("(" + editable.toString().length() + "/100)");
            }
        });


    }

    private Boolean checkifValid() {

        if(aboutText.length() > 100){
            about.setError("Description should not exceed 100 characters");
            about.requestFocus();
            return false;
        }

        return true;
    }


}

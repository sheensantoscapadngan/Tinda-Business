package com.release.android.tindabusiness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

public class RegisterProfileActivity extends AppCompatActivity {

    private EditText name,number,email,password;
    private TextView next;
    private ImageView back;
    private String nameText,numberText,emailText,passwordText,addressText;
    private ArrayList<String> businessDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile);

        setupViews();
        activateListeners();
    }

    private void setupViews() {

        name = (EditText) findViewById(R.id.editTextRegisterProfileName);
        number = (EditText) findViewById(R.id.editTextRegisterProfileNumber);
        email = (EditText) findViewById(R.id.editTextRegisterProfileEmail);
        password = (EditText) findViewById(R.id.editTextRegisterProfilePassword);
        back = (ImageView) findViewById(R.id.imageViewRegisterProfileBack);
        next = (TextView) findViewById(R.id.textViewRegisterProfileContinue);

        businessDetails = new ArrayList<>();

    }

    private void activateListeners() {

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getVariables();

                Boolean isValid = verifyVariables();
                if(isValid){

                    Intent intent = new Intent(RegisterProfileActivity.this,RegisterLocationActivity.class);
                    businessDetails.add(nameText);
                    businessDetails.add(addressText);
                    businessDetails.add("+63" + numberText);
                    businessDetails.add(emailText);
                    businessDetails.add(passwordText);
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

    private Boolean verifyVariables() {

        if(emailText.length() == 0 ||
                !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
            email.setError("Invalid email");
            email.requestFocus();
            return false;
        }

        if(numberText.length() != 10 ||
                !numberText.matches("[0-9]+")){
            number.setError("Invalid number");
            number.requestFocus();
            return false;
        }


        if(passwordText.length() < 6){
            password.setError("Invalid password");
            password.requestFocus();
            return false;
        }

        return true;

    }

    private void getVariables() {

        nameText = name.getText().toString();
        numberText = number.getText().toString();
        addressText = "";
        emailText = email.getText().toString();
        passwordText = password.getText().toString();

    }

}

package com.release.android.tindabusiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.release.android.tindabusiness.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ImageView add,signin,back;
    private ConstraintLayout loginScreen;
    private TextView login;
    private EditText email,password;
    private String emailText,passwordText;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();
        activateListeners();
    }

    private void setupViews() {

        add = (ImageView) findViewById(R.id.imageViewLoginAddBusiness);
        signin = (ImageView) findViewById(R.id.imageViewLoginSignin);
        loginScreen = (ConstraintLayout) findViewById(R.id.constraintLayoutLogin);
        back = (ImageView) findViewById(R.id.imageViewLoginBack);
        email = (EditText) findViewById(R.id.editTextLoginEmail);
        password = (EditText) findViewById(R.id.editTextLoginPassword);
        login = (TextView) findViewById(R.id.textViewLoginContinue);

        progressDialog = new ProgressDialog(this);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void activateListeners() {

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this,RegisterProfileActivity.class);
                startActivity(intent);

            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginScreen.setVisibility(View.VISIBLE);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginScreen.setVisibility(View.GONE);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailText = email.getText().toString();
                passwordText = password.getText().toString();

                Boolean isValid = validateInput();
                if(isValid) {

                    progressDialog.setMessage("Logging in...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    loginUser();
                }else
                    return;


            }
        });

    }

    private Boolean validateInput() {

        if(emailText.length() == 0){
            email.setError("This cannot be left blank");
            email.requestFocus();
            return false;
        }

        if(passwordText.length() == 0){
            password.setError("This cannot be left blank");
            password.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser() {

        //this function is for logging in user//
        firebaseAuth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    finish();

                }else{
                    Toast.makeText(LoginActivity.this, "Invalid username / password!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });


    }


}

package com.example.bookclubapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySignUp extends AppCompatActivity {
    EditText etUser, etPass, etRepass;
    Button btnRegister, btnGoToLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUser = findViewById(R.id.userName);
        etPass = findViewById(R.id.password);
        etRepass = findViewById(R.id.retypePassword);

        btnRegister = findViewById(R.id.regButton);

        dbHelper = new DBHelper(this);

        btnGoToLogin = findViewById(R.id.goToLogin);
        btnGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySignUp.this, MainActivity.class);
            startActivity(intent);
        });
        btnRegister.setOnClickListener(view -> {
            String user, pass, repass;
            user = etUser.getText().toString();
            pass = etPass.getText().toString();
            repass = etRepass.getText().toString();
            //Checks if fields are empty
            if (user.isEmpty() || pass.isEmpty() || repass.isEmpty() ){
                Toast.makeText(ActivitySignUp.this, "Please fill all the fields.", Toast.LENGTH_LONG).show();
            } else{
                //Checks if passwords are matching
                if(pass.equals(repass)) {
                    //Checks the database to see if the user already exists
                    if (dbHelper.checkUserName(user)){
                        Toast.makeText(ActivitySignUp.this, "User already exists", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Continues with registration if both password and retype password fields are matching.
                    boolean registeredSuccess = dbHelper.insertData(user,pass);
                    if (registeredSuccess) {
                        Toast.makeText(ActivitySignUp.this, "User registered Successfully", Toast.LENGTH_LONG).show();
                        //Ends the activity and takes the user back to the login page.
                        finish();
                    }
                    else {
                        Toast.makeText(ActivitySignUp.this, "User registration Failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ActivitySignUp.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
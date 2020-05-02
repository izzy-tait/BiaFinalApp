package com.example.bia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class registrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailReg, passwordReg, confirmPassword;
    private Button regBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        emailReg=findViewById(R.id.editText);
        passwordReg=findViewById(R.id.editText2);
        confirmPassword=findViewById(R.id.editText3);

        regBtn=findViewById(R.id.registerBtn);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser(){
        //progressBar.setVisibility(View.VISIBLE);

        String email, password, passwordConfirmation;
        email = emailReg.getText().toString();
        password = passwordReg.getText().toString();
        passwordConfirmation= confirmPassword.getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passwordConfirmation)) {
            Toast.makeText(getApplicationContext(), "Please confirm password.", Toast.LENGTH_LONG).show();
            return;
        }
        if(!passwordConfirmation.equals(password)){
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(registrationActivity.this, loginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            //FirebaseAuthException e = (FirebaseAuthException )task.getException();
                            //Log.e("LoginActivity", "Failed Registration", e);
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }


}

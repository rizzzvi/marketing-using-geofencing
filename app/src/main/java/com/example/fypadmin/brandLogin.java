package com.example.fypadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class brandLogin extends AppCompatActivity {

    private  Button btnlogin,btnSignup;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_login);
        auth = FirebaseAuth.getInstance();
        inputEmail = findViewById(R.id.email);
         inputPassword = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btn_login);
        btnSignup = findViewById(R.id.button_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(brandLogin.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //AUTHENTICATE USER
                auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(brandLogin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                              if(!task.isSuccessful()){
                                  Toast.makeText(brandLogin.this,"Wrong email or password",Toast.LENGTH_LONG).show();
                              }
                              else {
                                  Intent intent = new Intent(brandLogin.this,brandMainScreen.class);
                                  startActivity(intent);
                              }
                            }

                        });

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(this,brandMainScreen.class);
            startActivity(intent);
        }
    }
}

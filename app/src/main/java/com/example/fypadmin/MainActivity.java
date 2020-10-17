package com.example.fypadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextPassword, editTextBrand;
    private FirebaseAuth mAuth;
    Button btnRegister,btnSignup;
//    private Uri filePath;
//    private FirebaseStorage firebaseStorage;
//    private StorageReference storageReference;
    HashMap<String,String> brands = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.Nametxt);
        editTextEmail = findViewById(R.id.Emailtxt);
        editTextPassword = findViewById(R.id.Passwordtxt);
        editTextBrand = findViewById(R.id.BrandNametxt);
        btnRegister = findViewById(R.id.button_register);
        btnSignup = findViewById(R.id.button_signup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,brandLogin.class);
                startActivity(intent);
            }
        });
//        firebaseStorage = FirebaseStorage.getInstance();
//        storageReference = firebaseStorage.getReference();
//        btnImgchoose = findViewById(R.id.btn_image_choose);
//        btnImgupload = findViewById(R.id.btn_image_upload);
        mAuth = FirebaseAuth.getInstance();
//        btnImgchoose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        btnImgupload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadImage();
//            }
//        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

//    private void uploadImage() {
//        if(filePath!=null){
//            StorageReference reference = storageReference.child("images/"+FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
//
//        }
//    }

//    private void chooseImage() {
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//         if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null){
//             filePath = data.getData();
//             try {
//                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
//
//             } catch (IOException e) {
//                 e.printStackTrace();
//             }
//         }
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (mAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(this,brandMainScreen.class);
//            startActivity(intent);
//        }
//    }
    private void registerUser(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String brand = editTextBrand.getText().toString().trim();
        if (name.isEmpty()) {
            editTextName.setError("Name field is required");
            editTextName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Some error in the email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 4) {
            editTextPassword.setError("Minimum password length must be 4");
            editTextPassword.requestFocus();
            return;
        }
        if (brand.isEmpty()) {
            editTextBrand.setError("Brand name is required");
            editTextBrand.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            brands.put("Name",name);
                            brands.put("Email",email);
                            brands.put("Brand Name",brand);

                            FirebaseDatabase.getInstance().getReference("BRANDS")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(brands).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MainActivity.this,brandLogin.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this,"Failure",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                    }
                        else {
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

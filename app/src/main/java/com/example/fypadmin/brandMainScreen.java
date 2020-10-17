package com.example.fypadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class brandMainScreen extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    HashMap<String, Object> uploadImage = new HashMap<>();
    FirebaseAuth auth;
    private DrawerLayout drawerBrand;
    private ImageView mImageView;
    private TextView brandNameTxt,emailTxtHeader;
    private Uri mImageUri;
    private String imageUriDb, downloadUrlString,brandName,userMail;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_main_screen);
        Toolbar toolbar = findViewById(R.id.toolbarBrand);
        setSupportActionBar(toolbar);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragment_container, new homeFragmentBrand());
        tx.commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mImageView = headerView.findViewById(R.id.imgUpload);
        brandNameTxt = headerView.findViewById(R.id.nav_brand_header_brandName);
        emailTxtHeader = headerView.findViewById(R.id.nav_brand_header_email);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        Toast.makeText(this, "User logged in successfully", Toast.LENGTH_LONG).show();
        drawerBrand = findViewById(R.id.drawer_layout_brand);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerBrand, toolbar,
                R.string.navigation_drawer_brand_open, R.string.navigation_drawer_brand_close);
        drawerBrand.addDrawerListener(toggle);
        toggle.syncState();
        retrieveNavHeaderData();

    }

    @Override
    public void onBackPressed() {
        if (drawerBrand.isDrawerOpen(GravityCompat.START)) {
            drawerBrand.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new homeFragmentBrand()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new profileFragmentBrand()).commit();
                break;
            case R.id.nav_products:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new productsFragmentBrand()).commit();
                break;
            case R.id.nav_offers:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new offersFragmentBrand()).commit();
                break;
            case R.id.nav_productlistings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new productListingBrand()).commit();
                break;
            case R.id.nav_changepassword:
                Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent backToLoginIntent = new Intent(brandMainScreen.this, brandLogin.class);
                                startActivity(backToLoginIntent);
                            }
                        });

                break;
        }
        drawerBrand.closeDrawer(GravityCompat.START);

        return true;
    }


    public void openFileChooser(View view) {

        Intent chooseImageintent = new Intent();
        chooseImageintent.setType("image/*");
        chooseImageintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageintent, PICK_IMAGE_REQUEST);

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//                uploadFile();
//            }
//        }, 6000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Condition", "If condition is not running");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Log.i("Condition", "If condition is running");
            mImageUri = data.getData();
            Log.i("Condition", mImageUri.toString());
//            mImageView.setImageURI(mImageUri);
            Picasso.with(this).load(mImageUri).noFade().into(mImageView);
            Log.i("Condition", "Picasso is working");
            Toast.makeText(brandMainScreen.this, "Image URI picked", Toast.LENGTH_LONG).show();
        }
        if(mImageUri!=null){
            uploadFile();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
             .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Toast.makeText(brandMainScreen.this, "Upload successful", Toast.LENGTH_LONG).show();
//                     final String downloadUrl = taskSnapshot.;
                     Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                     while (!urlTask.isSuccessful());
                     Uri downloadUrl = urlTask.getResult();
                     downloadUrlString = downloadUrl.toString();
                     uploadImage.put("ProfileImage", downloadUrlString);
                     FirebaseDatabase.getInstance().getReference("BRANDS")
                             .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                             .updateChildren(uploadImage)
                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()) {
                                         Toast.makeText(brandMainScreen.this, downloadUrlString, Toast.LENGTH_LONG).show();

                                     } else {
                                         Toast.makeText(brandMainScreen.this, "Upload Failed", Toast.LENGTH_LONG).show();

                                     }
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(brandMainScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                         }
                     });

                 }
             });
        }
    }


    private void retrieveNavHeaderData() {
        FirebaseDatabase.getInstance().getReference("BRANDS")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ProfileImage")) {
                            FirebaseDatabase.getInstance().getReference("BRANDS")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            imageUriDb = dataSnapshot.child("ProfileImage").getValue().toString();
                                            brandName = dataSnapshot.child("Brand Name").getValue().toString();
                                            userMail = dataSnapshot.child("Email").getValue().toString();

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (imageUriDb != null) {
                                                        Toast.makeText(brandMainScreen.this, "Image updating", Toast.LENGTH_LONG).show();
                                                        Glide.with(brandMainScreen.this).load(imageUriDb).into(mImageView);
                                                        brandNameTxt.setText(brandName);
                                                        emailTxtHeader.setText(userMail);
                                                    }
                                                }
                                            }, 2000);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                        else {
                            Toast.makeText(brandMainScreen.this,"Image does not exist",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


}

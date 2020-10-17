package com.example.fypadmin;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class productsFragmentBrand extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    Button btnAddProduct, btnAddImage;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String strProductName, strProductDescription, strProductPrice,
            ProductImageURL;
    DatabaseReference dbref;
    StorageReference refStorage;
    HashMap<String, String> Products = new HashMap<>();
    private EditText productName, productDescription, productPrice;
    private Uri productImageUri;
    private StorageTask mUploadTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.products_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbref = firebaseDatabase.getReference("BRANDS");
        refStorage = FirebaseStorage.getInstance().getReference("ProductImages");
        productDescription = getView().findViewById(R.id.product_description);
        productName = getView().findViewById(R.id.product_name);
        productPrice = getView().findViewById(R.id.product_price);
        btnAddProduct = getView().findViewById(R.id.add_product);
        btnAddImage = getView().findViewById(R.id.add_image);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productImageUri != null) {
                    uploadProduct();
                } else {
                    Toast.makeText(getContext(), "Product Image is required", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void validateProductInputs() {
        strProductName = productName.getText().toString();
        strProductDescription = productDescription.getText().toString();
        strProductPrice = (productPrice.getText().toString() + "pkr");
        if (strProductName.isEmpty()) {
            productName.setError("Product Name is required");
            productName.requestFocus();
            return;
        }
        if (strProductDescription.isEmpty()) {
            productDescription.setError("Product Description is required");
            productDescription.requestFocus();
            return;
        }
        if (strProductPrice.isEmpty()) {
            productPrice.setError("Product Price is required");
            productPrice.requestFocus();
            return;
        }


    }

    private void uploadProduct() {
        validateProductInputs();
        final StorageReference fileReference = refStorage.child(System.currentTimeMillis()
                + "." + getFileExtension(productImageUri));
        mUploadTask = fileReference.putFile(productImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!urlTask.isSuccessful()) ;
                        Uri downloadUrl = urlTask.getResult();
                        ProductImageURL = downloadUrl.toString();
                        Products.put("ProductName", strProductName);
                        Products.put("ProductDescription", strProductDescription);
                        Products.put("ProductPrice", strProductPrice);
                        Products.put("ProductImage", ProductImageURL);
                        dbref.child(userID).child("Products").child(dbref.push().getKey()).setValue(Products)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Product Added Successfully", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(getContext(), "Product Upload Failed", Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openFileChooser() {

        Intent chooseImageintent = new Intent();
        chooseImageintent.setType("image/*");
        chooseImageintent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseImageintent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Log.i("Condition", "If condition is running");
            productImageUri = data.getData();
//            Log.i("Condition", mImageUri.toString());
//            mImageView.setImageURI(mImageUri);
            Log.i("Condition", "Picasso is working");
            Toast.makeText(getContext(), "Image URI picked", Toast.LENGTH_LONG).show();
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}

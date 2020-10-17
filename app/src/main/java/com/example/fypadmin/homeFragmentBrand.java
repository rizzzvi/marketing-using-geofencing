package com.example.fypadmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class homeFragmentBrand extends Fragment {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth;
    TextView name,brandname;
    String storeName, storeBrandName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name = getView().findViewById(R.id.get_name);
        brandname = getView().findViewById(R.id.get_brand_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveUserData();
    }

    private void retrieveUserData() {
         FirebaseDatabase.getInstance().getReference("BRANDS")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        storeName = dataSnapshot.child("Name").getValue().toString();
                        storeBrandName = dataSnapshot.child("Brand Name").getValue().toString();
                        name.setText(storeName);
                        brandname.setText(storeBrandName);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}

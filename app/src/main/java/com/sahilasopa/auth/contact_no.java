package com.sahilasopa.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sahilasopa.auth.databinding.ActivityContactNoBinding;

import java.util.Objects;

public class contact_no extends AppCompatActivity {
    ActivityContactNoBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityContactNoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        binding.buttonSendOtp.setOnClickListener(v -> {
            if (binding.contactNo.getText().toString().isEmpty() || binding.username.getText().toString().isEmpty()) {
                if (binding.username.getText().toString().isEmpty()) {
                    binding.username.setError("Please Enter A Username");
                    binding.username.requestFocus();
                } else if (binding.contactNo.getText().toString().isEmpty()) {
                    binding.contactNo.setError("Please Enter A Valid Phone No");
                    binding.contactNo.requestFocus();
                }
                return;
            }
            String countryCode = binding.ccp.getSelectedCountryCode();
            String phone_no = binding.contactNo.getText().toString();
            String contactNo = "+".concat(countryCode).concat(" " + phone_no);
            Intent otp = new Intent(this, verify_otp.class);
            otp.putExtra("contact_no", contactNo);
            otp.putExtra("username", binding.username.getText().toString());
            startActivity(otp);
        });
    }
}
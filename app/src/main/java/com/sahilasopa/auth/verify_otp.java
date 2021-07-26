package com.sahilasopa.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.sahilasopa.auth.databinding.ActivityVerifyOtpBinding;
import com.sahilasopa.auth.models.Users;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class verify_otp extends AppCompatActivity {
    private String verificationId;
    String contact_no;
    FirebaseDatabase database;
    ActivityVerifyOtpBinding binding;
    PhoneAuthProvider.ForceResendingToken resendToken;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        contact_no = getIntent().getExtras().get("contact_no").toString();
        sendVerificationCode(contact_no);
        binding.button.setOnClickListener(v -> {
            String code = binding.otp.getText().toString();
            if (code.isEmpty() || code.length() < 6) {
                binding.otp.setError("Enter A Valid Code");
                binding.otp.requestFocus();
                return;
            }
            verifyCode(code);
        });
        binding.textView4.setOnClickListener(v -> {
            sendVerificationCode(contact_no);
            Toast.makeText(this, "Verification Code Sent", Toast.LENGTH_SHORT).show();
        });
    }

    public void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Users users = new Users();
                users.setContact_no(contact_no);
                users.setId(Objects.requireNonNull(task.getResult().getUser()).getUid());
                String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                database.getReference().child("Users").child(id).setValue(users);
                Intent main = new Intent(this, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(main);
            } else {
                Log.v("response", Objects.requireNonNull(task.getException()).toString());
                if (task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException")){
                    Toast.makeText(this, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendVerificationCode(String contact_no) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(contact_no)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            resendToken = forceResendingToken;
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.v("response", e.getMessage());
        }
    };
}
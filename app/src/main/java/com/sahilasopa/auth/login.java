package com.sahilasopa.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.sahilasopa.auth.databinding.ActivityLoginBinding;
import com.sahilasopa.auth.models.Users;

import java.util.Objects;

public class login extends AppCompatActivity {
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        database = FirebaseDatabase.getInstance();
        progressDialog.setMessage("Login to your account");
        Intent intent = new Intent(this, MainActivity.class);
        Intent register = new Intent(this, Register.class);
        binding.buttonSignup.setOnClickListener(v -> {
            if (binding.email.getText().toString().isEmpty() || binding.password.getText().toString().isEmpty()) {
                if (binding.email.getText().toString().isEmpty()) {
                    binding.email.setError("This Field Is Required");
                    binding.email.requestFocus();
                } else if (binding.password.getText().toString().isEmpty()) {
                    binding.password.setError("This Field Is Required");
                    binding.password.requestFocus();
                }
                return;
            }
            progressDialog.show();
            auth.signInWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.buttonGoogle.setOnClickListener(v -> signIn());
        binding.buttonContactNo.setOnClickListener(v -> {
            Intent phone = new Intent(this, contact_no.class);
            startActivity(phone);
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.textRegister.setOnClickListener(v -> startActivity(register));
        if ((auth.getCurrentUser() != null)) {
            startActivity(intent);
        }
    }

    int RC_SIGN_IN = 69;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Intent intent = new Intent(this, MainActivity.class);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Users users = new Users();
                        assert user != null;
                        users.setId(user.getUid());
                        users.setProfile_pic(Objects.requireNonNull(user.getPhotoUrl()).toString());
                        users.setUsername(user.getDisplayName());
                        database.getReference().child("Users").child(user.getUid()).setValue(users);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }
}
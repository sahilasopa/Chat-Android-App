package com.sahilasopa.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.sahilasopa.auth.databinding.ActivityRegisterBinding;
import com.sahilasopa.auth.models.Users;

import java.util.Objects;

public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
    ActivityRegisterBinding binding;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        isOnline();
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, MainActivity.class);
        Intent login = new Intent(this, login.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("we're creating your account");
        binding.buttonSignIn.setOnClickListener(v -> {
            if (binding.email.getText().toString().isEmpty() || binding.password.getText().toString().isEmpty() || binding.username.getText().toString().isEmpty()) {
                if (binding.email.getText().toString().isEmpty()) {
                    binding.email.setError("This Field Is Required");
                    binding.email.requestFocus();
                } else if (binding.password.getText().toString().isEmpty()) {
                    binding.password.setError("This Field Is Required");
                    binding.password.requestFocus();
                } else if (binding.username.getText().toString().isEmpty()) {
                    binding.username.setError("This Field Is Required");
                    binding.username.requestFocus();
                }
                return;
            }
            progressDialog.show();
            auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString()).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Users user = new Users(binding.username.getText().toString(), binding.email.getText().toString(), binding.password.getText().toString());
                    String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    user.setId(id);
                    database.getReference().child("Users").child(id).setValue(user);
                    Toast.makeText(Register.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Register.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.buttonGoogle.setOnClickListener(v -> signIn());
        binding.alreadyHaveAccountText.setOnClickListener(v -> startActivity(login));
        binding.buttonMobileNo.setOnClickListener(v -> {
            Intent phone = new Intent(this, contact_no.class);
            startActivity(phone);
        });
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.alreadyHaveAccountText.setOnClickListener(v -> startActivity(login));
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
                Toast.makeText(this, "Google Sign in failed", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Register.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()) {
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
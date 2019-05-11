package com.technofreak.bdtextbooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private EditText emailText,passwordText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailText = findViewById(R.id.editText_email);
        passwordText = findViewById(R.id.editText_password);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            finish();
            Intent intent = new Intent(MainActivity.this,BookActivity.class);
            startActivity(intent);
        }
    }

    public void Login(View view) {
        loginUser();
    }

    private void loginUser() {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        if (email.isEmpty()){
            emailText.setError("Email is required");
            emailText.requestFocus();
        } else if (password.isEmpty()){
            passwordText.setError("Password is required");
            passwordText.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Intent intent = new Intent(MainActivity.this,BookActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();  //to not show login screen again
                    } else{
                        Toast.makeText(getApplicationContext(),"Invalid user information",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void Signup(View view) {
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        if (email.isEmpty()){
            emailText.setError("Email is required");
            emailText.requestFocus();
        } else if (password.isEmpty()){
            passwordText.setError("Password is required");
            passwordText.requestFocus();
        } else{
            progressBar.setVisibility(View.VISIBLE);
            int end = email.indexOf('@');
            final String userName=email.substring(0,end);
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        updateUserProfile(userName);
                        Toast.makeText(getApplicationContext(),"Signed up successfully",Toast.LENGTH_SHORT).show();
                        loginUser();
                    }
                    else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(),"You are already registered",Toast.LENGTH_SHORT).show();
                        }else Toast.makeText(getApplicationContext(),"Error! Sign up failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void updateUserProfile(String userName) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();
        if (user != null) {
            user.updateProfile(profileUpdate);
        }
    }

}

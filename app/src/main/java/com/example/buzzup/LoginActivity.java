package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ProgressBar progressBar;
    TextView textView;
    final String TAG = "LoginActivity";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            db.collection("user").document(currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    boolean userIsAdmin = Boolean.parseBoolean(document.getData().get("is_admin").toString());
                                    if (userIsAdmin) {
                                        //            check if current user is admin
                                        Toast.makeText(LoginActivity.this, "Admin User signed in", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),AdminEventsList.class);
                                        startActivity(intent);
                                    } else {
                                        // regular user
                                        Toast.makeText(LoginActivity.this, "Regular User signed in", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(),EventsList.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            }
                        }
                    });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login success",
                                            Toast.LENGTH_SHORT).show();

//                                    check whether admin or user is logging in...
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if(currentUser != null){
                                        db.collection("user").document(currentUser.getEmail())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                boolean userIsAdmin = Boolean.parseBoolean(document.getData().get("is_admin").toString());
                                                                if (userIsAdmin) {
                                                                    //            check if current user is admin
                                                                    Toast.makeText(LoginActivity.this, "Admin User signed in", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(),AdminEventsList.class);
                                                                    startActivity(intent);
                                                                } else {
                                                                    // regular user
                                                                    Toast.makeText(LoginActivity.this, "Regular User signed in", Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(getApplicationContext(),EventsList.class);
                                                                    startActivity(intent);
                                                                }
                                                                finish();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }
}
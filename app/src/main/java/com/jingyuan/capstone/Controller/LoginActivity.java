package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.jingyuan.capstone.DTO.Firebase.UserDTO;
import com.jingyuan.capstone.R;

public class LoginActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public EditText usernameInputField;
    public EditText passwordInputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInputField = findViewById(R.id.user);
        passwordInputField = findViewById(R.id.password);
        Button signInBtn = findViewById(R.id.signInBtn);
        Button signUpSuggestBtn = findViewById(R.id.signUpSuggestText);
        signInBtn.setOnClickListener(LoginActivity.this::onSignInBtnClick);
        signUpSuggestBtn.setOnClickListener(LoginActivity.this::setUpSignUpScreen);
    }

    @Override
    public void onStart() {
        super.onStart();
//        testUpdateUI();
        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
        startActivity(i);
        finish();
    }

    public void onSignUpBtnClick(View v) {
        String username = usernameInputField.getText().toString();
        String password = passwordInputField.getText().toString();
        createAccount(username, password);
    }

    public void onSignInBtnClick(View v) {
        String username = usernameInputField.getText().toString();
        String password = passwordInputField.getText().toString();
        signIn(username, password);
    }

    public void setUpSignUpScreen(View v) {
        setContentView(R.layout.activity_signup);
        usernameInputField = findViewById(R.id.user);
        passwordInputField = findViewById(R.id.password);
        Button signUpBtn = findViewById(R.id.signUpBtn);
        Button signInSuggestBtn = findViewById(R.id.signInSuggestText);
        signUpBtn.setOnClickListener(LoginActivity.this::onSignUpBtnClick);
        signInSuggestBtn.setOnClickListener(LoginActivity.this::setUpLogInScreen);
    }

    public void setUpLogInScreen(View v) {
        setContentView(R.layout.activity_login);
        usernameInputField = findViewById(R.id.user);
        passwordInputField = findViewById(R.id.password);
        Button signInBtn = findViewById(R.id.signInBtn);
        Button signUpSuggestBtn = findViewById(R.id.signUpSuggestText);
        signInBtn.setOnClickListener(LoginActivity.this::onSignInBtnClick);
        signUpSuggestBtn.setOnClickListener(LoginActivity.this::setUpSignUpScreen);
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Account created successfully.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            updateUI(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Sign in successfully.",
                                Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snap = task.getResult();
                UserDTO userDTO = snap.toObject(UserDTO.class);
                assert userDTO != null;

                SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sf.edit();
                editor.putString("username", userDTO.getUsername());
                editor.putString("email", userDTO.getEmail());
                editor.putString("pfp", userDTO.getPfp());
                editor.apply();

                startActivity(i);
                finish();
            }
        });
    }

    private void testUpdateUI() {
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("username", "Firefly");
        editor.putString("email", "firefly@gmail.com");
        editor.putString("pfp", "https://firebasestorage.googleapis.com/v0/b/capstone-c62ee.appspot.com/o/sam.jpg?alt=media");
        editor.apply();
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }
}
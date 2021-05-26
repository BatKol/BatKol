package com.example.batkol;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName, userEmail, userPassword,userPhone,id;
    private Button register;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    User userObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth fireBaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        setupUIviews();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();



    }

    private void setupUIviews() {
        userName = (EditText) findViewById(R.id.edUserName);
        userEmail = (EditText) findViewById(R.id.edUserEmail);
        userPassword = (EditText) findViewById(R.id.edUserPassword);
        userPhone = (EditText)findViewById(R.id.editTextPhone);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this::onClick);
    }

    private boolean validate() {
        boolean ans = false;
        String name = userName.getText().toString();
        String Email = userEmail.getText().toString();
        String Password = userPassword.getText().toString();
        String Phone = userPhone.getText().toString();

        if (name.isEmpty() || Email.isEmpty() || Password.isEmpty() || Phone.isEmpty()) {
            Toast.makeText(this, "please enter all the details", Toast.LENGTH_LONG).show();
        } else {
            ans = true;
        }
        return ans;
    }

    @Override
    public void onClick(View v) {
        if (v == register){
            registerMe();

        }
    }

    private void registerMe() {
        if(!validate()) {
            Toast.makeText(RegisterActivity.this,"pls fill the form correctly",Toast.LENGTH_LONG).show();
            return;
        }
        System.out.println("starting...");
        String user_email = userEmail.getText().toString().trim();
        String user_password = userPassword.getText().toString().trim();
        System.out.println(user_email+" "+user_password);
        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("create user", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "welcome ."+ user,
                                    Toast.LENGTH_LONG).show();//
                            if (user!=null) {
                                finish();
                                updateUserInformation(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("fail create", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void updateUserInformation(FirebaseUser firebaseUser) {
        userObject = new User(firebaseUser.getUid(),userName.getText().toString(),userPhone.getText().toString(), userEmail.getText().toString());

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName.getText().toString())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("user update", "User profile updated.");
                        }
                    }
                });
        db.collection("Users").document(firebaseUser.getUid())
                .set(userObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });

    }
    @Override
    public void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            startActivity(new  Intent(RegisterActivity.this,Main_flow.class));

        }
    }
}
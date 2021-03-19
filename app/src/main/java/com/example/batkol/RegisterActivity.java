package com.example.batkol;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName, userEmail, userPassword,userPhone,id;
    private Button register;





    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupUIviews();
        register.setOnClickListener(new View.OnClickListener() {
            private FirebaseAuth fireBaseAuth = FirebaseAuth.getInstance();
            @Override
            public void onClick(View view) {
                if (validate()) {
                    String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();
                    fireBaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"register successful",Toast.LENGTH_LONG).show();

                                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                            }else{
                                Toast.makeText(RegisterActivity.this,"register failed",Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }
            }


        });

    }

    private void setupUIviews() {
        userName = (EditText) findViewById(R.id.edUserName);
        userEmail = (EditText) findViewById(R.id.edUserEmail);
        userPassword = (EditText) findViewById(R.id.edUserPassword);
        userPhone = (EditText)findViewById(R.id.editTextPhone);
        register = (Button) findViewById(R.id.register);
    }

    private Boolean validate() {
        Boolean ans = false;
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
}
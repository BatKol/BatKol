package com.example.batkol;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnLogin;
    Button btnRegister;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ExistUserEmail);
        password = findViewById(R.id.ExistUserPassword);
        btnLogin = findViewById(R.id.Login);
        btnRegister = findViewById(R.id.buttonRegister);



        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebseAuth ){
                FirebaseUser mFirebaseUser =mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null){
                    Toast.makeText(MainActivity.this,"ברוך שובך",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent (MainActivity.this, BatKolHomeActivity.class));
                }
                else{
                    Toast.makeText(MainActivity.this,"בבקשה תרשם",Toast.LENGTH_SHORT).show();


                }

            }
        };
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String email=emailId.getText().toString();
                String pwd= password.getText().toString();
                if(email.isEmpty() ){
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();

                }
                else if (email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(MainActivity.this,"לא מילאת את השדות", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"שגיאת התחברות,תתחבר שוב", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                startActivity(new  Intent(MainActivity.this,Main_flow.class));
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this,"משתמש שגוי", Toast.LENGTH_SHORT).show();

                }
            }



        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new  Intent(MainActivity.this,RegisterActivity.class));
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        if(mFirebaseAuth.getCurrentUser()!=null){
            startActivity(new  Intent(MainActivity.this,Main_flow.class));

        }
    }


}




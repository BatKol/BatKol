package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity
{
    EditText et_otp;
    Button btn_verify;
    String otp;
    private FirebaseAuth FAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        et_otp = (EditText)findViewById(R.id.otp_Input);
        btn_verify = (Button)findViewById(R.id.Btn_verify);
        otp = getIntent().getStringExtra("verify_code");
        FAuth = FirebaseAuth.getInstance();


        btn_verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String verification_code = et_otp.getText().toString().trim();
                if(!verification_code.isEmpty())
                {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otp, verification_code);
                    signIn(credential);
                }
                else
                    Toast.makeText(OtpActivity.this, "Please Enter Your Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = FAuth.getCurrentUser();
        if(currentUser != null)
            sendToMain();

    }

    private void sendToMain()
    {
        startActivity(new Intent(OtpActivity.this, MainActivity.class));
        finish();
    }

    private void signIn(PhoneAuthCredential credential)
    {
        FAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                    sendToMain();
                else
                    Toast.makeText(OtpActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
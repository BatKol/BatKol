package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity
{
    private EditText et_countryCode, et_phoneNumber;
    private TextView tv_validation_text;
    private Button btn_sendCode;
    private FirebaseAuth FAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_countryCode = (EditText)findViewById(R.id.countryCode_Input);
        et_phoneNumber = (EditText)findViewById(R.id.phoneNumber_Input);
        tv_validation_text = (TextView)findViewById(R.id.validtion_text);
        btn_sendCode = (Button)findViewById(R.id.Btn_sendCode);
        FAuth = FirebaseAuth.getInstance();


        btn_sendCode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tv_validation_text.setVisibility(View.GONE);
                String country_code = et_countryCode.getText().toString().trim();
                String phone = et_phoneNumber.getText().toString().trim();
                String phoneNumber = "+" + country_code + "" + phone;

                if(!country_code.isEmpty() && !phone.isEmpty())
                {
                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FAuth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
                else
                {
                    tv_validation_text.setText("Please Enter Country Code and Phone Number");
                    tv_validation_text.setTextColor(Color.RED);
                    tv_validation_text.setVisibility(View.VISIBLE);
                }

            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                Log.d("Login", e.getMessage());
                tv_validation_text.setText(e.getMessage());
                tv_validation_text.setTextColor(Color.RED);
                tv_validation_text.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                super.onCodeSent(s, forceResendingToken);

                //@param s - verify code
                // if the code is not detected automatically
                // user has to put it manually

                tv_validation_text.setText("Code has been sent");
                tv_validation_text.setTextColor(Color.GREEN);
                tv_validation_text.setVisibility(View.VISIBLE);

                // put a 10 second delay to let the verification to complete
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                        otpIntent.putExtra("verify_code", s);
                        startActivity(otpIntent);
                    }
                }, 10000);
            }
        };




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
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                {
                    tv_validation_text.setText(task.getException().getMessage());
                    tv_validation_text.setTextColor(Color.RED);
                    tv_validation_text.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
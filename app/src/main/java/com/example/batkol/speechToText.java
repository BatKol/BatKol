package com.example.batkol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class speechToText extends AppCompatActivity {

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private Button button;
    boolean record = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            checkPermission();
        }

        editText = findViewById(R.id.text);
        button = findViewById(R.id.button2);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener()
        {
            @Override
            public void onReadyForSpeech(Bundle bundle)
            {
                System.out.println("onReadyForSpeech");

            }

            @Override
            public void onBeginningOfSpeech()
            {
                System.out.println("onBeginningOfSpeech");

                editText.setText("Listening...");

            }

            @Override
            public void onRmsChanged(float v)
            {
                System.out.println("onRmsChanged");

            }

            @Override
            public void onBufferReceived(byte[] bytes)
            {
                System.out.println("onBufferReceived");

            }

            @Override
            public void onEndOfSpeech()
            {
                System.out.println("onEndOfSpeech");

            }

            @Override
            public void onError(int i)
            {
                System.out.println("onError " + i);

            }

            @Override
            public void onResults(Bundle bundle)
            {

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));
                System.out.println(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle)
            {
                System.out.println("onPartialResults");

            }

            @Override
            public void onEvent(int i, Bundle bundle)
            {
                System.out.println("onEvent " + i + " "+ bundle);

            }
        });

        button.setOnClickListener((v)->
        {

                if (!record)
                {
                    record = true;
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                else
                {
                    record = false;
                    speechRecognizer.stopListening();

                }


        });

    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show();
        }
    }
}
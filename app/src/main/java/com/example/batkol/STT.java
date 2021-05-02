package com.example.batkol;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class STT {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    public ArrayList<String> data;
    Activity activity;
    final Intent speechRecognizerIntent ;
    public STT(Context context,Activity activity,ArrayList<String> data,Intent i){
        this.activity = activity;
        this.data = data;
        speechRecognizerIntent =i;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


    }

    public void StartSTT(){
        speechRecognizer.startListening(speechRecognizerIntent);
    }
    public void StoptSTT(onResultsFunc resultsFunc){

        speechRecognizer.stopListening();
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.e("liads", "begininggggggggggggggggg");

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                Log.e("liads", error+"");


            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> DataResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                data = DataResults;
                System.out.println(Arrays.toString(new ArrayList[]{data}));
                resultsFunc.run();


            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

    }
    public ArrayList<String> ReturnResult(){
       return this.data;
    }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
}

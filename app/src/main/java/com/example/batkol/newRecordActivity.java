package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import utils.AlgorithmsLibrary;

public class newRecordActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final Integer RecordAudioRequestCode = 1;
    EditText description;
    private static final String LOG_TAG ="newRecordActivity" ;
    TextView tv_recordLabel;
    EditText recordName;
    Button btn_record, btn_upload, btn_delete, btn_stop_record,btn_play;
    private MediaRecorder recorder = null;
    boolean playing = false, recording = false,canUpload = false;
    String recordNameS = null;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private Spinner freqSpinner;
    private MediaPlayer player = null;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Boolean finishUpload = false;
    ArrayAdapter<String> effectAdapter;
    private SpeechRecognizer speechRecognizer;
    Button speechButton;
    boolean canPlay = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        tv_recordLabel = (TextView)findViewById(R.id.recordLabel);
        recordName = (EditText)findViewById(R.id.edix_filename);
        btn_record = (Button)findViewById(R.id.recordBtn);
        btn_upload = (Button)findViewById(R.id.button_upload);
        btn_play = (Button)findViewById(R.id.button_play);
        btn_delete = (Button)findViewById(R.id.button_delete);
        description = findViewById(R.id.description_text);
        freqSpinner = findViewById(R.id.freqSpinner);
        speechButton = findViewById(R.id.speechhelper);

        btn_delete.setVisibility(View.INVISIBLE);
        btn_upload.setVisibility(View.INVISIBLE);
        btn_play.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();


        String[] effectsList = getResources().getStringArray(R.array.Effects);
        effectAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, effectsList);
        effectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        freqSpinner.setAdapter(effectAdapter);
        freqSpinner.setVisibility(View.GONE);

        btn_delete.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            checkPermission();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(new BatKolRconizer(this::wordProcessing));
        speechButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(playing)
                        stopPlay();
                    if (recording)
                        stopRecord();
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    TestButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
//        btn_record.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    newRecord();
//
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    stopRecord();
//
//
//                }
//                return false;
//            }
//        });
    }
;
    @Override
    public void onClick(View v) {
        if (v == btn_record){
            if (recording){
                stopRecord();
                return;
            }
            startRecording();
            return;
        }
        if (v == btn_upload){
            uploadRecord();
            return;
        }
        if (v == btn_play){
//            playRecord();
            startPlaying();
            return;
        }
        if (v==btn_delete){
            deleteRecord();
            return;
        }
        if (v== btn_stop_record){
            stopRecord();
        }

    }

    private void uploadRecord() {
        if (!canUpload){
            return;
        }
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("audio/"+recordName.getText().toString()+new Date().toString()+".3gp");
        try {
            InputStream stream = new FileInputStream(new File(recordNameS));
            UploadTask uploadTask = mountainsRef.putStream(stream);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return mountainsRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        uploadToFirebase(downloadUri);
                    } else {
                       Toast.makeText(newRecordActivity.this,"fail to upload",Toast.LENGTH_SHORT).show();
                    }
                }

                private void uploadToFirebase(Uri downloadUri) {
                    String userID = user.getUid();
                    String username = user.getDisplayName();
                    String fileName = recordName.getText().toString();
                    String descrition = description.getText().toString();
                    String url = downloadUri.toString();
                    Date date = new Date();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat pf= new SimpleDateFormat("dd.MMM.yyyy");
                    String dateF = pf.format(date);
                    //postID
                    String postID = AlgorithmsLibrary.hashMD5(userID+fileName+descrition+date);
                    float[] effect = getRecordEffect();
                    AudioPosts post = new AudioPosts(username,fileName,url,descrition,postID,userID,date, effect[0],effect[1]);


                    db.collection("Posts").document(postID)
                            .set(post)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("UploadTask", "DocumentSnapshot successfully written!");
                                    finishUpload = true;
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("UploadTask", "Error writing document", e);
                                }
                            });
                    db.collection("Users").document(userID).collection("Posts").document(postID)
                            .set(post)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("UploadTask", "DocumentSnapshot successfully written!");
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("UploadTask", "Error writing document", e);
                                }
                            });
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
    private void stopPlay(){
        if (playing){
            player.release();
            player = null;
            playing = false;
        }
    }

    private void startPlaying() {
        if (playing) {
            stopPlay();
            return;
        }
        player = new MediaPlayer();
        try {
            player.setDataSource(recordNameS);
            float[] effect = getRecordEffect();
            PlaybackParams params = new PlaybackParams();
            params.setPitch(effect[0]); // pitch
            params.setSpeed(effect[1]); // speed
            player.setPlaybackParams(params);
            player.prepare();
            player.start();
            playing = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void deleteRecord() {
        btn_delete.setVisibility(View.INVISIBLE);
        btn_upload.setVisibility(View.INVISIBLE);
        btn_play.setVisibility(View.INVISIBLE);
        freqSpinner.setVisibility(View.GONE);
        btn_record.setVisibility(View.VISIBLE);
        canPlay = false;
    }

    private void stopRecord() {
        if (!recording){
            return;
        }
        recorder.stop();
        recorder.release();
        freqSpinner.setVisibility(View.VISIBLE);


        recorder = null;
        recording = false;
        canUpload = true;
        btn_delete.setVisibility(View.VISIBLE);
        btn_upload.setVisibility(View.VISIBLE);
        btn_play.setVisibility(View.VISIBLE);
        btn_record.setVisibility(View.INVISIBLE);
        tv_recordLabel.setText("Tap to record");
        btn_record.setText("Tap to record");


    }
    private void startRecording() {
        canPlay = true;
        recordNameS = getExternalCacheDir().getAbsolutePath();
        recordNameS += "/audiorecordtest.3gp";
        recorder = new MediaRecorder();
        //
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
////        recorder.setAudioChannels(1);
////        recorder.setAudioEncodingBitRate(128000);
////        recorder.setAudioSamplingRate(48000);
//        recorder.setOutputFile(recordNameS);
        //

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordNameS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recording = true;
        recorder.start();

        tv_recordLabel.setText("Tap to Stop");
        btn_record.setText("Tap to Stop");
    }

    // return the desired effect by getting the position from the spinner and fetch the settings
    // for record pitch and speed
    private float[] getRecordEffect()
    {
        float[] pitch_speed = new float[2];
        //default values
        pitch_speed[0] = 1.0f;
        pitch_speed[1] = 1.0f;

        try
        {
            int spinner_pos = freqSpinner.getSelectedItemPosition();
            String[] effect_values = getResources().getStringArray(R.array.effectValues);
            String[] effectSettings = effect_values[spinner_pos].split(",");
            pitch_speed[0] = Float.parseFloat(effectSettings[0]);
            pitch_speed[1] = Float.parseFloat(effectSettings[1]);

        }catch (Exception e)
        {
            Log.d("Error", e.getMessage());
        }

        return pitch_speed;
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }
    private void wordProcessing(String s){
        System.out.println(s);
        ArrayList<String> listS = new ArrayList<String>(Arrays.asList(s.split(" ")));
        if(AlgorithmsLibrary.stringInArray(listS,"record")){
            if (!recording){
                startRecording();
            }

        }
        else if(AlgorithmsLibrary.stringInArray(listS,"upload")){
            uploadRecord();
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"play")){
            if (canPlay)
                startPlaying();
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"delete")){
            deleteRecord();
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"help")){
            System.out.println("help sound...");
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"back")){
            finish();
        }
        else
            System.out.println("nothing found");


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
package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class newRecordActivity extends AppCompatActivity implements View.OnClickListener
{
    TextView tv_recordLabel;
    EditText recordName;
    Button btn_record, btn_upload, btn_delete, btn_stop_record,btn_play;
    MediaRecorder mediaRecorder = new MediaRecorder();
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean playing = false, recording = false,canUpload = false;
    String recordNameS = "";
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        tv_recordLabel = (TextView)findViewById(R.id.recordLabel);
        btn_record = (Button)findViewById(R.id.recordBtn);
        btn_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_record){
            newRecord();
            return;
        }
        if (v == btn_upload){
            uploadRecord();
            return;
        }
        if (v == btn_play){
            playRecord();
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
        StorageReference mountainsRef = storageRef.child("audio/"+recordNameS);
        try {
            InputStream stream = new FileInputStream(new File(recordNameS));
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(newRecordActivity.this, "Fail upload to the server",
                            Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // contains file metadata such as size, content-type, etc.
                    Toast.makeText(newRecordActivity.this, "Success upload "+ taskSnapshot.getMetadata()+"to the server",
                            Toast.LENGTH_SHORT).show();
                    // ...
                    finish();
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void playRecord() {
        if (playing){
            playing = false;
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        try {
            mediaPlayer.setDataSource(recordNameS);
            mediaPlayer.prepare();
            playing = true;
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void deleteRecord() {
    }

    private void stopRecord() {
        if (!recording){
            return;
        }
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        recording = false;
        canUpload = true;
    }

    private void newRecord() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recordNameS = recordName+".aac";
        mediaRecorder.setOutputFile(recordNameS);
        try {
        mediaRecorder.prepare();
        recording = true;
        mediaRecorder.start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
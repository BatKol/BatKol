package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class Main_flow extends AppCompatActivity{
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<AudioPosts>  posts = new ArrayList<>();
    ListView audio_posts;
    Button searchBT,newRecordBT,profileBT;
    DocumentSnapshot lastVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_flow);
        audio_posts = findViewById(R.id.list_item);
        searchBT = findViewById(R.id.search_btn);
        newRecordBT = findViewById(R.id.record_btn);
        profileBT = findViewById(R.id.myprofile_btn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        first10Posts();

        newRecordBT.setOnClickListener(v -> startActivity(new Intent(Main_flow.this,newRecordActivity.class)));
    }

    public void help(View view) {

    }
    private void add10Posts(){
        db.collection("Posts").startAfter(lastVisible).limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lastVisible = task.getResult().getDocuments()
                                    .get(task.getResult().size() -1);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Posts", document.getId() + " => " + document.getData());
                                posts.add(document.toObject(AudioPosts.class));
                            }
                        } else {
                            Log.d("Posts", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }private void first10Posts(){
        db.collection("Posts").limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lastVisible = task.getResult().getDocuments()
                                    .get(task.getResult().size() -1);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Posts", document.getId() + " => " + document.getData());
                                posts.add(document.toObject(AudioPosts.class));
                            }
                        } else {
                            Log.d("posts", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
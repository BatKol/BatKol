package com.example.batkol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Main_flow extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<AudioPosts>  posts = new ArrayList<>();
    ListView audio_posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_flow);
        audio_posts = findViewById(R.id.list_item);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
    }

    public void help(View view) {

    }
}
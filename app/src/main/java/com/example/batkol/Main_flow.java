package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import models.RecordCard;
import utils.RecordList_adapter;

public class Main_flow extends AppCompatActivity{
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<AudioPosts>  posts = new ArrayList<>();
    ListView audio_posts;
    Button searchBT,newRecordBT,profileBT;
    DocumentSnapshot lastVisible;
    private RecyclerView recyclerView;
    private ArrayList<RecordCard> cards;
    private RecordList_adapter cardsAdapter;


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
        cards = new ArrayList<RecordCard>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initRecyclerAdapter();

        first10Posts();

        newRecordBT.setOnClickListener(v -> startActivity(new Intent(Main_flow.this,newRecordActivity.class)));
        searchBT.setOnClickListener(v -> startActivity(new Intent(Main_flow.this, TestElastic.class)));
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
                        if (task.isSuccessful()&&task.getResult().size()>0) {
                            lastVisible = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Posts", document.getId() + " => " + document.getData());
                                posts.add(document.toObject(AudioPosts.class));
                            }
                            addNewCard();
                        } else {
                            Log.d("posts", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    // create new card from given snapshot of seller products
    private void addNewCard(){

        RecordCard card = null;
        for(int i = 0; i < posts.size(); i++)
        {
            card = new RecordCard();

            card.setCreatorName(posts.get(i).name);

            card.setPublishDate(posts.get(i).date.toString());

            card.setRecordUrl(posts.get(i).url);
            cards.add(card);

        }

        cardsAdapter.notifyDataSetChanged();
    }


    // print the cards arrays on the current activity recyclerView
    private void initRecyclerAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new RecordList_adapter(this,cards);
        recyclerView.setAdapter(cardsAdapter);

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
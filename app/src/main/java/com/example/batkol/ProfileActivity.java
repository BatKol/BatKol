package com.example.batkol;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RecordCard;
import utils.RecordList_adapter;
import utils.RecordList_adapter_old;

public class ProfileActivity extends AppCompatActivity {
    private AppBarLayout appBar;
    private TextView profileName;
    private  TextView fansNumbers;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String id;
    RecyclerView profile_posts;
    private ArrayList<RecordCard> cards = new ArrayList<RecordCard>();

    private RecordList_adapter_old cardsAdapter;
    private static ArrayList<AudioPosts>  posts = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        appBar = findViewById(R.id.app_bar);
        profileName= findViewById(R.id.profile_name);
        fansNumbers =findViewById(R.id.fans_number);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        id=user.getUid();
        profile_posts= findViewById(R.id.profile_posts);
        posts.clear();

        fansNumbers.setText("Fans: "+String.valueOf( new Random().nextInt(61) +20));

        Bundle b = getIntent().getExtras();
        if(b != null)
            id = b.getString("uid");


        initRecyclerAdapter();
        getListItems();
        Log.d("hadar","43434343434343333333333333333333333333333333");


    }

    private void initRecyclerAdapter() {
        profile_posts.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new RecordList_adapter_old(this,cards);
        profile_posts.setAdapter(cardsAdapter);

    }

    private void addNewCard(){
        System.out.println(posts.size());

        RecordCard card = null;
        for(int i = 0; i < posts.size(); i++)
        {
            card = new RecordCard();
            System.out.println("new cardv added");
            card.setCreatorName(posts.get(i).name);
            card.setPublishDate(posts.get(i).date.toString());
            card.setRecordUrl(posts.get(i).url);
            float[] effect = new float[2];
            effect[0] = posts.get(i).record_pitch;
            effect[1] = posts.get(i).record_speed;
            card.setEffect(effect);
            cards.add(card);

        }

        cardsAdapter.notifyDataSetChanged();
    }

    private void getListItems() {
        db.collection("Posts").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d("slomi", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            List<AudioPosts> types = documentSnapshots.toObjects(AudioPosts.class);
                            for (AudioPosts post : types){
                                if(post.getUserID().equals(id)){
                                    posts.add(post);
                                    profileName.setText(post.getName());
                                }
                            }
                            addNewCard();


                        }

                    }
                });

    }
}
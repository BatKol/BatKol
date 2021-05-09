package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.RecordCard;
import utils.AlgorithmsLibrary;
import utils.RecordList_adapter;

public class Main_flow extends AppCompatActivity{
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<AudioPosts>  posts = new ArrayList<>();
    ListView audio_posts;
    Button searchBT,newRecordBT,profileBT,DOWNbutton,UPbutton,TestButton;
    DocumentSnapshot lastVisible;
    private RecyclerView recyclerView;
    private ArrayList<RecordCard> cards;
    private ArrayList<RecordCard> cards_visible;
    private RecordList_adapter cardsAdapter;
    private int postNumberIndex=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_flow);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            checkPermission();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(new BatKolRconizer(this::wordProcessing));
        audio_posts = findViewById(R.id.list_item);
        searchBT = findViewById(R.id.search_btn);
        newRecordBT = findViewById(R.id.record_btn);
        profileBT = findViewById(R.id.myprofile_btn);
        UPbutton = findViewById(R.id.UPbutton);
        TestButton=findViewById(R.id.TESTbutton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        cards = new ArrayList<RecordCard>();
        cards_visible = new ArrayList<RecordCard>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initRecyclerAdapter();

        first10Posts();
        TestButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    System.out.println("hhh");
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    TestButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
//        TestButton.setOnClickListener(v -> startActivity(new Intent(Main_flow.this, speechToText.class)));
        newRecordBT.setOnClickListener(v -> startActivity(new Intent(Main_flow.this,newRecordActivity.class)));
        searchBT.setOnClickListener(v -> startActivity(new Intent(Main_flow.this, SearchPosts.class)));
        UPbutton.setOnClickListener(v->{nextPost();});


    }
    private void nextPost(){
        if (postNumberIndex<cards.size()) {
            
            cards_visible.clear();
            cards_visible.add(cards.get(postNumberIndex));
            cardsAdapter.notifyDataSetChanged();
            postNumberIndex++;
        }

//        myFlow.removeAllViews();
//        Log.d("numberofpost", String.valueOf(recyclerView.getChildCount()));
//
////        recyclerView.setVisibility(View.VISIBLE);
//        if(recyclerView.getChildCount() != 0) {
//            View tempview = recyclerView.getChildAt(postNumberIndex++);
//            recyclerView.removeView(tempview);
//            if(tempview!= null) {
//                myFlow.addView(tempview);
//            }
//            else
//                postNumberIndex--;
//        }

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
                            nextPost();
                        } else {
                            Log.d("posts", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
    // create new card from given snapshot of seller products
    private void addNewCard(){
        System.out.println(posts.size());

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
        cardsAdapter = new RecordList_adapter(this,cards_visible);
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
    private void wordProcessing(String s){
        System.out.println(s);
        ArrayList<String> listS = new ArrayList<String>(Arrays.asList(s.split(" ")));
        if(AlgorithmsLibrary.stringInArray(listS,"search")){
            System.out.println("joke on liad");
            startActivity(new Intent(Main_flow.this, SearchPosts.class));
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"record")){
            startActivity(new Intent(Main_flow.this, newRecordActivity.class));
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"help")){
            System.out.println("help sound...");
        }
        else
            System.out.println("nothing found");


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
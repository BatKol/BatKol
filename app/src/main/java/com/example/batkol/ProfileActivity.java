package com.example.batkol;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Paint;
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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import models.RecordCard;
import utils.AlgorithmsLibrary;
import utils.RecordList_adapter;
import utils.RecordList_adapter_old;

public class ProfileActivity extends AppCompatActivity {
    private AppBarLayout appBar;
    private TextView profileName;
    private  TextView fansNumbers;
    Button button;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    TextToSpeech textToSpeech;
    String id;
    SpeechRecognizer speechRecognizer;
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
        button=findViewById(R.id.buttonSpeak);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        id=user.getUid();
        profile_posts= findViewById(R.id.profile_posts);
        posts.clear();


        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(new BatKolRconizer(this::wordProcessing));
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    TestButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });






        //change header color randomly
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        appBar.setBackgroundColor(color);






        fansNumbers.setText("Fans: "+String.valueOf( new Random().nextInt(61) +20));

        Bundle b = getIntent().getExtras();
        if(b != null)
            id = b.getString("uid");


        initRecyclerAdapter();
        getListItems();


        //tell the user he is on Profie name
        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SpeackToUser();
            }
        });





    }


    private void SpeackToUser() {


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            // THIS RUNS THIRD!
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {

                    textToSpeech.setLanguage(Locale.ENGLISH);

                    // NEW LOCATION
                    textToSpeech.speak("You are on " + profileName.getText()+" profile.", TextToSpeech.QUEUE_FLUSH, null, null);


                }

            }
        });
    }

    private void initRecyclerAdapter() {
        profile_posts.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new RecordList_adapter_old(this,cards,"profile");
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

    private void wordProcessing(String s){
        System.out.println(s);
        ArrayList<String> listS = new ArrayList<String>(Arrays.asList(s.split(" ")));
        if(AlgorithmsLibrary.stringInArray(listS,"search") || AlgorithmsLibrary.stringInArray(listS,"חפש")){

            startActivity(new Intent(ProfileActivity.this, SearchPosts.class));
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"record") || AlgorithmsLibrary.stringInArray(listS,"רקורד")){
            startActivity(new Intent(ProfileActivity.this, newRecordActivity.class));
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"log out")){
            mAuth.signOut();
            finish();
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"bay")){
            mAuth.signOut();
            finish();
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"לוגאוט")){
            mAuth.signOut();
            finish();
        }

        else if(AlgorithmsLibrary.stringInArray(listS,"home")){
            startActivity(new Intent(ProfileActivity.this, Main_flow.class));
        }

        else if(AlgorithmsLibrary.stringInArray(listS,"profile") || AlgorithmsLibrary.stringInArray(listS,"פרופיל")){
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        }
        else
            System.out.println("nothing found");


    }
}
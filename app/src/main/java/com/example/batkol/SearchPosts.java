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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import models.RecordCard;
import utils.AlgorithmsLibrary;
import utils.ElasticRestClient;
import cz.msebera.android.httpclient.Header;
import utils.RecordList_adapter;
import utils.RecordList_adapter_old;

public class SearchPosts extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    int postNumberIndex = 0;
    private SpeechRecognizer speechRecognizer;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Button search,TestButton;
    EditText searchTags;
    private RecyclerView recyclerView;
    private ArrayList<RecordCard> cards;
    private ArrayList<RecordCard> cards_visible;
    private RecordList_adapter_old cardsAdapter;
    ArrayList<AudioPosts> posts = new ArrayList<>();
    HashMap<String,Boolean> once = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_posts);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            checkPermission();
        }
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.setRecognitionListener(new BatKolRconizer(this::wordProcessing));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        cards = new ArrayList<RecordCard>();
        cards_visible = new ArrayList<RecordCard>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        search = findViewById(R.id.buttonsearch);
        TestButton = findViewById(R.id.test);
        searchTags = findViewById(R.id.editTextsearch);
        recyclerView = findViewById(R.id.recyclerView2);
        TestButton.setOnTouchListener(new View.OnTouchListener() {
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
        search.setOnClickListener((v)->{
            if (!searchTags.getText().toString().equals("")){
                startshearch(searchTags.getText().toString());
            }
        });
        initRecyclerAdapter();

    }

    public void startshearch(String word) {
        once = new HashMap<>();
        posts.clear();
        cards.clear();
        cardsAdapter.notifyDataSetChanged();
        ElasticRestClient.postsearch(word, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode + " " + Arrays.toString(headers) + " " + Arrays.toString(responseBody));
                String s = new String(responseBody, StandardCharsets.UTF_8);
                System.out.println(s);
                try {
                    JSONObject testV = new JSONObject(new String(responseBody));
                    JSONArray postArr = testV.getJSONObject("hits").getJSONArray("hits");
                    String postID, stt;
                    for (int i = postArr.length()-1 ; i>=0;i--){
                        postID = ((JSONObject)postArr.get(i)).getJSONObject("_source").getString("postID");
                        stt = ((JSONObject)postArr.get(i)).getJSONObject("_source").getString("stt");
                        if (once.get(postID) == null) {
                            getFromDbToCard(postID);
                            once.put(postID,true);
                        }


                    }
//                    JSONObject hit = (JSONObject) testV.getJSONObject("hits").getJSONArray("hits").get(0);
//                    postID = hit.getJSONObject("_source").getString("postID");
//                    stt = hit.getJSONObject("_source").getString("stt");

//                System.out.println("PostID: "+ postID);
//                System.out.println("stt: "+ stt);
//                textView.setText("PostID: "+ postID+"\n"+"stt: "+ stt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody, StandardCharsets.UTF_8);
                System.out.println(error.getMessage());
                System.out.println(s);
                System.out.println(Arrays.toString(headers));
                System.out.println(statusCode);
            }
        });
    }

    private void getFromDbToCard(String postID) {
        DocumentReference docRef = db.collection("Posts").document(postID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    posts.add(document.toObject(AudioPosts.class));
                    assert document != null;
                    addNewCard(Objects.requireNonNull(document.toObject(AudioPosts.class)));
                    if (document.exists()) {
                        Log.d("get-post", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("get-post", "No such document");
                    }
                } else {
                    Log.d("get-post", "get failed with ", task.getException());
                }
            }
        });
    }

    private void initRecyclerAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new RecordList_adapter_old(this,cards_visible);
        recyclerView.setAdapter(cardsAdapter);

    }
    // create new card from given snapshot of seller products
    private void addNewCard(AudioPosts audioPosts){
        RecordCard card = null;
        card = new RecordCard();
        card.setCreatorName(audioPosts.name);
        card.setPublishDate(audioPosts.date.toString());
        card.setRecordUrl(audioPosts.url);
        float[] effect = new float[2];
        effect[0] = audioPosts.record_pitch;
        effect[1] = audioPosts.record_speed;
        card.setEffect(effect);
        cards.add(card);
        cardsAdapter.notifyDataSetChanged();
        cards_visible.add(cards.get(0));
    }
    private void nextPost()
    {
        try {

        cards_visible.clear();
        recyclerView.removeAllViewsInLayout();

        cards_visible.add(cards.get(postNumberIndex));

        cardsAdapter.notifyDataSetChanged();

        postNumberIndex++;

        postNumberIndex = postNumberIndex % cards.size();

        } catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }

    }
    private void wordProcessing(String s){
        System.out.println(s);
        ArrayList<String> listS = new ArrayList<String>(Arrays.asList(s.split(" ")));
        if(AlgorithmsLibrary.stringInArray(listS,"search")){
            cards.clear();
            cards_visible.clear();
            cardsAdapter.notifyDataSetChanged();
            String exp = AlgorithmsLibrary.parsStringafter(listS,"search");
            startshearch(exp);
        }
        else if(AlgorithmsLibrary.stringInArray(listS,"next")){
            nextPost();
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
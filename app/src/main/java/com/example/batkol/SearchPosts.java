package com.example.batkol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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

import models.RecordCard;
import utils.ElasticRestClient;
import cz.msebera.android.httpclient.Header;
import utils.RecordList_adapter;

public class SearchPosts extends AppCompatActivity {
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    Button search;
    EditText searchTags;
    private RecyclerView recyclerView;
    private ArrayList<RecordCard> cards;
    private RecordList_adapter cardsAdapter;
    ArrayList<AudioPosts> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_posts);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
//        initRecyclerAdapter();
        cards = new ArrayList<RecordCard>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        search = findViewById(R.id.buttonsearch);
        searchTags = findViewById(R.id.editTextsearch);
        recyclerView = findViewById(R.id.recyclerView2);

        search.setOnClickListener((v)->{
            if (!searchTags.getText().toString().equals("")){
                startshearch(searchTags.getText().toString());
            }
        });
        initRecyclerAdapter();

    }

    public void startshearch(String word) {
        cards.clear();
        initRecyclerAdapter();
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
                        getFromDbToCard(postID);

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
                    addNewCard();
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
        cardsAdapter = new RecordList_adapter(this,cards);
        recyclerView.setAdapter(cardsAdapter);

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
}
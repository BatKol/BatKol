package com.example.batkol;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import models.RecordCard;
import utils.RecordList_adapter;

public class benTesst extends AppCompatActivity
{

    private RecyclerView recyclerView;
    private ArrayList<RecordCard> cards;
    private RecordList_adapter cardsAdapter;
    private ProgressBar progressb;

    public   static boolean flag;
    public static boolean b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ben_tesst);

        cards = new ArrayList<RecordCard>();

        progressb = (ProgressBar)findViewById(R.id.progressBar);
        progressb.setVisibility(View.GONE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        initRecyclerAdapter();
        addNewCard("Liad", "now");
        addNewCard("Ben", "1 day ago");
        addNewCard("Amichai", "3 min ago");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    // create new card from given snapshot of seller products
    private void addNewCard(String creator, String date){

        RecordCard card = new RecordCard();

        card.setCreatorName(creator);

        card.setPublishDate(date);

        cards.add(card);

        cardsAdapter.notifyDataSetChanged();
    }


    // print the cards arrays on the current activity recyclerView
    private void initRecyclerAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardsAdapter = new RecordList_adapter(this,cards);
        recyclerView.setAdapter(cardsAdapter);

    }
}
package com.example.batkol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class newRecordActivity extends AppCompatActivity
{
    TextView tv_recordLabel;
    Button btn_record;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        tv_recordLabel = (TextView)findViewById(R.id.recordLabel);
        btn_record = (Button)findViewById(R.id.recordBtn);
    }
}
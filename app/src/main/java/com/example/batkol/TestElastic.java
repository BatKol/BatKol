package com.example.batkol;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import utils.ElasticRestClient;

public class TestElastic extends AppCompatActivity implements View.OnClickListener {
    Button button;
    EditText editText;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_elastic);
        button = findViewById(R.id.test);
        editText = findViewById(R.id.searchWord);
        textView = findViewById(R.id.showRes);

        button.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
//        RequestParams requestParams = new RequestParams();
//        requestParams.put("id1","123456");
//        requestParams.put("stt","not sloowed amichai g");
//        ElasticRestClient.post("tests/_doc",requestParams,new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String s = new String(responseBody , StandardCharsets.UTF_8);
//                System.out.println(statusCode + " : " + Arrays.toString(headers) +" RRR\n" + s );
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                String s = new String(responseBody , StandardCharsets.UTF_8);
//                System.out.println(error);
//                System.out.println(s);
//                System.out.println(headers);
//                System.out.println(statusCode);
//            }
//        });
        RequestParams stt = new RequestParams();
        RequestParams query = new RequestParams();
//        RequestParams match = new RequestParams();
        HashMap<String,String> mach = new HashMap<>();
        mach.put("match",new HashMap<String ,String>().put("stt","ben")); //here ben is the search replace it
//        match.put("match",stt);
        stt.put("stt","ben");
        query.put("query",mach);


//        JSONObject jsM = new JSONObject();
//        JSONObject jsS = new JSONObject();
//        try {
//            jsS.put("stt","ben");
//            jsM.put("match",jsS);
//            query.put("",jsM);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//        query.put("match",payload);
        String word = editText.getText().toString();




        ElasticRestClient.postsearch( word, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println(statusCode+" "+ Arrays.toString(headers)+" "+Arrays.toString(responseBody));
                String s = new String(responseBody , StandardCharsets.UTF_8);
                System.out.println(s);
                try {
                    JSONObject testV=new JSONObject(new String(responseBody));
                    JSONObject hit = (JSONObject) testV.getJSONObject("hits").getJSONArray("hits").get(0);
                    String postID,stt;
                    postID = hit.getJSONObject("_source").getString("postID");
                    stt=hit.getJSONObject("_source").getString("stt");
                    System.out.println("PostID: "+ postID);
                    System.out.println("stt: "+ stt);
                    textView.setText("PostID: "+ postID+"\n"+"stt: "+ stt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody , StandardCharsets.UTF_8);
                System.out.println(error);
                System.out.println(s);
                System.out.println(Arrays.toString(headers));
                System.out.println(statusCode);
            }
        });
    }
}
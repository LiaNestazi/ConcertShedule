package com.mycompany.concertschedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DescriptionActivity extends AppCompatActivity {


    private Document doc;
    private Thread secThread;
    private Thread thread;
    private Runnable runnable;

    private Elements paragraph;

    private Bundle args;

    private String image;
    private String title;
    private String desc;
    private String date;
    private String time;
    private String place;
    private String price;

    private ProgressBar progressBar;
    private View backArrow;

    private ImageView image_view;
    private TextView title_view;
    private TextView date_view;
    private TextView time_view;
    private TextView place_view;
    private TextView price_view;
    private TextView desc_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        init();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescriptionActivity.super.onBackPressed();
            }
        });
        progressBar.setVisibility(ProgressBar.VISIBLE);

        webThread();

        adaptInfo();
    }

    private void init() {
        args = getIntent().getExtras();
        progressBar = findViewById(R.id.progressBar_desc);
        backArrow = findViewById(R.id.back_arrow);
        image = args.getString("image");
        title = args.getString("title");
        desc = args.getString("desc");
        date = args.getString("date");
        time = args.getString("time");
        place = args.getString("place");
        price = args.getString("price");
        image_view = findViewById(R.id.desc_image_view);
        title_view = findViewById(R.id.desc_title_view);
        date_view = findViewById(R.id.desc_date_view);
        time_view = findViewById(R.id.desc_time_view);
        place_view = findViewById(R.id.desc_place_view);
        price_view = findViewById(R.id.desc_price_view);
        desc_view = findViewById(R.id.desc_view);
    }
    private void adaptInfo() {
        Glide.with(DescriptionActivity.this).load(image).into(image_view);
        title_view.setText(title);
        date_view.setText(date);
        if (time.equals("")) {
            time_view.setText("время неизвестно");
        }
        else {
            time_view.setText(time);
        }
        place_view.setText(place);
        price_view.setText(price);
    }

    public void webThread(){
        runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        secThread = new Thread(runnable);
        secThread.start();
    }

    public void getWeb(){
        try {
            doc = Jsoup.connect(desc).get();
            paragraph = doc.getElementsByAttributeValue("class", "full opened");
            newThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newThread() {
        runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        parse();
                    }
                });
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    private void parse() {
        Elements el;
        String text="";
        for (int i=0;i<paragraph.size();i++){
            el = paragraph.get(i).getElementsByTag("p");
            if (el.isEmpty()){
                desc_view.setText("Нет описания");
                progressBar.setVisibility(ProgressBar.GONE);
            }
            else{
                for (int j=0;j<el.size();j++){
                    if (!el.get(j).text().equals("")) {
                        text += el.get(j).text() + "\n" + "\n";
                    }
                }
                desc_view.setText(text);
                progressBar.setVisibility(ProgressBar.GONE);
            }
        }
    }
}
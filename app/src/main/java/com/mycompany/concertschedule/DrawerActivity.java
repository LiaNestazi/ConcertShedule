package com.mycompany.concertschedule;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mycompany.concertschedule.models.Concert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawerActivity extends AppCompatActivity {

    private Document doc;
    private Elements concerts;
    private Concert concert;
    private Thread thread;
    private Thread secThread;
    private Runnable runnable;
    private DatabaseReference concertsDB;

    private AppBarConfiguration mAppBarConfiguration;

    private ClipData.Item schedule, password, moders, conc_db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView uid_tv = headerView.findViewById(R.id.header_uid);
        TextView pass_tv = headerView.findViewById(R.id.header_pass);
        TextView login_tv = headerView.findViewById(R.id.header_login);
        TextView email_tv = headerView.findViewById(R.id.header_email);

        MenuItem db_panel_item = navigationView.getMenu().findItem(R.id.nav_db_panel);
        MenuItem moders_db_item = navigationView.getMenu().findItem(R.id.nav_moders);
        MenuItem conc_db_item = navigationView.getMenu().findItem(R.id.nav_concerts_db);



        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_schedule, R.id.nav_password, R.id.nav_moders, R.id.nav_concerts_db)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        Bundle args;

        String email, login, uid, pass;
        int admin, moderator;

        args = getIntent().getExtras();
        email = args.getString("email");
        login = args.getString("login");
        pass = args.getString("pass");
        uid = args.getString("uid");
        admin = args.getInt("admin");
        moderator = args.getInt("moderator");
        if (admin == 1) {
            db_panel_item.setVisible(true);
            moders_db_item.setVisible(true);
            conc_db_item.setVisible(true);
        } else{
            if (moderator == 1){
                db_panel_item.setVisible(true);
                conc_db_item.setVisible(true);
            }
        }

        uid_tv.setText(uid);
        pass_tv.setText(pass);
        login_tv.setText(login);
        email_tv.setText(email);

        init();
    }

    public void init(){
        concertsDB = FirebaseDatabase.getInstance().getReference("Concerts");
        concert = new Concert();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem action_settings = menu.findItem(R.id.action_settings);
        action_settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
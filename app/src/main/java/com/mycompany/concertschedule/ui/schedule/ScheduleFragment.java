package com.mycompany.concertschedule.ui.schedule;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.concertschedule.DescriptionActivity;
import com.mycompany.concertschedule.R;
import com.mycompany.concertschedule.models.Concert;

import java.io.InputStream;
import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    private ListView listView;
    private Thread thread;
    private Runnable runnable;
    private DBAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<String> images;
    private ArrayList<String> title;
    private ArrayList<String> desc;
    private ArrayList<String> date;
    private ArrayList<String> time;
    private ArrayList<String> place;
    private ArrayList<String> price;
    private DatabaseReference concertsDB;

    private ScheduleViewModel concertsViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        concertsViewModel =
                new ViewModelProvider(this).get(ScheduleViewModel.class);
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        concertsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Concert conc = ds.getValue(Concert.class);
                    assert conc != null;

                    images.add(conc.getImage());
                    title.add(conc.getTitle());
                    desc.add(conc.getDesc());
                    date.add(conc.getDate());
                    time.add(conc.getTime());
                    place.add(conc.getPlace());
                    price.add(conc.getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        newThread();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent descriptionIntent = new Intent(getActivity(), DescriptionActivity.class);
                descriptionIntent.putExtra("image", images.get(position));
                descriptionIntent.putExtra("title", title.get(position));
                descriptionIntent.putExtra("desc", desc.get(position));
                descriptionIntent.putExtra("date", date.get(position));
                descriptionIntent.putExtra("time", time.get(position));
                descriptionIntent.putExtra("place", place.get(position));
                descriptionIntent.putExtra("price", price.get(position));

                startActivity(descriptionIntent);
            }
        });
    }

    class DBAdapter extends ArrayAdapter<String> {
        Context context;
        private ArrayList<String> images;
        private ArrayList<String> title;
        private ArrayList<String> date;
        private ArrayList<String> time;
        private ArrayList<String> place;
        private ArrayList<String> price;

        DBAdapter(Context c, ArrayList<String> images, ArrayList<String> title, ArrayList<String> desc,
                  ArrayList<String> date, ArrayList<String> time, ArrayList<String> place, ArrayList<String> price){
            super(c, R.layout.row, R.id.title_view, title);
            this.context=c;
            this.images = images;
            this.title = title;
            this.date = date;
            this.time = time;
            this.place = place;
            this.price = price;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView image_view = row.findViewById(R.id.image_view);
            TextView title_view = row.findViewById(R.id.title_view);
            TextView date_view = row.findViewById(R.id.date_view);
            TextView time_view = row.findViewById(R.id.time_view);
            TextView place_view = row.findViewById(R.id.place_view);
            TextView price_view = row.findViewById(R.id.price_view);

            Glide.with(getActivity()).load(images.get(position)).into(image_view);
            title_view.setText(title.get(position));
            date_view.setText(date.get(position));
            if (time.get(position).equals("")) {
                time_view.setText("время неизвестно");
            }
            else {
                time_view.setText(time.get(position));
            }
            place_view.setText(place.get(position));
            price_view.setText(price.get(position));

            progressBar.setVisibility(ProgressBar.GONE);

            return row;
        }
    }
    public void init(View root){
        concertsDB = FirebaseDatabase.getInstance().getReference("Concerts");
        listView = (ListView) root.findViewById(R.id.conc_list_view);
        progressBar = root.findViewById(R.id.progressBar);
        images = new ArrayList<String>();
        title = new ArrayList<String>();
        desc = new ArrayList<String>();
        date = new ArrayList<String>();
        time = new ArrayList<String>();
        place = new ArrayList<String>();
        price = new ArrayList<String>();
    }

    private void newThread() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter = new DBAdapter(getActivity(), images, title, desc, date, time, place, price);
                        listView.setAdapter(adapter);
                    }
                });
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
}
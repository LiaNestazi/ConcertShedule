package com.mycompany.concertschedule.ui.conc_db;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.Toast;

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
import com.mycompany.concertschedule.R;
import com.mycompany.concertschedule.models.Concert;
import com.mycompany.concertschedule.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConcDBFragment extends Fragment {

    private ListView listView;
    private Document doc;
    private Elements concerts;
    private Concert concert;
    private Thread secThread;
    private Thread thread;
    private Runnable runnable;
    private List<String> listData;
    private DBAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<String> uid;
    private ArrayList<String> title;
    private ArrayList<String> date;
    private ArrayList<String> time;
    private ArrayList<String> place;
    private ArrayList<String> price;
    private DatabaseReference concertsDB;
    private AlertDialog.Builder builder;

    private ConcDBViewModel concDBViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_conc_db, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        progressBar.setVisibility(ProgressBar.VISIBLE);

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                   Concert conc = ds.getValue(Concert.class);
                    assert conc != null;

                    uid.add(ds.getKey());
                    title.add(conc.getTitle());
                    date.add(conc.getDate());
                    time.add(conc.getTime());
                    place.add(conc.getPlace());
                    price.add(conc.getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        concertsDB.addValueEventListener(vListener);

        newThread();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                View dialog_view = inflater.inflate(R.layout.conc_db_window, null);
                MaterialEditText title_edit_view = dialog_view.findViewById(R.id.title_edit_view);
                MaterialEditText date_edit_view = dialog_view.findViewById(R.id.date_edit_view);
                MaterialEditText time_edit_view = dialog_view.findViewById(R.id.time_edit_view);
                MaterialEditText place_edit_view = dialog_view.findViewById(R.id.place_edit_view);
                MaterialEditText price_edit_view = dialog_view.findViewById(R.id.price_edit_view);
                builder.setView(dialog_view)
                        .setTitle("Редактировать запись в БД")
                        .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!TextUtils.isEmpty(title_edit_view.getText().toString())){
                                    String value = title_edit_view.getText().toString();
                                    concertsDB.child(uid.get(position)).child("title").setValue(value);
                                }
                                if (!TextUtils.isEmpty(date_edit_view.getText().toString())){
                                    String value = date_edit_view.getText().toString();
                                    concertsDB.child(uid.get(position)).child("date").setValue(value);
                                }
                                if (!TextUtils.isEmpty(time_edit_view.getText().toString())){
                                    String value = time_edit_view.getText().toString();
                                    concertsDB.child(uid.get(position)).child("time").setValue(value);
                                }
                                if (!TextUtils.isEmpty(place_edit_view.getText().toString())){
                                    String value = place_edit_view.getText().toString();
                                    concertsDB.child(uid.get(position)).child("place").setValue(value);
                                }
                                if (!TextUtils.isEmpty(price_edit_view.getText().toString())){
                                    String value = price_edit_view.getText().toString();
                                    concertsDB.child(uid.get(position)).child("price").setValue(value);
                                }
                                newThread();
                            }
                        })
                        .setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteConcert(position);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    class DBAdapter extends ArrayAdapter<String> {
        Context context;
        private ArrayList<String> title;
        private ArrayList<String> uid;
        private ArrayList<String> date;
        private ArrayList<String> time;
        private ArrayList<String> place;
        private ArrayList<String> price;

        DBAdapter(Context c, ArrayList<String> uid, ArrayList<String> title,
                  ArrayList<String> date, ArrayList<String> time, ArrayList<String> place, ArrayList<String> price){
            super(c, R.layout.row_db, R.id.title_view, title);
            this.context=c;
            this.title = title;
            this.uid = uid;
            this.date = date;
            this.time = time;
            this.place = place;
            this.price = price;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row_db, parent, false);
            TextView title_view = row.findViewById(R.id.title_view);
            TextView uid_view = row.findViewById(R.id.uid_view);
            TextView date_view = row.findViewById(R.id.date_view);
            TextView time_view = row.findViewById(R.id.time_view);
            TextView place_view = row.findViewById(R.id.place_view);
            TextView price_view = row.findViewById(R.id.price_view);

            title_view.setText(title.get(position));
            uid_view.setText(uid.get(position));
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
        listView = (ListView) root.findViewById(R.id.db_conc_list_view);
        listData = new ArrayList<>();
        concert = new Concert();
        progressBar = root.findViewById(R.id.progressBar);
        uid = new ArrayList<String>();
        title = new ArrayList<String>();
        date = new ArrayList<String>();
        time = new ArrayList<String>();
        place = new ArrayList<String>();
        price = new ArrayList<String>();
        builder = new AlertDialog.Builder(getActivity());
    }
    private void newThread() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new DBAdapter(getActivity(), uid, title, date, time, place, price);
                        listView.setAdapter(adapter);
                        Log.d("myLog", "Adapter set");
                    }
                });
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
    private void deleteConcert(int position){
        concertsDB.child(uid.get(position)).removeValue();
    }
}
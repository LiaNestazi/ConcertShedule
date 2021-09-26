package com.mycompany.concertschedule.ui.moders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

import java.util.ArrayList;
import java.util.List;

public class ModersFragment extends Fragment {

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
    private ArrayList<String> email;
    private ArrayList<String> login;
    private ArrayList<Integer> admin;
    private ArrayList<Integer> moder;
    private DatabaseReference usersDB;
    private AlertDialog.Builder builder;

    private ModersViewModel modersViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        modersViewModel =
                new ViewModelProvider(this).get(ModersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_moders, container, false);
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
                    User user = ds.getValue(User.class);
                    assert user != null;
                    uid.add(ds.getKey());
                    email.add(user.getEmail());
                    login.add(user.getLogin());
                    admin.add(user.getAdmin());
                    moder.add(user.getModerator());
                    Log.d("myLog", "Got user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersDB.addValueEventListener(vListener);

        newThread(-1,-1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                View dialog_view = inflater.inflate(R.layout.moder_window, null);
                MaterialEditText moder_edit_view = dialog_view.findViewById(R.id.moder_edit_view);
                builder.setView(dialog_view)
                        .setTitle("Редактировать уровень доступа")
                        .setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!moder_edit_view.getText().toString().equals("1")&&!moder_edit_view.getText().toString().equals("0")){
                                    Toast.makeText(getContext(), "Уровень доступа может быть 0 или 1",Toast.LENGTH_SHORT).show();
                                } else{
                                    int value = Integer.parseInt(moder_edit_view.getText().toString());
                                    moder.set(position, value);
                                    newThread(position,value);
                                }
                            }
                        })
                        .setNeutralButton("Удалить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser(position);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
    class DBAdapter extends ArrayAdapter<String> {
        Context context;
        private ArrayList<String> uid;
        private ArrayList<String> email;
        private ArrayList<String> login;
        private ArrayList<Integer> admin;
        private ArrayList<Integer> moder;

        DBAdapter(Context c, ArrayList<String> uid, ArrayList<String> email, ArrayList<String> login, ArrayList<Integer> admin, ArrayList<Integer> moder){
            super(c, R.layout.row_user, R.id.email_view, email);
            this.context=c;
            this.uid = uid;
            this.email = email;
            this.login = login;
            this.admin = admin;
            this.moder = moder;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.row_user, parent, false);
            TextView uid_view = row.findViewById(R.id.uid_view);
            TextView email_view = row.findViewById(R.id.email_view);
            TextView login_view = row.findViewById(R.id.login_view);
            TextView admin_view = row.findViewById(R.id.admin_view);
            TextView moder_view = row.findViewById(R.id.moder_view);

            Log.d("myLog", "Into the get view");
            uid_view.setText(uid.get(position));
            email_view.setText(email.get(position));
            login_view.setText(login.get(position));
            admin_view.setText(String.valueOf(admin.get(position)));
            moder_view.setText(String.valueOf(moder.get(position)));

            progressBar.setVisibility(ProgressBar.GONE);

            return row;
        }
    }
    public void init(View root){
        usersDB = FirebaseDatabase.getInstance().getReference("Users");
        listView = (ListView) root.findViewById(R.id.moders_list_view);
        listData = new ArrayList<>();
        concert = new Concert();
        progressBar = root.findViewById(R.id.progressBar);
        uid = new ArrayList<String>();
        email = new ArrayList<String>();
        login = new ArrayList<String>();
        admin = new ArrayList<Integer>();
        moder = new ArrayList<Integer>();
        builder = new AlertDialog.Builder(getActivity());
    }
    private void newThread(int position, int value) {
        runnable = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter = new DBAdapter(getActivity(), uid, email, login, admin, moder);
                        listView.setAdapter(adapter);
                        if (position>-1&&value>-1){
                            usersDB.child(uid.get(position)).child("moderator").setValue(value);
                        }
                        Log.d("myLog", "Adapter set");
                    }
                });
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
    private void deleteUser(int position){
        usersDB.child(uid.get(position)).removeValue();
    }
}
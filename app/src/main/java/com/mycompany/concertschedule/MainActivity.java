package com.mycompany.concertschedule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.concertschedule.models.Concert;
import com.mycompany.concertschedule.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Document doc;
    private Elements concerts;
    private Concert concert;
    private Thread thread;
    private Thread secThread;
    private Runnable runnable;
    private DatabaseReference concertsDB;

    Button btn_sign_in, btn_sign_up;
    FirebaseAuth auth;
    DatabaseReference users;
    LinearLayout edit_layout;
    View sign_in_edit, sign_up_edit;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        webThread();
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialEditText email = sign_up_edit.findViewById(R.id.email);
                final MaterialEditText login = sign_up_edit.findViewById(R.id.login);
                final MaterialEditText pass = sign_up_edit.findViewById(R.id.password);
                if (!edit_layout.getChildAt(0).equals(sign_up_edit)){
                    edit_layout.removeAllViews();
                    edit_layout.addView(sign_up_edit);
                } else {
                    if (!isOnline(MainActivity.this)){
                        Snackbar.make(v,"Нет подключения к интернету", Snackbar.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(email.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Введите e-mail", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(login.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Введите логин", Toast.LENGTH_SHORT).show();
                        } else {
                            if (login.getText().toString().length() < 3) {
                                Toast.makeText(MainActivity.this, "Логин должен содержать минимум 3 символа", Toast.LENGTH_SHORT).show();
                            } else {
                                if (TextUtils.isEmpty(pass.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (pass.getText().toString().length() < 5) {
                                        Toast.makeText(MainActivity.this, "Пароль должен содержать минимум 5 символов", Toast.LENGTH_SHORT).show();
                                    } else {
                                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(isEmailExists(email.getText().toString(), snapshot)){
                                                    Toast.makeText(MainActivity.this, "Пользователь с таким e-mail уже существует", Toast.LENGTH_SHORT).show();

                                                }  else {
                                                    //Регистрация пользователя, если ошибок не обнаружено
                                                    User user = new User();
                                                    user.setId(users.getKey());
                                                    user.setEmail(email.getText().toString());
                                                    user.setLogin(login.getText().toString());
                                                    user.setPass(pass.getText().toString());
                                                    users.push().setValue(user);
                                                    Toast.makeText(MainActivity.this, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialEditText email = sign_in_edit.findViewById(R.id.email);
                final MaterialEditText pass = sign_in_edit.findViewById(R.id.password);
                if (!edit_layout.getChildAt(0).equals(sign_in_edit)){
                    edit_layout.removeAllViews();
                    edit_layout.addView(sign_in_edit);
                } else {
                    if (!isOnline(MainActivity.this)){
                        Snackbar.make(v,"Нет подключения к интернету", Snackbar.LENGTH_SHORT).show();
                    }
                    if (TextUtils.isEmpty(email.getText().toString())) {
                        Toast.makeText(MainActivity.this, "Введите e-mail", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(pass.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                        } else {
                            if (pass.getText().toString().length() < 5) {
                                Toast.makeText(MainActivity.this, "Пароль должен содержать минимум 5 символов", Toast.LENGTH_SHORT).show();
                            } else {
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            User user = ds.getValue(User.class);
                                            assert user != null;
                                            if (user.getEmail().equals(email.getText().toString()) && user.getPass().equals(pass.getText().toString())) {
                                                signIn(ds.getKey(), user.getEmail(), user.getLogin(), user.getPass(), user.getAdmin(), user.getModerator());
                                                return;
                                            }
                                        }
                                        Toast.makeText(MainActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private void init() {
        //инициализация переменных
        concertsDB = FirebaseDatabase.getInstance().getReference("Concerts");
        concert = new Concert();
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        root = findViewById(R.id.root);
        auth = FirebaseAuth.getInstance();
        users = FirebaseDatabase.getInstance().getReference("Users");
        edit_layout = findViewById(R.id.edit_layout);
        LayoutInflater inflater = LayoutInflater.from(this);
        sign_in_edit = inflater.inflate(R.layout.sign_in, null);
        sign_up_edit = inflater.inflate(R.layout.sign_up, null);
        edit_layout.addView(sign_in_edit);
    }

    public void parse(){
        updateDB();
        Elements el;
        for (int i=0;i<concerts.size();i++){
            //парсинг информации с сайта
            el = concerts.get(i).getElementsByAttributeValueContaining("class", "image");
            if (!el.isEmpty()){
                el = el.get(0).getElementsByAttribute("data-src");
                concert.setImage(el.attr("data-src"));
            }

            el = concerts.get(i).getElementsByAttributeValue("class","title");
            concert.setTitle(el.text());

            if (!el.isEmpty()) {
                el = el.get(0).getElementsByAttribute("href");
                concert.setDesc(el.attr("href"));
            }

            el = concerts.get(i).getElementsByAttributeValue("class","date");
            concert.setDate(el.text());

            if (!el.isEmpty()) {
                el = el.get(0).getElementsByAttributeValue("class", "bold");
                concert.setTime(el.text());
            }

            concert.setDate(concert.getDate().replace(concert.getTime(),""));
            concert.setDate(concert.getDate().replace(".",""));

            el = concerts.get(i).getElementsByAttributeValue("class","place");
            concert.setPlace(el.text());

            el = concerts.get(i).getElementsByAttributeValue("class","cost rub");
            concert.setPrice(el.text());

            //Добавление информации о концерте в массивы
            addDataToDB();
        }
    }
    private void updateDB() {
        //подготовка базы данных для внесения новой информации
        concertsDB.removeValue();
    }

    public void getWeb(){
        //получение сайта с информацией и запуск нового потока
        try {
            doc = Jsoup.connect("https://msk.kassir.ru/bilety-na-koncert").get();
            concerts = doc.getElementsByAttributeValue("class", "col-xs-2");
            newThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addDataToDB(){
        //добавление информации о концерте в БД, если он не пустой
        if (!concert.getTitle().equals("")) {
            concertsDB.push().setValue(concert);
        }
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
    private void newThread() {
        runnable = new Runnable() {
            @Override
            public void run() {
                parse();
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }
    private boolean isEmailExists(String email, DataSnapshot dataSnapshot){
        //поиск пользователя с такой же почтой
        User temp = new User();
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            temp.setEmail(ds.getValue(User.class).getEmail());
            if (temp.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void signIn(String uid, String email, String login, String pass, int admin, int moderator) {
        //переход на новую активность
        Intent drawerIntent = new Intent(MainActivity.this, DrawerActivity.class);
        drawerIntent.putExtra("uid", uid);
        drawerIntent.putExtra("email", email);
        drawerIntent.putExtra("login", login);
        drawerIntent.putExtra("pass", pass);
        drawerIntent.putExtra("admin", admin);
        drawerIntent.putExtra("moderator", moderator);
        startActivity(drawerIntent);
    }
    public static boolean isOnline(Context context) {
        //проверка подключения к интернету
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

}
package com.mycompany.concertschedule.ui.change_pass;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.concertschedule.MainActivity;
import com.mycompany.concertschedule.R;
import com.mycompany.concertschedule.models.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChangePassFragment extends Fragment {

    private Button btn_change_pass;

    DatabaseReference users;
    FirebaseAuth auth;
    String email;
    String pass;
    String uid;
    User user;

    private ChangePassViewModel changePassViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_password, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView uid_tv = headerView.findViewById(R.id.header_uid);
        TextView pass_tv = headerView.findViewById(R.id.header_pass);
        TextView email_tv = headerView.findViewById(R.id.header_email);

        uid = (String) uid_tv.getText();
        email = (String) email_tv.getText();
        pass = (String) pass_tv.getText();

        user = new User();
        auth = FirebaseAuth.getInstance();
        users = FirebaseDatabase.getInstance().getReference("Users");

        btn_change_pass = view.findViewById(R.id.btn_change_pass);

        btn_change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialEditText old_pass = view.findViewById(R.id.old_pass);
                final MaterialEditText new_pass = view.findViewById(R.id.new_pass);
                final MaterialEditText new_pass_confirm = view.findViewById(R.id.new_pass_confirm);
                if (TextUtils.isEmpty(old_pass.getText().toString())){
                    Toast.makeText(getContext(), "Введите старый пароль", Toast.LENGTH_SHORT).show();
                } else {
                    if (old_pass.getText().toString().length() < 5) {
                        Toast.makeText(getContext(), "Пароль должен содержать минимум 5 символов", Toast.LENGTH_SHORT).show();
                    } else{
                        if (TextUtils.isEmpty(new_pass.getText().toString())) {
                            Toast.makeText(getContext(), "Введите новый пароль", Toast.LENGTH_SHORT).show();
                        } else {
                            if (new_pass.getText().toString().length() < 5) {
                                Toast.makeText(getContext(), "Пароль должен содержать минимум 5 символов", Toast.LENGTH_SHORT).show();
                            } else {
                                if (TextUtils.isEmpty(new_pass_confirm.getText().toString())) {
                                    Toast.makeText(getContext(), "Введите подтверждение нового пароля", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (new_pass_confirm.getText().toString().length() < 5) {
                                        Toast.makeText(getContext(), "Пароль должен содержать минимум 5 символов", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (!new_pass.getText().toString().equals(new_pass_confirm.getText().toString())){
                                            Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (!old_pass.getText().toString().equals(pass)){
                                                Toast.makeText(getContext(), "Неверный старый пароль", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String value = new_pass.getText().toString();
                                                users.child(uid).child("pass").setValue(value);
                                                pass = value;
                                                Toast.makeText(getContext(), "Пароль успешно изменен!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
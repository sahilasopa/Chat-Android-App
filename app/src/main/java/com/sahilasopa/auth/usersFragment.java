package com.sahilasopa.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilasopa.auth.adapter.UserAdapter;
import com.sahilasopa.auth.models.Users;

import java.util.ArrayList;
import java.util.List;

public class usersFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference reference;
    private UserAdapter userAdapter;
    private List<Users> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView;
        reference = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent login = new Intent(getContext(), com.sahilasopa.auth.login.class);
        if ((firebaseUser == null)) {
            startActivity(login);
            return;
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Users user = ds.getValue(Users.class);
                    assert user != null;
                    if (!(user.getId().equals(firebaseUser.getUid()))) {
                        users.add(user);
                    }
                }
                userAdapter = new UserAdapter(getActivity(), users, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
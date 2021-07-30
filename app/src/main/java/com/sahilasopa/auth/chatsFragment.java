package com.sahilasopa.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sahilasopa.auth.adapter.UserAdapter;
import com.sahilasopa.auth.models.Chat;
import com.sahilasopa.auth.models.Users;

import java.util.ArrayList;
import java.util.List;

public class chatsFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference reference;
    DatabaseReference chatReference;
    FirebaseDatabase database;
    private UserAdapter userAdapter;
    private List<Users> users;
    private List<Users> final_users;
    private List<String> ids;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        Query chatQuery = chatReference.orderByChild("timestamp");
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        final_users = new ArrayList<>();
        ids = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent login = new Intent(getContext(), com.sahilasopa.auth.login.class);
        if ((firebaseUser == null)) {
            startActivity(login);
            return;
        }
        chatQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ids.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if ((!chat.getReceiver().equals(firebaseUser.getUid())) && (chat.getSender().equals(firebaseUser.getUid()))) {
                        ids.add(chat.getReceiver());
                    }
                    if ((!chat.getSender().equals(firebaseUser.getUid())) && (chat.getReceiver().equals(firebaseUser.getUid()))) {
                        ids.add(chat.getSender());
                    }
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Users user = ds.getValue(Users.class);
                            if (ids.contains(user.getId())){
                                users.add(user);
                            }
                        }
//                        for (int i = 0; i < ids.size(); i++) {
//                            String string = ids.get(i);
//                            Users users1 = users.get(i);
//                            Log.v("response", String.valueOf(i));
//                            Log.v("response", String.valueOf(ids));
//                            if (!(users1.getId().equals(string))) {
//                                users.set(i, users1);
//                            }
//                        }
                        userAdapter = new UserAdapter(getActivity(), users);
                        recyclerView.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
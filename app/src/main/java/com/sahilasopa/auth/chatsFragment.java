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
    private List<String> ids;
    private List<Chat> unread;
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
        ids = new ArrayList<>();
        unread = new ArrayList<Chat>();
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
                unread.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if ((!chat.getReceiver().equals(firebaseUser.getUid())) && (chat.getSender().equals(firebaseUser.getUid()))) {
                        // I am the sender
                        ids.remove(chat.getReceiver());
                        ids.add(0, chat.getReceiver());
                    }
                    if ((!chat.getSender().equals(firebaseUser.getUid())) && (chat.getReceiver().equals(firebaseUser.getUid()))) {
                        if (!chat.isSeen()) {
                            unread.remove(chat);
                            unread.add(0, chat);
                        }
                        // I am the receiver
                        ids.remove(chat.getSender());
                        ids.add(0, chat.getSender());
                    }
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (int i = 0; i < ids.size(); i++) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Users user = ds.getValue(Users.class);
                                assert user != null;
                                if (ids.get(i).equals(user.getId())) {
                                    users.add(user);
                                }
                            }
                        }
                        userAdapter = new UserAdapter(getActivity(), users, true, unread);
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
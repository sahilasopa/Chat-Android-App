package com.sahilasopa.auth;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahilasopa.auth.adapter.MessageAdapter;
import com.sahilasopa.auth.databinding.ActivityMessageBinding;
import com.sahilasopa.auth.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ActivityMessageBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    private MessageAdapter messageAdapter;
    private List<Chat> chats;
    Chat chat = new Chat();
    long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        RecyclerView recyclerView;
        setTitle(getIntent().getExtras().get("username").toString());
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        auth = FirebaseAuth.getInstance();
        chats = new ArrayList<>();
        recyclerView = binding.chats;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.editMessage.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (!(binding.editMessage.getText().toString().isEmpty())) {
                            timestamp = System.currentTimeMillis();
                            assert firebaseUser != null;
                            chat.setTimestamp(timestamp);
                            chat.setSender(firebaseUser.getUid());
                            chat.setReceiver(getIntent().getExtras().get("user").toString());
                            chat.setMessage(binding.editMessage.getText().toString());
                            binding.editMessage.setText("");
                            reference.push().setValue(chat);
                        }
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        binding.buttonSend.setOnClickListener(v -> {
            if (!(binding.editMessage.getText().toString().isEmpty())) {
                assert firebaseUser != null;
                timestamp = System.currentTimeMillis();
                chat.setTimestamp(timestamp);
                chat.setSender(firebaseUser.getUid());
                chat.setReceiver(getIntent().getExtras().get("user").toString());
                chat.setMessage(binding.editMessage.getText().toString());
                binding.editMessage.setText("");
                reference.push().setValue(chat);
            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    assert firebaseUser != null;
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(getIntent().getExtras().get("user").toString()) ||
                            chat.getReceiver().equals(getIntent().getExtras().get("user").toString()) && chat.getSender().equals(firebaseUser.getUid())) {
                        chats.add(chat);
                    }
//                    recyclerView.smoothScrollToPosition(chats.size() - 1);
                }
                messageAdapter = new MessageAdapter(getApplicationContext(), chats);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
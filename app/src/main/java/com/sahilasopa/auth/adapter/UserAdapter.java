package com.sahilasopa.auth.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.sahilasopa.auth.MessageActivity;
import com.sahilasopa.auth.R;
import com.sahilasopa.auth.models.Chat;
import com.sahilasopa.auth.models.Users;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final Context context;
    private final List<Users> users;
    private boolean isChat;
    private List<Chat> unread;

    public UserAdapter(Context context, List<Users> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    public UserAdapter(Context context, List<Users> users, boolean isChat, List<Chat> unreads) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
        this.unread = unreads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Users user = users.get(position);
        holder.textView.setText(user.getUsername());
        if (isChat) {
            for (int i = 0; i < unread.size(); i++) {
                Chat chat = unread.get(i);
                if (chat.getSender().equals(user.getId()) || chat.getReceiver().equals(user.getId())) {
                    holder.unread.setVisibility(View.VISIBLE);
                    holder.unread.setText("");
                }
            }
            if (user.getStatus() != null) {
                holder.status.setText(user.getStatus());
            }
        } else {
            holder.status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;
        public TextView status;
        public TextView unread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            status = itemView.findViewById(R.id.textView2);
            unread = itemView.findViewById(R.id.textView3);
            CardView cardView = itemView.findViewById(R.id.userView);
            textView.setOnClickListener(this);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Users user = users.get(pos);
            Intent chat = new Intent(context, MessageActivity.class);
            chat.putExtra("user", user.getId());
            chat.putExtra("username", user.getUsername());
            context.startActivity(chat);
        }
    }
}

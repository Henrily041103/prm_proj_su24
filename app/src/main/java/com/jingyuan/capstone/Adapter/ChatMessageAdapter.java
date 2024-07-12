package com.jingyuan.capstone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.jingyuan.capstone.DTO.Firebase.ChatMessageFDTO;
import com.jingyuan.capstone.R;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessageFDTO, ChatMessageAdapter.MyViewHolder> {
    Context context;
    FirebaseAuth mAuth;

    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageFDTO> options, Context context) {
        super(options);
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ChatMessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recview_chat_message, parent, false);
        return new ChatMessageAdapter.MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatMessageFDTO messageDTO) {
        String message = messageDTO.getMessage();
        if (messageDTO.getSender().equalsIgnoreCase("a")) {
            holder.senderTextview.setText(message);
            holder.receiverLayout.setVisibility(View.GONE);
        } else {
            holder.receiverTextview.setText(message);
            holder.senderLayout.setVisibility(View.GONE);
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView senderTextview, receiverTextview;
        LinearLayout senderLayout, receiverLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextview = itemView.findViewById(R.id.chat_sender);
            receiverTextview = itemView.findViewById(R.id.chat_receiver);
            senderLayout = itemView.findViewById(R.id.chat_sender_layout);
            receiverLayout = itemView.findViewById(R.id.chat_receiver_layout);
        }
    }
}

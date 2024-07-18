package com.jingyuan.capstone.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.jingyuan.capstone.Controller.StoreChatActivity;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.DTO.View.ChatboxItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class StoreInboxItemAdapter extends RecyclerView.Adapter<StoreInboxItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChatboxItem> chatboxItemsList;

    public StoreInboxItemAdapter(Context context, ArrayList<ChatboxItem> chatboxItemsList) {
        this.context = context;
        this.chatboxItemsList = chatboxItemsList;
    }

    @NonNull
    @Override
    public StoreInboxItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recview_inbox_item, parent, false);
        return new StoreInboxItemAdapter.MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StoreInboxItemAdapter.MyViewHolder holder, int position) {
        ChatboxItem item = chatboxItemsList.get(position);
        holder.username.setText(item.getUsername());
        String thumbnailURL = item.getPfp();
        Glide.with(context).load(thumbnailURL).into(holder.pfp);
        holder.layout.setOnClickListener(v -> {
            Intent i = new Intent(context, StoreChatActivity.class);
            i.putExtra("chatDoc", item.getChatroomDoc());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return chatboxItemsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, last_message;
        ImageView pfp;
        LinearLayout layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            last_message = itemView.findViewById(R.id.last_message);
            pfp = itemView.findViewById(R.id.pfp);
            layout = itemView.findViewById(R.id.chat_item_layout);
        }
    }
}

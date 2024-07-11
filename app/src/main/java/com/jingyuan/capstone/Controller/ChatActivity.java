package com.jingyuan.capstone.Controller;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jingyuan.capstone.Adapter.ChatMessageAdapter;
import com.jingyuan.capstone.DTO.Firebase.ChatMessageFDTO;
import com.jingyuan.capstone.DTO.Firebase.ChatRoomFDTO;
import com.jingyuan.capstone.R;
import com.jingyuan.capstone.Utility.FirestoreUtilities;

public class ChatActivity extends AppCompatActivity {
    ImageButton backBtn, sendBtn;
    String shopDoc;
    RecyclerView chatSection;
    EditText messageInput;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirestoreUtilities utilities = new FirestoreUtilities();
    ChatRoomFDTO chatroom = new ChatRoomFDTO();
    String chatroomDoc;
    ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatSection = findViewById(R.id.chat_section_recycle_view);
        backBtn = findViewById(R.id.back_btn);
        sendBtn = findViewById(R.id.send_message_btn);
        messageInput = findViewById(R.id.chat_input);

        backBtn.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
        });
        sendBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            sendMessage(message);
        });
//        Intent i = getIntent();
//        shopDoc = i.getStringExtra("shopDoc");
        chatroomDoc = "shopDoc" + "a";
        setUpChatroom();
    }

    public void setUpChatroom() {
        //RETRIEVE CHAT ROOM
        utilities.getChatRoomRef(chatroomDoc).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    DocumentSnapshot snap = task.getResult();
                    chatroom = snap.toObject(ChatRoomFDTO.class);
                    chatroomDoc = snap.getId();
                } else {
                    chatroomDoc = "shopDoc" + "a";
                    chatroom.setUserDoc("a");
                    chatroom.setShopDoc("shopDoc");
                    chatroom.setTimeCreated(Timestamp.now());
                    utilities.getChatRoomRef(chatroomDoc).set(chatroom);
                }
            }
        });

        //SET UP CHAT ROOM WITH RECYCLE VIEW
        Query query = utilities.getMessagesCollection(chatroomDoc).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageFDTO> options = new FirestoreRecyclerOptions.Builder<ChatMessageFDTO>()
                .setQuery(query, ChatMessageFDTO.class).build();
        adapter = new ChatMessageAdapter(options, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        chatSection.setLayoutManager(layoutManager);
        chatSection.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatSection.smoothScrollToPosition(0);
            }
        });
    }

    public void sendMessage(String message) {
        ChatMessageFDTO messageFDTO = new ChatMessageFDTO("a", message, Timestamp.now());
        firestore.collection("Chatroom").document(chatroomDoc).collection("Chat")
                .add(messageFDTO).addOnCompleteListener(task -> {
                    messageInput.setText("");
                });
    }

}
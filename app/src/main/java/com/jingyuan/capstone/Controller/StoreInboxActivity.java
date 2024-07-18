package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jingyuan.capstone.Adapter.StoreInboxItemAdapter;
import com.jingyuan.capstone.DTO.Firebase.ChatRoomFDTO;
import com.jingyuan.capstone.DTO.Firebase.StoreFDTO;
import com.jingyuan.capstone.DTO.Firebase.UserDTO;
import com.jingyuan.capstone.DTO.View.ChatboxItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;
import java.util.Objects;

public class StoreInboxActivity extends AppCompatActivity {
    ArrayList<ChatboxItem> inboxList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String shopDoc;
    StoreInboxItemAdapter adapter;
    RecyclerView inboxListView;
    UserDTO user = new UserDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_inbox);
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        shopDoc = sf.getString("storeDoc", "error");
        inboxListView = findViewById(R.id.inbox_view);
        setUpInboxList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new StoreInboxItemAdapter(this, inboxList);
        inboxListView.setAdapter(adapter);
        inboxListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setUpInboxList() {
        db.collection("Store").document("FbkmI2MwoxutuqD3ZOSW").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String test = task.getResult().toObject(StoreFDTO.class).getName();
                Log.d("GACHI", test);
            }
        });

//        db.collection("Chatroom").whereEqualTo("shopDoc", shopDoc).get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot doc : task.getResult()) {
//                    ChatRoomFDTO chatRoomFDTO = doc.toObject(ChatRoomFDTO.class);
//                    String userDoc = chatRoomFDTO.getUserDoc();
//                    Log.d("GACHI", userDoc);
//                    addChatboxItem(userDoc, doc.getId());
//                }
//            }
//        });

    }

    private void addChatboxItem(String userDoc, String chatroomDoc) {
        db.collection("User").document(userDoc).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user = task.getResult().toObject(UserDTO.class);
                ChatboxItem item = new ChatboxItem();
                item.setChatroomDoc(chatroomDoc);
                assert user != null;
                item.setUsername(user.getUsername());
                item.setPfp(user.getPfp());
                item.setLastMessage("");
                inboxList.add(item);
            }
        });
    }
}
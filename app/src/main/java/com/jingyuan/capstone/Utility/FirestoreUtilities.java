package com.jingyuan.capstone.Utility;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jingyuan.capstone.DTO.Firebase.ChatRoomFDTO;
import com.jingyuan.capstone.DTO.Firebase.StoreFDTO;

public class FirestoreUtilities {
    private final FirebaseFirestore firestore =  FirebaseFirestore.getInstance();

    public DocumentReference getChatRoomRef(String chatroomDoc) {
        return firestore.collection("Chatroom").document(chatroomDoc);
    }

    public DocumentReference getUserRef(String userDoc) {
        return firestore.collection("User").document(userDoc);
    }

    public CollectionReference getMessagesCollection(String chatroomDoc) {
        return getChatRoomRef(chatroomDoc).collection("Chat");
    }

    public DocumentReference getCategoryRef(String catDoc) {
        return firestore.collection("Category").document(catDoc);
    }
}

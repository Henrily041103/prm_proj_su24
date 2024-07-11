package com.jingyuan.capstone.Utility;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jingyuan.capstone.DTO.Firebase.ChatRoomFDTO;

public class FirestoreUtilities {
    private final FirebaseFirestore firestore =  FirebaseFirestore.getInstance();

    public DocumentReference getChatRoomRef(String chatroomDoc) {
        return firestore.collection("Chatroom").document(chatroomDoc);
    }

    public CollectionReference getMessagesCollection(String chatroomDoc) {
        return getChatRoomRef(chatroomDoc).collection("Chat");
    }
}

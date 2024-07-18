package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jingyuan.capstone.DTO.Firebase.StoreFDTO;
import com.jingyuan.capstone.R;

public class StoreHomeActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StoreFDTO store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String userDoc = sf.getString("uid","error");
        Log.d("GACHI", userDoc);
        db.collection("Store").whereEqualTo("ownerDoc", userDoc).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot docSnap = task.getResult().getDocuments().get(0);
                store = docSnap.toObject(StoreFDTO.class);
                assert store != null;
                store.setDoc(docSnap.getId());
                SharedPreferences.Editor editor = sf.edit();
                editor.putString("storeDoc", store.getDoc());
                editor.putString("name", store.getName());
                editor.apply();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button addProductBtn = findViewById(R.id.add_product_btn);
        addProductBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StoreProductActivity.class);
            startActivity(i);
        });
    }
}
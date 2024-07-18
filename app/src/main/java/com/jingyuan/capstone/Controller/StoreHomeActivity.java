package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jingyuan.capstone.Adapter.ShopPrctRecViewAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.Firebase.StoreFDTO;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class StoreHomeActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StoreFDTO store = new StoreFDTO();
    ArrayList<ProductItem> productItemsList = new ArrayList<>();
    ShopPrctRecViewAdapter productAdapter;
    RecyclerView productRecyclerView;
    Button addProductBtn, inboxBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        productRecyclerView = findViewById(R.id.store_product_view);
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String userDoc = sf.getString("uid", "error");

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
        setupProductList();
        addProductBtn = findViewById(R.id.add_product_btn);
        inboxBtn = findViewById(R.id.inbox_btn);
        addProductBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StoreProductActivity.class);
            startActivity(i);
        });
        inboxBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), StoreInboxActivity.class);
            startActivity(i);
        });
    }

    private void setupProductList() {
        db.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    ProductFDTO productFDTO = docSnap.toObject(ProductFDTO.class);
                    if (productFDTO.getStore().getDoc().equalsIgnoreCase(store.getDoc())) {
                        ProductItem itemDTO = getProductItem(docSnap, productFDTO);
                        productItemsList.add(itemDTO);
                    }
                }
                productAdapter = new ShopPrctRecViewAdapter(this, productItemsList);
                productRecyclerView.setAdapter(productAdapter);
                productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        });
    }

    @NonNull
    private static ProductItem getProductItem(QueryDocumentSnapshot docSnap, ProductFDTO productFDTO) {
        ProductItem itemDTO = new ProductItem();
        itemDTO.setDoc(docSnap.getId());
        CategoryFDTO cat = productFDTO.getCategory();
        itemDTO.setCategory(cat.getName());
        itemDTO.setName(productFDTO.getName());
        itemDTO.setPrice(productFDTO.getPrice());
        itemDTO.setThumbnail(productFDTO.getThumbnail());
        String status = "Available";
        if (productFDTO.getStock() == 0) status = "Out of stock";
        itemDTO.setStatus(status);
        return itemDTO;
    }
}
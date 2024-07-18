package com.jingyuan.capstone.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductStoreAttrFDTO;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    static int PERMISSION_CODE = 100;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sf;
    ProductFDTO productFDTO;
    TextView label, price, des, store, cart_notice;
    ImageView thumbnail;
    ImageButton backBtn, cartBtn, contactBtn;
    Button addToCartBtn;
    String docData;
    ProductStoreAttrFDTO pStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CODE);
        }
        label = findViewById(R.id.label);
        price = findViewById(R.id.price);
        des = findViewById(R.id.des);
        store = findViewById(R.id.store);
        backBtn = findViewById(R.id.back);
        thumbnail = findViewById(R.id.thumbnail);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        contactBtn = findViewById(R.id.contact_btn);
        cartBtn = findViewById(R.id.cart);
        cart_notice = findViewById(R.id.cart_notice);
        Intent i = getIntent();
        docData = i.getStringExtra("doc");
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference docRef = db.collection("Product").document(docData);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    productFDTO = document.toObject(ProductFDTO.class);
                    assert productFDTO != null;
                    label.setText(productFDTO.getName());
                    price.setText(productFDTO.getPrice() + " USD");
                    des.setText(productFDTO.getDescription());
                    Glide.with(DetailActivity.this).load(productFDTO.getThumbnail()).into(thumbnail);
                    pStore = productFDTO.getStore();
                    store.setText("Store: " + pStore.getName());
                }
            }
        });

        if (checkAddedToCart(docData)) {
            Log.d("GACHI", "Added or not: "+ checkAddedToCart(docData));
            updateStatus();
        } else {
            addToCartBtn.setOnClickListener(v -> DetailActivity.this.addToCart());
        }

        backBtn.setOnClickListener(v -> DetailActivity.this.finish());

        cartBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, CartActivity.class);
            startActivity(i);
            finish();
        });

        contactBtn.setOnClickListener(v -> {
            Intent i = new Intent(DetailActivity.this, ChatActivity.class);
            i.putExtra("shopDoc", productFDTO.getStore().getDoc());
            startActivity(i);
        });
    }

    public boolean checkAddedToCart(String doc) {
        sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (plainJsonString.equalsIgnoreCase("empty")) {
            return false;
        }
        Gson gson = new Gson();
        Cart cart = gson.fromJson(plainJsonString, Cart.class);
        for (CartItem item : cart.getItems()) {
            if (item.getDoc().equalsIgnoreCase(doc)) return true;
        }
        return false;
    }

    public void addToCart() {
        Cart cart = new Cart();
        ArrayList<CartItem> cartList = new ArrayList<>();
        sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (!plainJsonString.equalsIgnoreCase("empty")) {
            Gson gson = new Gson();
            cart = gson.fromJson(plainJsonString, Cart.class);
            cartList = cart.getItems();
        }
        CartItem item = new CartItem(docData, productFDTO.getName(),
                1, productFDTO.getPrice(), productFDTO.getThumbnail());
        cartList.add(item);
        cart.setItems(cartList);
        SharedPreferences.Editor editor = sf.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        editor.putString("Cart", json);
        editor.putBoolean("cart_status", false);
        editor.apply();
        updateStatus();
    }

    public void updateStatus() {
        addToCartBtn.setVisibility(View.GONE);
        cart_notice.setVisibility(View.VISIBLE);
    }

}
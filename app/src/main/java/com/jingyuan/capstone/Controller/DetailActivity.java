package com.jingyuan.capstone.Controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    static int PERMISSION_CODE = 100;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sf;
    ProductFDTO productFDTO;
    TextView label, price, des, store, cart_notice;
    ImageView thumbnail;
    ImageButton backBtn, cartBtn;
    Button addToCartBtn;
    String docData;
    ProductStoreAttrFDTO pStore;
    private int cartItemCount = 0;

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

        // Update the cart item count
        updateCartItemCount();
    }

    public boolean checkAddedToCart(String doc) {
        sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (plainJsonString.equalsIgnoreCase("empty")) {
            return false;
        }
        Gson gson = new Gson();
        Cart cart = gson.fromJson(plainJsonString, Cart.class);
        if (cart == null || cart.getItems() == null) {
            return false;
        }
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
            if (cart == null) {
                cart = new Cart();
            }
            if (cart.getItems() != null) {
                cartList = cart.getItems();
            }
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

    private void updateCartItemCount() {
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (!plainJsonString.equalsIgnoreCase("empty")) {
            Gson gson = new Gson();
            Cart cart = gson.fromJson(plainJsonString, Cart.class);
            List<CartItem> cartItems = cart.getItems();
            cartItemCount = (cartItems != null) ? cartItems.size() : 0;
            TextView cartBadge = new TextView(this);
            cartBadge.setText(String.valueOf(cartItemCount));
            cartBadge.setTextColor(Color.WHITE);
            cartBadge.setTextSize(12);
            cartBadge.setPadding(8, 4, 8, 4);
            cartBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.cart_badge));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_TOP, cartBtn.getId());
            params.addRule(RelativeLayout.ALIGN_END, cartBtn.getId());
            params.setMargins(0, 8, 8, 0);
            ((ViewGroup) cartBtn.getParent()).addView(cartBadge, params);
        } else {
            TextView cartBadge = ((ViewGroup) cartBtn.getParent()).findViewById(R.id.cart_badge);
            if (cartBadge != null) {
                ((ViewGroup) cartBtn.getParent()).removeView(cartBadge);
            }
        }
    }
}

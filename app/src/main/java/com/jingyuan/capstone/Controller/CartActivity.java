package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.jingyuan.capstone.Adapter.CartItemAdapter;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {
    TextView cartStatusNotice;
    SharedPreferences sf;
    RecyclerView cartItems;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartStatusNotice = findViewById(R.id.cart_status_display);
        cartItems = findViewById(R.id.cart_items_list);
        backBtn = findViewById(R.id.back);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (plainJsonString.equalsIgnoreCase("empty")) {
            cartStatusNotice.setVisibility(View.VISIBLE);
        } else {
            cartItems.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            Cart cart = gson.fromJson(plainJsonString, Cart.class);
            ArrayList<CartItem> cartList = cart.getItems();
            CartItemAdapter adapter = new CartItemAdapter(this, cartList);
            cartItems.setAdapter(adapter);
            cartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
        backBtn.setOnClickListener(v -> {
            Intent i = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(i);
        });
    }

}
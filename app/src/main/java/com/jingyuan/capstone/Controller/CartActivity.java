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
    TextView cartStatusNotice, cartTotalText;
    SharedPreferences sf;
    RecyclerView cartItems;
    ImageButton backBtn;
    ImageButton clearCartBtn;
    CartItemAdapter adapter;
    ArrayList<CartItem> cartItemsList;
    int cartTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartStatusNotice = findViewById(R.id.cart_status_display);
        cartTotalText = findViewById(R.id.cart_total);
        cartItems = findViewById(R.id.cart_items_list);
        backBtn = findViewById(R.id.back);
        clearCartBtn = findViewById(R.id.clear_cart);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String plainJsonString = sf.getString("Cart", "empty");
        if (plainJsonString.equalsIgnoreCase("empty")) {
            cartStatusNotice.setVisibility(View.VISIBLE);
            clearCartBtn.setVisibility(View.GONE);
        } else {
            cartItems.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            Cart cart = gson.fromJson(plainJsonString, Cart.class);
            cartItemsList = cart.getItems();
            adapter = new CartItemAdapter(this, cartItemsList, clearCartBtn, new CartItemAdapter.OnCartItemUpdateListener() {
                @Override
                public void onCartItemRemoved() {
                    updateCartTotal();
                }

                @Override
                public void onCartItemUpdated() {
                    updateCartTotal();
                }
            });
            cartItems.setAdapter(adapter);
            cartItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            clearCartBtn.setVisibility(View.VISIBLE);
            clearCartBtn.setOnClickListener(v -> {
                clearCart();
            });
            updateCartTotal();
        }
        backBtn.setOnClickListener(v -> {
            Intent i = new Intent(CartActivity.this, HomeActivity.class);
            startActivity(i);
        });
    }

    private void clearCart() {
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("Cart", "empty");
        editor.apply();
        cartItemsList.clear();
        adapter.notifyDataSetChanged();
        cartStatusNotice.setVisibility(View.VISIBLE);
        clearCartBtn.setVisibility(View.GONE);
        updateCartTotal();
    }

    private void updateCartTotal() {
        // Calculate the total cart amount and update the UI
        cartTotal = 0;
        for (CartItem item : cartItemsList) {
            cartTotal += item.getPrice() * item.getQuantity();
        }
        cartTotalText.setText("Total: " + cartTotal + " USD");
    }
}

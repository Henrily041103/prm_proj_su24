package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.jingyuan.capstone.Adapter.RecyclerViewAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;
import com.jingyuan.capstone.Utility.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String CHANNEL_ID = "NoticeChannelID";
    public static final String CHANNEL_NAME = "Notice name";
    public static final String CHANNEL_DESC = "Description";
    private int cartItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NotificationHelper.createNotificationChannel(this, CHANNEL_ID, CHANNEL_NAME, CHANNEL_DESC);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageButton shopBtn = findViewById(R.id.shop);
        shopBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
            startActivity(intent);
        });

        ImageButton cartBtn = findViewById(R.id.cart);
        cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<ProductItem> productItemsList = new ArrayList<>();
        db.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    ProductFDTO productFDTO = docSnap.toObject(ProductFDTO.class);
                    ProductItem itemDTO = getProductItemDTO(docSnap, productFDTO);
                    productItemsList.add(itemDTO);
                }
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, productItemsList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        });

        updateCartItemCount();
    }

    private static ProductItem getProductItemDTO(QueryDocumentSnapshot docSnap, ProductFDTO productFDTO) {
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

    private void updateCartItemCount() {
        ImageButton cartBtn = findViewById(R.id.cart);
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

            // Update the app icon badge
            NotificationHelper.updateAppIconBadge(this, cartItemCount);
        } else {
            TextView cartBadge = ((ViewGroup) cartBtn.getParent()).findViewById(R.id.cart_badge);
            if (cartBadge != null) {
                ((ViewGroup) cartBtn.getParent()).removeView(cartBadge);
            }
            // Clear the app icon badge
            NotificationHelper.clearAppIconBadge(this);
        }
    }
}

package com.jingyuan.capstone.Controller;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.jingyuan.capstone.Adapter.RecyclerViewAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String CHANNEL_ID = "CartNotificationChannel";
    public static final String CHANNEL_NAME = "Cart Notifications";
    public static final String CHANNEL_DESC = "Notifications for cart updates";
    private int cartItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Create the notification channel
        createNotificationChannel();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESC);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
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

        NavigationView nv = findViewById(R.id.nav_view);
        View header = nv.getHeaderView(0);
        TextView navUsername = header.findViewById(R.id.username);
        TextView navEmail = header.findViewById(R.id.email);
        ImageView pfp = header.findViewById(R.id.pfp);
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        navUsername.setText(sf.getString("username","error"));
        navEmail.setText(sf.getString("email","error"));
        String pfpURL = sf.getString("pfp","error");
        Glide.with(HomeActivity.this).load(pfpURL).placeholder(R.drawable.sam).dontAnimate().into(pfp);

        // Update the cart item count
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

    protected void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
            }
        });
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

            // Display a notification if the cart item count has changed
            if (cartItemCount > 0) {
                displayCartNotification(cartItemCount);
            }
        } else {
            TextView cartBadge = ((ViewGroup) cartBtn.getParent()).findViewById(R.id.cart_badge);
            if (cartBadge != null) {
                ((ViewGroup) cartBtn.getParent()).removeView(cartBadge);
            }
        }
    }
    private void displayCartNotification(int cartItemCount) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cart)
                .setContentTitle("Cart Update")
                .setContentText("You have " + cartItemCount + " item(s) in your cart.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESC;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

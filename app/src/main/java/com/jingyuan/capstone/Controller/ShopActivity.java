package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.jingyuan.capstone.Adapter.CategoryAdapter;
import com.jingyuan.capstone.Adapter.RecyclerViewAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.View.Cart;
import com.jingyuan.capstone.DTO.View.CartItem;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;
    RecyclerView productRecyclerView, categoryRecyclerView;
    EditText searchView;
    ImageButton filterButton, sortButton, cartBtn;
    ArrayList<ProductItem> productItemsList;
    ArrayList<CategoryFDTO> categoryDTOList;
    RecyclerViewAdapter productAdapter;
    CategoryAdapter categoryAdapter;
    String selectedCategory = "All";
    int sortOption = 0; // 0 - Default, 1 - Price (Low to High), 2 - Price (High to Low)
    private int cartItemCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ImageButton homeBtn = findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        searchView = findViewById(R.id.search_view);
        filterButton = findViewById(R.id.filter_btn);
        sortButton = findViewById(R.id.sort_btn);
        cartBtn = findViewById(R.id.cart);
        productRecyclerView = findViewById(R.id.product_recycler_view);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productItemsList = new ArrayList<>();
        categoryDTOList = new ArrayList<>();

        cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupProductList();
        setupCategoryList();

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filterButton.setOnClickListener(v -> {
            showFilterOptions();
        });

        sortButton.setOnClickListener(v -> {
            showSortingOptions();
        });

        // Update the cart item count
        updateCartItemCount();
    }

    private void setupProductList() {
        db.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    ProductFDTO productFDTO = docSnap.toObject(ProductFDTO.class);
                    ProductItem itemDTO = getProductItemDTO(docSnap, productFDTO);
                    productItemsList.add(itemDTO);
                }
                productAdapter = new RecyclerViewAdapter(this, productItemsList);
                productRecyclerView.setAdapter(productAdapter);
                productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        });
    }

    private void setupCategoryList() {
        db.collection("Category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                CategoryFDTO allCategory = new CategoryFDTO();
                allCategory.setName("All");
                allCategory.setThumbnail("https://firebasestorage.googleapis.com/v0/b/capstone-c62ee.appspot.com/o/select_all.png?alt=media");
                categoryDTOList.add(allCategory);
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    CategoryFDTO categoryFDTO = docSnap.toObject(CategoryFDTO.class);
                    categoryDTOList.add(categoryFDTO);
                }
                categoryAdapter = new CategoryAdapter(this, categoryDTOList);
                categoryRecyclerView.setAdapter(categoryAdapter);
                categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                categoryAdapter.setOnCategoryClickListener(category -> {
                    selectedCategory = category.getName();
                    filterProducts(searchView.getText().toString().toLowerCase(Locale.getDefault()));
                });
            }
        });
    }


    private void filterProducts(String query) {
        ArrayList<ProductItem> filteredItems = new ArrayList<>();
        for (ProductItem item : productItemsList) {
            if (item.getName().toLowerCase(Locale.getDefault()).contains(query) && (selectedCategory.equals("All") || item.getCategory().equalsIgnoreCase(selectedCategory))) {
                filteredItems.add(item);
            }
        }
        sortProducts(filteredItems);
        productAdapter.updateData(filteredItems);
    }

    private void showFilterOptions() {
        // Show a dialog or bottom sheet to allow the user to select a filtering option
        // Update the selectedCategory variable based on the user's selection
        // Call the filterProducts() method to filter the productItemsList
        // Update the productAdapter with the filtered list
    }


    private void showSortingOptions() {
        // Show a dialog or bottom sheet to allow the user to select a sorting option
        final CharSequence[] items = {"Default", "Price (Low to High)", "Price (High to Low)"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sort by");
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0:
                    // Default sorting (no sorting)
                    sortOption = 0;
                    sortProducts(productItemsList);
                    break;
                case 1:
                    // Sort by price (low to high)
                    sortOption = 1;
                    sortByPriceLowToHigh(productItemsList);
                    break;
                case 2:
                    // Sort by price (high to low)
                    sortOption = 2;
                    sortByPriceHighToLow(productItemsList);
                    break;
            }
            productAdapter.updateData(productItemsList);
        });
        builder.show();
    }

    private void sortProducts(ArrayList<ProductItem> items) {
        // No sorting, keep the default order
    }

    private void sortByPriceLowToHigh(ArrayList<ProductItem> items) {
        Collections.sort(items, Comparator.comparingInt(ProductItem::getPrice));
    }

    private void sortByPriceHighToLow(ArrayList<ProductItem> items) {
        Collections.sort(items, (a, b) -> Integer.compare(b.getPrice(), a.getPrice()));
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

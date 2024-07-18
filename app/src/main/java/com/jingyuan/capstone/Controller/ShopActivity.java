package com.jingyuan.capstone.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jingyuan.capstone.Adapter.CategoryAdapter;
import com.jingyuan.capstone.Adapter.RecyclerViewAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;
    RecyclerView productRecyclerView, categoryRecyclerView;
    EditText searchView;
    ImageButton filterButton, sortButton;
    ArrayList<ProductItem> productItemsList;
    ArrayList<CategoryFDTO> categoryDTOList;
    RecyclerViewAdapter productAdapter;
    CategoryAdapter categoryAdapter;
    String selectedCategory = "All";
    int sortOption = 0; // 0 - Default, 1 - Price (Low to High), 2 - Price (High to Low)

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
        productRecyclerView = findViewById(R.id.product_recycler_view);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productItemsList = new ArrayList<>();
        categoryDTOList = new ArrayList<>();

        ImageButton cartBtn = findViewById(R.id.cart);
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
}

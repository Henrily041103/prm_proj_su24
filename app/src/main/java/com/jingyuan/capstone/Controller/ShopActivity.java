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
import java.util.Locale;

public class ShopActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;
    RecyclerView productRecyclerView, categoryRecyclerView;
    EditText searchView;
    ImageButton filterButton;
    ArrayList<ProductItem> productItemsList;
    ArrayList<CategoryFDTO> categoryDTOList;
    RecyclerViewAdapter productAdapter;
    CategoryAdapter categoryAdapter;

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
        productRecyclerView = findViewById(R.id.product_recycler_view);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        productItemsList = new ArrayList<>();
        categoryDTOList = new ArrayList<>();
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
            // Show filter options
            Toast.makeText(this, "Filter button clicked", Toast.LENGTH_SHORT).show();
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
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    CategoryFDTO categoryFDTO = docSnap.toObject(CategoryFDTO.class);
                    categoryDTOList.add(categoryFDTO);
                }
                categoryAdapter = new CategoryAdapter(this, categoryDTOList);
                categoryRecyclerView.setAdapter(categoryAdapter);
                categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            }
        });
    }

    private void filterProducts(String query) {
        ArrayList<ProductItem> filteredItems = new ArrayList<>();
        for (ProductItem item : productItemsList) {
            if (item.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                filteredItems.add(item);
            }
        }
        productAdapter.updateData(filteredItems);
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

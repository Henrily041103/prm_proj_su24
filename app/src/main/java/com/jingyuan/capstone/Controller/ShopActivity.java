package com.jingyuan.capstone.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jingyuan.capstone.Adapter.CategoryAdapter;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.View.ProductItem;
import com.jingyuan.capstone.R;
import com.jingyuan.capstone.Adapter.RecyclerViewAdapter;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ImageButton homeBtn = findViewById(R.id.home_btn);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //PRODUCT
        ArrayList<ProductItem> productItemsList = new ArrayList<>();
        db.collection("Product").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    ProductFDTO productFDTO = docSnap.toObject(ProductFDTO.class);
                    ProductItem itemDTO = getProductItemDTO(docSnap, productFDTO);
                    productItemsList.add(itemDTO);
                }
                RecyclerView recyclerView = findViewById(R.id.product_recycler_view);
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, productItemsList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
        });

        //CATEGORY
        ArrayList<CategoryFDTO> categoryDTOList = new ArrayList<>();
        db.collection("Category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot docSnap : task.getResult()) {
                    CategoryFDTO categoryFDTO = docSnap.toObject(CategoryFDTO.class);
                    categoryDTOList.add(categoryFDTO);
                }
                RecyclerView categoryRecView = findViewById(R.id.category_recycler_view);
                CategoryAdapter adapter = new CategoryAdapter(this, categoryDTOList);
                categoryRecView.setAdapter(adapter);
                categoryRecView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            }
        });

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
package com.jingyuan.capstone.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductStoreAttrFDTO;
import com.jingyuan.capstone.R;
import java.util.ArrayList;
import java.util.Objects;

public class StoreProductActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText nameInput, desInput, priceInput, stockInput;
    AutoCompleteTextView catInput;
    Button addProductBtn;
    ArrayList<CategoryFDTO> catList = new ArrayList<>();
    ArrayList<String> catStringList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ProductFDTO product = new ProductFDTO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        nameInput = findViewById(R.id.product_name_input);
        desInput = findViewById(R.id.product_description_input);
        priceInput = findViewById(R.id.product_price_input);
        stockInput = findViewById(R.id.product_stock_input);
        addProductBtn = findViewById(R.id.add_product_btn);
        catInput = findViewById(R.id.product_category_input);
        db.collection("Category").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                CategoryFDTO cat = document.toObject(CategoryFDTO.class);
                catList.add(cat);
                catStringList.add(cat.getName());
            }
            adapter = new ArrayAdapter<>(this, R.layout.category_items, catStringList);
            catInput.setAdapter(adapter);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addProductBtn.setOnClickListener(v -> {
            addProduct();
        });
    }

    private void addProduct() {
        SharedPreferences sf = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        CategoryFDTO selectedCat = retrieveCat(Objects.requireNonNull(catInput.getText()).toString());
        product.setCategory(selectedCat);
        product.setName(Objects.requireNonNull(nameInput.getText()).toString());
        product.setDescription(Objects.requireNonNull(desInput.getText()).toString());
        product.setPrice(Integer.parseInt(Objects.requireNonNull(priceInput.getText()).toString()));
        product.setStock(Integer.parseInt(Objects.requireNonNull(stockInput.getText()).toString()));
        ProductStoreAttrFDTO store = new ProductStoreAttrFDTO();
        store.setDoc(sf.getString("storeDoc", "error"));
        store.setName(sf.getString("name", "error"));
        product.setStore(store);
        product.setThumbnail("");

        db.collection("Product").add(product).addOnSuccessListener(docRef -> {
            Toast.makeText(StoreProductActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), StoreHomeActivity.class);
            startActivity(i);
        }).addOnFailureListener(e -> {
            Toast.makeText(StoreProductActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
        });
    }

    //UTILITIES
    private CategoryFDTO retrieveCat(String selectedString) {
        for (CategoryFDTO item : catList) {
            if (item.getName().equalsIgnoreCase(selectedString)) {
                return item;
            }
        }
        return null;
    }
}
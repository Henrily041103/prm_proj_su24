package com.jingyuan.capstone.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.R;

import java.util.Objects;

public class AdminProductActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextInputEditText nameInput, desInput, priceInput, stockInput;
    Button addProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product);
        nameInput = findViewById(R.id.product_name_input);
        desInput = findViewById(R.id.product_description_input);
        priceInput = findViewById(R.id.product_price_input);
        stockInput = findViewById(R.id.product_stock_input);
        addProductBtn = findViewById(R.id.add_product_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addProductBtn.setOnClickListener(v -> {
            addProduct();
        });
    }

    private void addProduct() {
        ProductFDTO product = new ProductFDTO();
        product.setName(Objects.requireNonNull(nameInput.getText()).toString());
        product.setDescription(Objects.requireNonNull(desInput.getText()).toString());
        product.setPrice(Integer.parseInt(Objects.requireNonNull(priceInput.getText()).toString()));
        product.setStock(Integer.parseInt(Objects.requireNonNull(stockInput.getText()).toString()));
        db.collection("Product").add(product).addOnSuccessListener(documentReference -> {
            Toast.makeText(AdminProductActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), AdminHomeActivity.class);
            startActivity(i);
        }).addOnFailureListener(e -> {
            Toast.makeText(AdminProductActivity.this, "Unexpected error", Toast.LENGTH_SHORT).show();
        });
    }
}
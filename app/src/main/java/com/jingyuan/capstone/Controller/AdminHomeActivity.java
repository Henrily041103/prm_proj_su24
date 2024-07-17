package com.jingyuan.capstone.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.jingyuan.capstone.R;

public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button addProductBtn = findViewById(R.id.add_product_btn);
        addProductBtn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AdminProductActivity.class);
            startActivity(i);
        });
    }
}
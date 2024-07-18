package com.jingyuan.capstone.Controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jingyuan.capstone.DTO.Firebase.CategoryFDTO;
import com.jingyuan.capstone.DTO.Firebase.ProductFDTO;
import com.jingyuan.capstone.R;
import java.util.ArrayList;
import java.util.Objects;

public class StorePrctDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    ActivityResultLauncher<Intent> launcher;
    AutoCompleteTextView catInput;
    TextInputEditText nameInput, desInput, priceInput, stockInput;
    Button editProductBtn, updateProductBtn, cancelUpdate;
    ImageButton uploadImgBtn, confirmUploadBtn;
    ImageView previewThumb;
    LinearLayout updateSectionLayout;
    ArrayList<CategoryFDTO> catList = new ArrayList<>();
    ArrayList<String> catStringList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Uri imageUri;
    String imageDownloadUrl, docData;
    ProductFDTO product = new ProductFDTO();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_prct_details);
        nameInput = findViewById(R.id.product_name_input);
        desInput = findViewById(R.id.product_description_input);
        priceInput = findViewById(R.id.product_price_input);
        stockInput = findViewById(R.id.product_stock_input);
        editProductBtn = findViewById(R.id.edit_product_btn);
        updateProductBtn = findViewById(R.id.update_product_btn);
        cancelUpdate = findViewById(R.id.cancel_update_btn);
        catInput = findViewById(R.id.product_category_input);
        uploadImgBtn = findViewById(R.id.upload_image_btn);
        confirmUploadBtn = findViewById(R.id.confirm_upload_image_btn);
        previewThumb = findViewById(R.id.preview_thumbnail);
        updateSectionLayout = findViewById(R.id.update_layout);

        Intent i = getIntent();

        docData = i.getStringExtra("doc").trim();
        assert docData != null;
        db.collection("Product").document(docData).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                product = document.toObject(ProductFDTO.class);
                assert product != null;
                nameInput.setText(product.getName());
                desInput.setText(product.getDescription());
                priceInput.setText(product.getPrice()+"");
                stockInput.setText(product.getStock()+"");
                catInput.setText(product.getCategory().getName());
                Glide.with(StorePrctDetailsActivity.this).load(product.getThumbnail()).into(previewThumb);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpCategory();
        adapter = new ArrayAdapter<>(this, R.layout.category_items, catStringList);
        editProductBtn.setOnClickListener(v -> {
            switchEditMode(true);
        });

        updateProductBtn.setOnClickListener(v -> {
            updateProductDetails();
            switchEditMode(false);
            confirmUploadBtn.setVisibility(View.GONE);
            uploadImgBtn.setVisibility(View.VISIBLE);
        });

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        imageUri = data.getData();
                        previewThumb.setImageURI(imageUri);
                        confirmUploadBtn.setVisibility(View.VISIBLE);
                        uploadImgBtn.setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        uploadImgBtn.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(i);
        });

        confirmUploadBtn.setOnClickListener(v -> {
            uploadImageToDatabase(imageUri);
            confirmUploadBtn.setVisibility(View.INVISIBLE);
            uploadImgBtn.setVisibility(View.VISIBLE);
        });

        cancelUpdate.setOnClickListener(v -> {
            switchEditMode(false);
        });
    }

    private CategoryFDTO retrieveCat(String selectedString) {
        for (CategoryFDTO item : catList) {
            if (item.getName().equalsIgnoreCase(selectedString)) {
                return item;
            }
        }
        return null;
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    private void updateProductDetails() {
        db.collection("Product").document(docData).update(
                        "name", Objects.requireNonNull(nameInput.getText()).toString(),
                        "description", Objects.requireNonNull(desInput.getText()).toString(),
                        "price", Integer.parseInt(Objects.requireNonNull(priceInput.getText()).toString()),
                        "stock", Integer.parseInt(Objects.requireNonNull(stockInput.getText()).toString()),
                        "category", retrieveCat(Objects.requireNonNull(catInput.getText()).toString())
                ).addOnSuccessListener(docRef ->
                        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unexpected error", Toast.LENGTH_SHORT).show());
    }

    private void uploadImageToDatabase(Uri uri) {
        final StorageReference imgRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        imgRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            imgRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                imageDownloadUrl = uri1.toString();
                updateThumbnail(imageDownloadUrl);
            });
        });
    }

    private void updateThumbnail(String url) {
        db.collection("Product").document(docData).update("thumbnail", url)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unexpected error", Toast.LENGTH_SHORT).show());
    }

    private void switchEditMode(Boolean editMode) {
        nameInput.setEnabled(editMode);
        desInput.setEnabled(editMode);
        priceInput.setEnabled(editMode);
        stockInput.setEnabled(editMode);
        catInput.setEnabled(editMode);
        if (editMode) {
            catInput.setAdapter(adapter);
            editProductBtn.setVisibility(View.GONE);
            updateSectionLayout.setVisibility(View.VISIBLE);
        } else {
            catInput.setText(product.getCategory().getName());
            editProductBtn.setVisibility(View.VISIBLE);
            updateSectionLayout.setVisibility(View.GONE);
        }
    }

    private void setUpCategory() {
        db.collection("Category").get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                CategoryFDTO cat = document.toObject(CategoryFDTO.class);
                catList.add(cat);
                catStringList.add(cat.getName());
            }
        });
    }
}
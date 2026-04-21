package com.phghuy.calmihome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.Product;
import com.phghuy.calmihome.model.SessionManager;

import java.io.Serializable;

public class DetailProductActivity extends AppCompatActivity {
    private int valueq = 1, maxQty = 0, idproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ApiCaller apiCaller = new ApiCaller(this);
        TextView textView = findViewById(R.id.nameproduct);
        ImageView img = findViewById(R.id.imgProduct);
        TextView description = findViewById(R.id.description);
        TextView price = findViewById(R.id.price);
        TextView tvValue = findViewById(R.id.tvValue);
        Button btnIncrement = findViewById(R.id.btnIncrement);
        Button btnDecrement = findViewById(R.id.btnDecrement);
        TextView addcart = findViewById(R.id.addcart);
        ImageView backBtn = findViewById(R.id.backBtn);
        ImageView cartBtn = findViewById(R.id.cartBtn);

        Product selectedProduct = null;
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("selected product")) {
                selectedProduct = (Product) getIntent().getSerializableExtra("selected product");
            }
        }

        if (selectedProduct != null) {
            textView.setText(selectedProduct.getName());
            description.setText(selectedProduct.getDescription());
            price.setText(selectedProduct.getPrice() + " đ");

            Bitmap bitmap = convertImage(selectedProduct.getImageBase64());
            if (bitmap != null) {
                img.setImageBitmap(bitmap);
            }
            idproduct = selectedProduct.getId();
            maxQty = selectedProduct.getQty();
        }

        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueq < maxQty) {
                    valueq++;
                    tvValue.setText(String.valueOf(valueq));
                }
            }
        });

        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valueq > 1) {
                    valueq--;
                    tvValue.setText(String.valueOf(valueq));
                }
            }
        });

        addcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiCaller.addCart("/products/addtocart/" + String.valueOf(SessionManager.userId) + "/" +
                        String.valueOf(idproduct) + "/" + String.valueOf(valueq), new ApiCaller.OnRawResponse() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(DetailProductActivity.this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(DetailProductActivity.this, "Lỗi! Kiểm tra lại API", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backBtn.setOnClickListener(v -> finish());
        cartBtn.setOnClickListener(v -> startActivity(new Intent(DetailProductActivity.this, CartActivity.class)));
    }

    public Bitmap convertImage(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            Log.e("ConvertImage", "Base64 string is null or empty");
            return null;
        }
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            Log.e("ConvertImage", "Base64 decode error: " + e.getMessage());
        } catch (Exception e) {
            Log.e("ConvertImage", "Unknown error: " + e.getMessage());
        }
        return null;
    }
}

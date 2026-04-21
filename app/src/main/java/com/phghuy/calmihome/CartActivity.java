package com.phghuy.calmihome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.Cart;
import com.phghuy.calmihome.model.Product;
import com.phghuy.calmihome.model.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private int TotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout cartContainer = findViewById(R.id.cartcontainer);
        TextView totalprice = findViewById(R.id.totalprice);
        TextView btn_pay = findViewById(R.id.btn_pay);
        ImageView backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        ApiCaller apiCaller = new ApiCaller(this);
        getCart(new OnCartLoaded() {
            @Override
            public void onLoaded(List<Cart> cartList) {
                cartContainer.removeAllViews();
                TotalPrice = 0;
                for (Cart p : cartList) {
                    View itemCart = inflater.inflate(R.layout.layout_cart_item, cartContainer, false);
                    ImageView pimg = itemCart.findViewById(R.id.imgProduct);
                    TextView pname = itemCart.findViewById(R.id.nameproduct);
                    TextView pprice = itemCart.findViewById(R.id.price);
                    TextView cqty = itemCart.findViewById(R.id.qty);
                    TextView ctotalprice = itemCart.findViewById(R.id.item_totalprice);
                    ImageView btn_delete = itemCart.findViewById(R.id.delete);

                    Bitmap bitmap = convertImage(p.getProduct().getImageBase64());
                    if (bitmap != null) {
                        pimg.setImageBitmap(bitmap);
                    }
                    pname.setText(p.getProduct().getName());
                    pprice.setText(p.getProduct().getPrice() + " đ");
                    cqty.setText("Tổng số lượng: " + p.getQty());
                    
                    int itemTotal = p.getQty() * Integer.parseInt(p.getProduct().getPrice());
                    ctotalprice.setText(String.valueOf(itemTotal) + " đ");

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Xác nhận xóa")
                                    .setMessage("Bạn có chắc chắn muốn xóa mục này không?")
                                    .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            apiCaller.DeleteItem("/cart_items/remove/" + p.getId(), new ApiCaller.OnRawResponse() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    Toast.makeText(view.getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                                                    recreate();
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Toast.makeText(view.getContext(), "Lỗi API! Sản phẩm không được xóa!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton("Hủy", null)
                                    .show();
                        }
                    });

                    cartContainer.addView(itemCart);
                    TotalPrice += itemTotal;
                }
                totalprice.setText(String.valueOf(TotalPrice) + " đ");
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TotalPrice == 0) {
                    Toast.makeText(view.getContext(), "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
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

    public interface OnCartLoaded {
        void onLoaded(List<Cart> cartList);
    }

    public void getCart(OnCartLoaded callback) {
        ApiCaller caller = new ApiCaller(this);
        caller.getItems("/cart_items/get/" + SessionManager.userId, null, new ApiCaller.OnRawResponse() {
            @Override
            public void onSuccess(String response) {
                List<Cart> cartList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int cartId = obj.getInt("id");
                        int qty = obj.getInt("qty");

                        JSONObject productObj = obj.getJSONObject("product");
                        int productId = productObj.getInt("id");
                        String name = productObj.getString("name");
                        String description = productObj.getString("description");
                        String price = String.valueOf(productObj.getInt("price"));
                        int productQty = productObj.getInt("qty");

                        JSONObject imageObj = productObj.getJSONObject("image");
                        String imageName = imageObj.getString("name");
                        String imageBase64 = imageObj.getString("image");

                        Product product = new Product(
                                productId, name, description, price, productQty, imageName, imageBase64
                        );

                        Cart cart = new Cart(cartId, qty, product);
                        cartList.add(cart);
                    }
                    callback.onLoaded(cartList);
                } catch (JSONException e) {
                    Log.e("CART", "JSON error: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CART", "API error: " + error);
            }
        });
    }
}

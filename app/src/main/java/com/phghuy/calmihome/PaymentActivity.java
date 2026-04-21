package com.phghuy.calmihome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.Cart;
import com.phghuy.calmihome.model.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    public interface OnCartLoaded {
        void onLoaded(List<Cart> cartList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        ApiCaller apiCaller = new ApiCaller(this);
        getCart(new OnCartLoaded() {
            @Override
            public void onLoaded(List<Cart> cartList) {
                if (cartList.isEmpty()) {
                    Toast.makeText(PaymentActivity.this, "Không có sản phẩm trong giỏ!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                final int total = cartList.size();
                final int[] deletedCount = {0};

                for (Cart p : cartList) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            apiCaller.DeleteItem("/cart_items/remove/" + p.getId(), new ApiCaller.OnRawResponse() {
                                @Override
                                public void onSuccess(String response) {
                                    deletedCount[0]++;
                                    if (deletedCount[0] == total) {
                                        Toast.makeText(PaymentActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(PaymentActivity.this, SuccessActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Toast.makeText(PaymentActivity.this, "Lỗi khi xóa sản phẩm!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                        }
                    }, 2000);
                }
            }
        });
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

                        Cart cart = new Cart(cartId, 1, null);
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

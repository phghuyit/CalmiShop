package com.phghuy.calmihome.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.flexbox.FlexboxLayout;
import com.phghuy.calmihome.CartActivity;
import com.phghuy.calmihome.DetailProductActivity;
import com.phghuy.calmihome.R;
import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.Category;
import com.phghuy.calmihome.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        LinearLayout CategoryContainer = view.findViewById(R.id.categoryContainer);
        FlexboxLayout ProductContainer = view.findViewById(R.id.productcontainer);
        LinearLayout promotioncontainer = view.findViewById(R.id.promotion_container);

        // Xóa sạch container trước khi load dữ liệu thật
        if (CategoryContainer != null) CategoryContainer.removeAllViews();
        if (ProductContainer != null) ProductContainer.removeAllViews();

        // 1. Load Categories từ API
        getCategory(new OnCategoryLoaded() {
            @Override
            public void onLoaded(List<Category> categoryList) {
                if (CategoryContainer == null) return;
                for (Category c : categoryList) {
                    View itemCategory = inflater.inflate(R.layout.item_category, CategoryContainer, false);
                    TextView name = itemCategory.findViewById(R.id.nameCategory);
                    ImageView img = itemCategory.findViewById(R.id.imgCategory);

                    if (name != null) name.setText(c.getCategory());
                    Bitmap bitmap = convertImage(c.getImageBase64());
                    if (bitmap != null && img != null) img.setImageBitmap(bitmap);
                    
                    CategoryContainer.addView(itemCategory);
                }
            }
        });

        // 2. Load Products từ API
        getProducts(new OnProductLoaded() {
            @Override
            public void onLoaded(List<Product> productList) {
                if (ProductContainer == null) return;
                for (Product p : productList) {
                    View ItemProduct = inflater.inflate(R.layout.item_product, ProductContainer, false);
                    
                    TextView name = ItemProduct.findViewById(R.id.productname);
                    ImageView img = ItemProduct.findViewById(R.id.imgProduct);
                    TextView desc = ItemProduct.findViewById(R.id.description);
                    TextView price = ItemProduct.findViewById(R.id.price);
                    TextView qty = ItemProduct.findViewById(R.id.qty);

                    if (name != null) name.setText(p.getName());
                    Bitmap bitmap = convertImage(p.getImageBase64());
                    if (bitmap != null && img != null) {
                        img.setImageBitmap(bitmap);
                    }
                    if (desc != null) desc.setText(p.getDescription());
                    if (price != null) price.setText(p.getPrice() + " đ");
                    if (qty != null) qty.setText(String.valueOf(p.getQty()));

                    View btn_product = ItemProduct.findViewById(R.id.btn_product);
                    if (btn_product != null) {
                        btn_product.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), DetailProductActivity.class);
                                intent.putExtra("selected product", (Serializable) p);
                                startActivity(intent);
                            }
                        });
                    }

                    ProductContainer.addView(ItemProduct);
                }
            }
        });

        // 3. Hiển thị Khuyến mãi (Tạm thời để mẫu)
        if (promotioncontainer != null) {
            for(int i=0; i<10; i++){
                View itempromotion = inflater.inflate(R.layout.item_promotion, promotioncontainer, false);
                TextView txt = itempromotion.findViewById(R.id.txt);
                if (txt != null) txt.setText("Sản phẩm " + (i+1));
                promotioncontainer.addView(itempromotion);
            }
        }

        // Click listeners
        TextView detaiilProd = view.findViewById(R.id.detailProd);
        ImageView cart = view.findViewById(R.id.cartBtn);
        if(detaiilProd != null) detaiilProd.setOnClickListener(v -> startActivity(new Intent(getActivity(), DetailProductActivity.class)));
        if(cart != null) cart.setOnClickListener(v -> startActivity(new Intent(getActivity(), CartActivity.class)));
        
        return view;
    }

    public Bitmap convertImage(String base64String) {
        if(base64String == null || base64String.isEmpty()) return null;
        try {
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch(Exception e) {
            return null;
        }
    }

    public interface OnCategoryLoaded { void onLoaded(List<Category> list); }
    public interface OnProductLoaded { void onLoaded(List<Product> list); }

    public void getCategory(OnCategoryLoaded callback) {
        new ApiCaller(getContext()).getItems("/ProductCategory/GetAll", null, new ApiCaller.OnRawResponse() {
            @Override
            public void onSuccess(String response) {
                List<Category> list = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        JSONObject imgObj = obj.optJSONObject("image");
                        String base64 = (imgObj != null) ? imgObj.optString("image") : obj.optString("image");
                        String name = (imgObj != null) ? imgObj.optString("name") : "";
                        list.add(new Category(obj.getInt("id"), obj.getString("category"), name, base64));
                    }
                    callback.onLoaded(list);
                } catch (Exception e) { Log.e("API", "Error Category: " + e.getMessage()); }
            }
            @Override public void onError(String error) { Log.e("API", "API Error Category: " + error); }
        });
    }

    public void getProducts(OnProductLoaded callback) {
        new ApiCaller(getContext()).getItems("/products/getall", null, new ApiCaller.OnRawResponse() {
            @Override
            public void onSuccess(String response) {
                List<Product> list = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        JSONObject imgObj = obj.optJSONObject("image");
                        String base64 = (imgObj != null) ? imgObj.optString("image") : obj.optString("image");
                        String imageName = (imgObj != null) ? imgObj.optString("name") : "";
                        list.add(new Product(obj.getInt("id"), obj.getString("name"), obj.getString("description"), obj.getString("price"), obj.getInt("qty"), imageName, base64));
                    }
                    callback.onLoaded(list);
                } catch (Exception e) { Log.e("API", "Error Product: " + e.getMessage()); }
            }
            @Override public void onError(String error) { Log.e("API", "API Error Product: " + error); }
        });
    }
}

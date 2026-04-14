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
import com.phghuy.calmihome.MainActivity;
import com.phghuy.calmihome.R;
import com.phghuy.calmihome.data.remote.ApiCaller;
import com.phghuy.calmihome.model.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        LinearLayout promotioncontainer = view.findViewById(R.id.promotion_container);
        LinearLayout CategoryContainer = view.findViewById(R.id.categoryContainer);

        getCategory(new OnCategoryLoaded() {
            @Override
            public void onLoaded(List<Category> categoryList) {
                for (Category c : categoryList) {
                    // Inflate layout cho từng item category
                    View ItemCategory = inflater.inflate(R.layout.item_category, container, false);

                    // Đặt dữ liệu cho từng view
                    TextView CategoryName = ItemCategory.findViewById(R.id.nameCategory);
                    ImageView categoryImage = ItemCategory.findViewById(R.id.imgCategory);

                    CategoryName.setText(c.getCategory());

                    Bitmap bitmap = convertImage(c.getImageBase64());
                    if(bitmap!=null){
                        categoryImage.setImageBitmap(bitmap);
                    }
                    CategoryContainer.addView(ItemCategory);
                }
            }
        });
        for(int i=0;i<10;i++){
            View itempromotion = inflater.inflate(R.layout.item_promotion,promotioncontainer,false);
            TextView txt = itempromotion.findViewById(R.id.txt);
            txt.setText("Sản phẩm"+i);
            promotioncontainer.addView(itempromotion);
        }

        FlexboxLayout flexcontainer = view.findViewById(R.id.productcontainer);
        for(int i=0;i<5;i++){
            View itemproduct = inflater.inflate(R.layout.item_product,flexcontainer,false);
            flexcontainer.addView(itemproduct);
        }
        TextView detaiilProd = view.findViewById(R.id.detailProd);
        ImageView cart = view.findViewById(R.id.cartBtn);


        detaiilProd.setOnClickListener(v -> startActivity(new Intent(getActivity(), DetailProductActivity.class)));
        cart.setOnClickListener(v->startActivity(new Intent(getActivity(), CartActivity.class)));
        return view;
    }

    public Bitmap convertImage(String base64String) {
         if(base64String==null||base64String.isEmpty()){
             Log.e("ConvertImage","base64Image is null or empty");
             return null;
         }
        try{
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }catch(IllegalArgumentException e){
            Log.e("ConvertImage","Base64 decode error: "+e.getMessage());
        }catch (Exception e){
            Log.e("ConvertImage","Unknown error: "+e.getMessage());
        }
        return null;
    }

    public interface OnCategoryLoaded{
        void onLoaded(List<Category> categoryList);
    }

    public void getCategory(OnCategoryLoaded callback) {
        ApiCaller caller=new ApiCaller(getContext());
        caller.getItems("/category/getall",null,new ApiCaller.OnRawResponse(){
            @Override
            public void onSuccess(String response) {
            List<Category> categoryList=new ArrayList<>();
            try{
                JSONArray jsonArray=new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    int id=obj.getInt("id");
                    String categoryname=obj.getString("category");
                    JSONObject image =obj.getJSONObject("image");
                    String imageName=image.getString("name");
                    String imageBase64 = image.getString("image");

                    Category category = new Category(id,categoryname,imageName,imageBase64);
                    categoryList.add(category);
                }
                callback.onLoaded(categoryList);
            }catch (JSONException e){
                Log.e("PRODUCT","LỖI ĐỌC JSON: "+e.getMessage());
            }
            }

            @Override
            public void onError(String error){

                Log.e("PRODUCT","LỖI API: "+error);
            }
        }
        );
    }
}

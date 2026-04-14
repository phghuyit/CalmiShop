package com.phghuy.calmihome.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ApiCaller {

    private final RequestQueue requestQueue;
    private static final String TAG = "API_HELPER";
    private static final String BASE_URL = "http://10.0.2.2:8080/user";

    public ApiCaller(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public interface OnRawResponse {
        void onSuccess(String response);
        void onError(String error);
    }

    // ==================== GET ====================
    // Added endpoint, params, and callback as parameters
    public void getItems(String endpoint, Map<String, String> params, OnRawResponse callback) {
        String fullUrl = buildUrlWithParams(BASE_URL + endpoint, params);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                fullUrl,
                response -> callback.onSuccess(response),
                error -> callback.onError(error.toString())
        );

        requestQueue.add(request);
    }

    // ==================== CREATE (POST) ====================
    public void createItem(String name, int quantity) {
        String url = BASE_URL + "/create";

        JSONObject data = new JSONObject();
        try {
            data.put("name", name);
            data.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                data,
                response -> Log.d(TAG, "CREATE response: " + response),
                error -> Log.e(TAG, "CREATE error: " + error.toString())
        );

        requestQueue.add(request);
    }

    // ==================== UPDATE (PUT) ====================
    public void updateItem(int id, String name, int quantity) {
        String url = BASE_URL + "/" + id;

        JSONObject data = new JSONObject();
        try {
            data.put("name", name);
            data.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                data,
                response -> Log.d(TAG, "UPDATE response: " + response),
                error -> Log.e(TAG, "UPDATE error: " + error.toString())
        );

        requestQueue.add(request);
    }

    // ==================== DELETE ====================
    public void deleteItem(int id) {
        String url = BASE_URL + "/" + id;

        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> Log.d(TAG, "DELETE response: " + response),
                error -> Log.e(TAG, "DELETE error: " + error.toString())
        );

        requestQueue.add(request);
    }

    private String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) return baseUrl;

        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        // Remove the trailing '&'
        url.setLength(url.length() - 1);
        return url.toString();
    }
}
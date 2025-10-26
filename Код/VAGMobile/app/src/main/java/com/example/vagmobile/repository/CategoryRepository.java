package com.example.vagmobile.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.vagmobile.network.ApiClient;
import com.example.vagmobile.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class CategoryRepository {
    private ApiService apiService;

    public CategoryRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public MutableLiveData<Map<String, Object>> getCategories() {
        MutableLiveData<Map<String, Object>> result = new MutableLiveData<>();

        apiService.getCategories().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Failed to load categories: " + response.code());
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Network error: " + t.getMessage());
                result.setValue(error);
            }
        });

        return result;
    }

    public MutableLiveData<Map<String, Object>> getCategory(Long categoryId) {
        MutableLiveData<Map<String, Object>> result = new MutableLiveData<>();

        apiService.getCategory(categoryId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Failed to load category");
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Network error: " + t.getMessage());
                result.setValue(error);
            }
        });

        return result;
    }

    public MutableLiveData<Map<String, Object>> getCategoryArtworks(Long categoryId, int page, int size) {
        MutableLiveData<Map<String, Object>> result = new MutableLiveData<>();

        apiService.getCategoryArtworks(categoryId, page, size).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Failed to load category artworks");
                    result.setValue(error);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Network error: " + t.getMessage());
                result.setValue(error);
            }
        });

        return result;
    }
}
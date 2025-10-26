package com.example.vagmobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vagmobile.network.ApiClient;
import com.example.vagmobile.network.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {
    private MutableLiveData<Map<String, Object>> categoriesResult = new MutableLiveData<>();
    private MutableLiveData<Map<String, Object>> categoryResult = new MutableLiveData<>();
    private MutableLiveData<Map<String, Object>> categoryArtworksResult = new MutableLiveData<>();

    public CategoryViewModel() {
        // repository больше не нужен, работаем напрямую с API
    }

    public void getCategories() {
        ApiClient.getClient().create(ApiService.class).getCategories()
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoriesResult.setValue(response.body());
                        } else {
                            Map<String, Object> error = new HashMap<>();
                            error.put("success", false);
                            error.put("message", "Failed to load categories: " + response.message());
                            categoriesResult.setValue(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Network error: " + t.getMessage());
                        categoriesResult.setValue(error);
                    }
                });
    }

    public void getCategory(Long categoryId) {
        ApiClient.getClient().create(ApiService.class).getCategory(categoryId)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryResult.setValue(response.body());
                        } else {
                            Map<String, Object> error = new HashMap<>();
                            error.put("success", false);
                            error.put("message", "Failed to load category");
                            categoryResult.setValue(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Network error: " + t.getMessage());
                        categoryResult.setValue(error);
                    }
                });
    }

    public void getCategoryArtworks(Long categoryId, int page, int size) {
        ApiClient.getClient().create(ApiService.class).getCategoryArtworks(categoryId, page, size)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call,
                                           Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryArtworksResult.setValue(response.body());
                        } else {
                            Map<String, Object> error = new HashMap<>();
                            error.put("success", false);
                            error.put("message", "Failed to load category artworks");
                            categoryArtworksResult.setValue(error);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Map<String, Object> error = new HashMap<>();
                        error.put("success", false);
                        error.put("message", "Network error: " + t.getMessage());
                        categoryArtworksResult.setValue(error);
                    }
                });
    }

    public LiveData<Map<String, Object>> getCategoriesResult() {
        return categoriesResult;
    }

    public LiveData<Map<String, Object>> getCategoryResult() {
        return categoryResult;
    }

    public LiveData<Map<String, Object>> getCategoryArtworksResult() {
        return categoryArtworksResult;
    }
}
package com.example.vagmobile.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.vagmobile.model.AuthResponse;
import com.example.vagmobile.model.User;
import com.example.vagmobile.network.ApiClient;
import com.example.vagmobile.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public MutableLiveData<Map<String, Object>> login(String username, String password) {
        MutableLiveData<Map<String, Object>> result = new MutableLiveData<>();

        apiService.login(username, password).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Boolean success = (Boolean) responseBody.get("success");

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", success != null ? success : false);
                    responseMap.put("message", responseBody.get("message"));

                    if (success != null && success) {
                        // Создаем AuthResponse из данных ответа
                        AuthResponse authResponse = new AuthResponse();
                        authResponse.setSuccess(true);
                        authResponse.setMessage((String) responseBody.get("message"));

                        // Извлекаем данные пользователя
                        if (responseBody.get("id") != null) {
                            authResponse.setId(((Number) responseBody.get("id")).longValue());
                        }
                        if (responseBody.get("username") != null) {
                            authResponse.setUsername((String) responseBody.get("username"));
                        }
                        if (responseBody.get("email") != null) {
                            authResponse.setEmail((String) responseBody.get("email"));
                        }
                        if (responseBody.get("role") != null) {
                            authResponse.setRole((String) responseBody.get("role"));
                        }

                        responseMap.put("user", authResponse);
                    }

                    result.setValue(responseMap);
                } else {
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Login failed: " + response.message());
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

    public MutableLiveData<Map<String, Object>> register(User user) {
        MutableLiveData<Map<String, Object>> result = new MutableLiveData<>();

        apiService.register(user.getUsername(), user.getEmail(), user.getPassword())
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Map<String, Object> responseBody = response.body();
                            Boolean success = (Boolean) responseBody.get("success");

                            Map<String, Object> responseMap = new HashMap<>();
                            responseMap.put("success", success != null ? success : false);
                            responseMap.put("message", responseBody.get("message"));

                            if (success != null && success) {
                                // Создаем AuthResponse из данных ответа
                                AuthResponse authResponse = new AuthResponse();
                                authResponse.setSuccess(true);
                                authResponse.setMessage((String) responseBody.get("message"));

                                // Извлекаем данные пользователя
                                if (responseBody.get("id") != null) {
                                    authResponse.setId(((Number) responseBody.get("id")).longValue());
                                }
                                if (responseBody.get("username") != null) {
                                    authResponse.setUsername((String) responseBody.get("username"));
                                }
                                if (responseBody.get("email") != null) {
                                    authResponse.setEmail((String) responseBody.get("email"));
                                }
                                if (responseBody.get("role") != null) {
                                    authResponse.setRole((String) responseBody.get("role"));
                                }

                                responseMap.put("user", authResponse);
                            }

                            result.setValue(responseMap);
                        } else {
                            Map<String, Object> error = new HashMap<>();
                            error.put("success", false);
                            error.put("message", "Registration failed: " + response.message());
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
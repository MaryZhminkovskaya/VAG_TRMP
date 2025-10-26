package com.example.vagmobile.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.vagmobile.model.User;
import com.example.vagmobile.repository.AuthRepository;
import java.util.Map;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<Map<String, Object>> loginResult = new MutableLiveData<>();
    private MutableLiveData<Map<String, Object>> registerResult = new MutableLiveData<>();

    public AuthViewModel() {
        authRepository = new AuthRepository();
    }

    public void login(String username, String password) {
        authRepository.login(username, password).observeForever(result -> {
            loginResult.setValue(result);
        });
    }

    public void register(User user) {
        authRepository.register(user).observeForever(result -> {
            registerResult.setValue(result);
        });
    }

    public LiveData<Map<String, Object>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Map<String, Object>> getRegisterResult() {
        return registerResult;
    }
}
package com.example.vagmobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.vagmobile.R;
import com.example.vagmobile.ui.activity.LoginActivity;
import com.example.vagmobile.ui.activity.ProfileActivity;
import com.example.vagmobile.ui.activity.AdminCategoriesActivity;
import com.example.vagmobile.util.SharedPreferencesHelper;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnViewProfile, btnLogout, btnAdminCategories;
    private SharedPreferencesHelper prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);
        loadUserData();
        setupClickListeners();
        checkAdminRole();

        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnViewProfile = view.findViewById(R.id.btn_view_profile);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnAdminCategories = view.findViewById(R.id.btn_admin_categories);

        prefs = new SharedPreferencesHelper(getContext());
    }

    private void loadUserData() {
        String username = prefs.getUsername();
        String email = prefs.getEmail();

        tvUsername.setText(username != null ? username : "User");
        tvEmail.setText(email != null ? email : "user@example.com");
    }

    private void checkAdminRole() {
        // Проверяем роль пользователя (предполагаем, что у вас есть метод для получения роли)
        // Если пользователь админ, показываем кнопку управления категориями
        String userRole = prefs.getUserRole(); // Вам нужно добавить этот метод в SharedPreferencesHelper
        boolean isAdmin = "ADMIN".equals(userRole);

        btnAdminCategories.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }

    private void setupClickListeners() {
        btnViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        btnAdminCategories.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AdminCategoriesActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            prefs.clearUserData();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });
    }
}
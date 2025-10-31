package com.example.vagmobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vagmobile.R;
import com.example.vagmobile.model.Category;
import com.example.vagmobile.ui.activity.ArtworkListActivity;
import com.example.vagmobile.ui.adapter.CategoryAdapter;
import com.example.vagmobile.viewmodel.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    private CategoryViewModel categoryViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        initViews(view);
        setupRecyclerView();
        observeViewModels();
        loadCategories();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Открываем публикации категории
            Intent intent = new Intent(getActivity(), ArtworkListActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            startActivity(intent);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdapter);
    }

    private void observeViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getCategoriesResult().observe(getViewLifecycleOwner(), result -> {
            progressBar.setVisibility(View.GONE);

            if (result != null) {
                Boolean success = (Boolean) result.get("success");
                if (success != null && success) {
                    List<Map<String, Object>> categoriesData = (List<Map<String, Object>>) result.get("categories");
                    if (categoriesData != null) {
                        categoryList.clear();
                        for (Map<String, Object> categoryData : categoriesData) {
                            Category category = convertToCategory(categoryData);
                            categoryList.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                } else {
                    String message = (String) result.get("message");
                    Toast.makeText(getContext(), "Failed to load categories: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        categoryViewModel.getCategories();
    }

    private Category convertToCategory(Map<String, Object> categoryData) {
        Category category = new Category();
        category.setId(((Double) categoryData.get("id")).longValue());
        category.setName((String) categoryData.get("name"));
        category.setDescription((String) categoryData.get("description"));

        if (categoryData.get("approvedArtworksCount") != null) {
            if (categoryData.get("approvedArtworksCount") instanceof Double) {
                category.setApprovedArtworksCount(((Double) categoryData.get("approvedArtworksCount")).longValue());
            } else if (categoryData.get("approvedArtworksCount") instanceof Integer) {
                category.setApprovedArtworksCount(((Integer) categoryData.get("approvedArtworksCount")).longValue());
            } else if (categoryData.get("approvedArtworksCount") instanceof Long) {
                category.setApprovedArtworksCount((Long) categoryData.get("approvedArtworksCount"));
            }
        }

        return category;
    }
}
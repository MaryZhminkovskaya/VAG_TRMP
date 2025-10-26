package com.example.vagmobile.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.vagmobile.R;
import com.example.vagmobile.model.Category;
import com.example.vagmobile.ui.adapter.CategoryAdapter;
import com.example.vagmobile.viewmodel.CategoryViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private CategoryViewModel categoryViewModel;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initViews();
        setupRecyclerView();
        observeViewModels();
        loadCategories();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Открываем публикации категории
            Intent intent = new Intent(CategoryActivity.this, ArtworkListActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            startActivity(intent);
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdapter);
    }

    private void observeViewModels() {
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getCategoriesResult().observe(this, result -> {
            progressBar.setVisibility(android.view.View.GONE);

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
                    Toast.makeText(this, "Failed to load categories: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadCategories() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        categoryViewModel.getCategories();
    }

    private Category convertToCategory(Map<String, Object> categoryData) {
        Category category = new Category();
        category.setId(((Double) categoryData.get("id")).longValue());
        category.setName((String) categoryData.get("name"));
        category.setDescription((String) categoryData.get("description"));

        if (categoryData.get("approvedArtworksCount") != null) {
            category.setApprovedArtworksCount(((Double) categoryData.get("approvedArtworksCount")).longValue());
        }

        return category;
    }
}
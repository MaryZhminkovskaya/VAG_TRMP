package com.example.vag.controller.mobile;

import com.example.vag.dto.ArtworkDTO;
import com.example.vag.dto.CategoryDTO;
import com.example.vag.mapper.ArtworkMapper;
import com.example.vag.model.Artwork;
import com.example.vag.model.Category;
import com.example.vag.service.ArtworkService;
import com.example.vag.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile/categories")
public class MobileCategoryController {

    private final CategoryService categoryService;
    private final ArtworkService artworkService;
    private final ArtworkMapper artworkMapper;

    public MobileCategoryController(CategoryService categoryService, ArtworkService artworkService, ArtworkMapper artworkMapper) {
        this.categoryService = categoryService;
        this.artworkService = artworkService;
        this.artworkMapper = artworkMapper;
    }

    // Получить все категории
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.findAll();

            // Устанавливаем количество одобренных публикаций для каждой категории
            for (Category category : categories) {
                Long approvedCount = artworkService.countApprovedArtworksByCategoryId(category.getId());
                category.setApprovedArtworksCount(approvedCount);
            }

            List<CategoryDTO> categoryDTOs = artworkMapper.toCategoryDTOList(categories);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categories", categoryDTOs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch categories");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Получить категорию по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        try {
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Long approvedCount = artworkService.countApprovedArtworksByCategoryId(id);
            category.setApprovedArtworksCount(approvedCount);

            CategoryDTO categoryDTO = artworkMapper.toCategoryDTO(category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("category", categoryDTO);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Category not found");
            return ResponseEntity.notFound().build();
        }
    }

    // Получить публикации по категории
    @GetMapping("/{id}/artworks")
    public ResponseEntity<?> getArtworksByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            // Проверяем существование категории
            Category category = categoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            Pageable pageable = PageRequest.of(page, size);
            Page<Artwork> artworks = artworkService.findByCategoryId(id, pageable);

            List<ArtworkDTO> artworkDTOs = artworkMapper.toDTOList(artworks.getContent());
            CategoryDTO categoryDTO = artworkMapper.toCategoryDTO(category);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("category", categoryDTO);
            response.put("artworks", artworkDTOs);
            response.put("totalPages", artworks.getTotalPages());
            response.put("currentPage", artworks.getNumber());
            response.put("totalItems", artworks.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch artworks");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
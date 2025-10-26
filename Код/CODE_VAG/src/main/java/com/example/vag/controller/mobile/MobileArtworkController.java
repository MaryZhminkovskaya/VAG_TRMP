package com.example.vag.controller.mobile;

import com.example.vag.dto.ArtworkDTO;
import com.example.vag.mapper.ArtworkMapper;
import com.example.vag.model.Artwork;
import com.example.vag.model.User;
import com.example.vag.service.ArtworkService;
import com.example.vag.service.UserService;
import com.example.vag.util.FileUploadUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile")
public class MobileArtworkController {

    private final ArtworkService artworkService;
    private final UserService userService;
    private final FileUploadUtil fileUploadUtil;
    private final ArtworkMapper artworkMapper;

    public MobileArtworkController(ArtworkService artworkService, UserService userService,
                                   FileUploadUtil fileUploadUtil, ArtworkMapper artworkMapper) {
        this.artworkService = artworkService;
        this.userService = userService;
        this.fileUploadUtil = fileUploadUtil;
        this.artworkMapper = artworkMapper;
    }

    // Получить все одобренные публикации
    @GetMapping("/artworks")
    public ResponseEntity<?> getApprovedArtworks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Artwork> artworks = artworkService.findPaginatedApprovedArtworks(pageable);

            // Преобразуем в DTO
            List<ArtworkDTO> artworkDTOs = artworkMapper.toSimpleDTOList(artworks.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("artworks", artworkDTOs);
            response.put("totalPages", artworks.getTotalPages());
            response.put("currentPage", artworks.getNumber());
            response.put("totalItems", artworks.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch artworks: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Получить публикацию по ID
    @GetMapping("/artworks/{id}")
    public ResponseEntity<?> getArtwork(@PathVariable Long id) {
        try {
            Artwork artwork = artworkService.findByIdWithComments(id);

            // Проверка прав доступа
            User currentUser = null;
            try {
                currentUser = userService.getCurrentUser();
            } catch (Exception e) {
                // Пользователь не аутентифицирован
            }

            boolean isApproved = "APPROVED".equals(artwork.getStatus());
            boolean isAuthor = currentUser != null && currentUser.getId().equals(artwork.getUser().getId());
            boolean isAdmin = currentUser != null && currentUser.hasRole("ADMIN");

            if (!isApproved && !isAuthor && !isAdmin) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Access denied");
                return ResponseEntity.status(403).body(response);
            }

            // Проверяем лайк для аутентифицированного пользователя
            if (currentUser != null) {
                boolean isLiked = artworkService.isLikedByUser(artwork, currentUser);
                artwork.setLiked(isLiked);
            }

            // Преобразуем в DTO
            ArtworkDTO artworkDTO = artworkMapper.toDTO(artwork);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("artwork", artworkDTO);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Artwork not found");
            return ResponseEntity.notFound().build();
        }
    }

    // Создать публикацию
    @PostMapping("/artworks/create")
    public ResponseEntity<?> createArtwork(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<Long> categoryIds,
            @RequestParam MultipartFile imageFile) {

        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            Artwork artwork = new Artwork();
            artwork.setTitle(title);
            artwork.setDescription(description);
            artwork.setStatus("PENDING");
            artwork.setCategoryIds(categoryIds);

            Artwork savedArtwork = artworkService.create(artwork, imageFile, currentUser);
            ArtworkDTO artworkDTO = artworkMapper.toSimpleDTO(savedArtwork);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("artwork", artworkDTO);
            response.put("message", "Artwork created successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create artwork: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Поставить лайк
    @PostMapping("/artworks/{id}/like")
    public ResponseEntity<?> likeArtwork(@PathVariable Long id) {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            artworkService.likeArtwork(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Liked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to like artwork");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Убрать лайк
    @PostMapping("/artworks/{id}/unlike")
    public ResponseEntity<?> unlikeArtwork(@PathVariable Long id) {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            artworkService.unlikeArtwork(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Unliked successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to unlike artwork");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Добавить комментарий
    @PostMapping("/artworks/{id}/comment")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestParam String content) {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            artworkService.addComment(id, user, content);
            Artwork artwork = artworkService.findByIdWithComments(id);
            ArtworkDTO artworkDTO = artworkMapper.toDTO(artwork);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("artwork", artworkDTO);
            response.put("message", "Comment added successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to add comment");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Поиск публикаций
    @GetMapping("/artworks/search")
    public ResponseEntity<?> searchArtworks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Artwork> artworks = artworkService.searchApprovedArtworks(query, pageable);

            List<ArtworkDTO> artworkDTOs = artworkMapper.toSimpleDTOList(artworks.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("artworks", artworkDTOs);
            response.put("totalPages", artworks.getTotalPages());
            response.put("currentPage", artworks.getNumber());
            response.put("totalItems", artworks.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Search failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
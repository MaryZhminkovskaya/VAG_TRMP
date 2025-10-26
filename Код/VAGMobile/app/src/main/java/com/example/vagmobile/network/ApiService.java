package com.example.vagmobile.network;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Эндпоинты для artworks
    @GET("api/mobile/artworks")
    Call<Map<String, Object>> getArtworks(@Query("page") int page, @Query("size") int size);

    @GET("api/mobile/artworks/{id}")
    Call<Map<String, Object>> getArtwork(@Path("id") Long id);

    @Multipart
    @POST("api/mobile/artworks/create")
    Call<Map<String, Object>> createArtwork(
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("categoryIds") RequestBody categoryIds,
            @Part MultipartBody.Part imageFile
    );

    @POST("api/mobile/artworks/{id}/like")
    Call<Map<String, Object>> likeArtwork(@Path("id") Long id);

    @POST("api/mobile/artworks/{id}/unlike")
    Call<Map<String, Object>> unlikeArtwork(@Path("id") Long id);

    @Multipart
    @POST("api/mobile/artworks/{id}/comment")
    Call<Map<String, Object>> addComment(@Path("id") Long id, @Part("content") RequestBody content);

    // Поиск публикаций
    @GET("api/mobile/artworks/search")
    Call<Map<String, Object>> searchArtworks(
            @Query("query") String query,
            @Query("page") int page,
            @Query("size") int size
    );

    // Эндпоинты для категорий
    @GET("api/mobile/categories")
    Call<Map<String, Object>> getCategories();

    @GET("api/mobile/categories/{id}")
    Call<Map<String, Object>> getCategory(@Path("id") Long id);

    @GET("api/mobile/categories/{id}/artworks")
    Call<Map<String, Object>> getCategoryArtworks(
            @Path("id") Long id,
            @Query("page") int page,
            @Query("size") int size
    );

    // Эндпоинты для пользователей
    @GET("api/mobile/users/profile")
    Call<Map<String, Object>> getCurrentUserProfile();

    @GET("api/mobile/users/{userId}")
    Call<Map<String, Object>> getUserProfile(@Path("userId") Long userId);

    @GET("api/mobile/users/{userId}/artworks")
    Call<Map<String, Object>> getUserArtworks(
            @Path("userId") Long userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/mobile/users/liked/artworks")
    Call<Map<String, Object>> getLikedArtworks(@Query("page") int page, @Query("size") int size);

    // Эндпоинты для аутентификации
    @POST("api/mobile/auth/login")
    Call<Map<String, Object>> login(@Query("username") String username, @Query("password") String password);

    @POST("api/mobile/auth/register")
    Call<Map<String, Object>> register(@Query("username") String username, @Query("email") String email, @Query("password") String password);
}
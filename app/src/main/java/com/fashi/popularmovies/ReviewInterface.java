package com.fashi.popularmovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewInterface {
    //GET movie/{movie_id}/reviews
    @GET("/3/movie/{movie_id}/reviews")
    Call<ReviewClass> getMovies(
            @Path("movie_id") int movie_id,
            @Query("api_key") String api_key,
            @Query("language") String language,
            @Query("page") int page
    );
}
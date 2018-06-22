package com.example.android.popularmovies.utilities;

public class MovieUtils {
    public static final String MOVIE_DB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static String getMovieThumbnailSrc(String path) {
        return MOVIE_DB_POSTER_BASE_URL + path;
    }
}

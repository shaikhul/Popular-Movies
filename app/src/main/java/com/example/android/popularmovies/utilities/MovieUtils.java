package com.example.android.popularmovies.utilities;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.models.Movie;

import java.util.List;

public class MovieUtils {
    private static final String MOVIE_DB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/";

    public static String getMovieThumbnailSrc(String path) {
        return MOVIE_DB_POSTER_BASE_URL + path;
    }

    public static LiveData<List<Movie>> getMyFavoriteMovies(Context context) {
        return MovieDatabase.getInstance(context).movieDao().getAll();
    }
}

package com.example.android.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.models.Movie;

public class MovieDetailViewModel extends ViewModel {
    private LiveData<Movie> movie;

    public MovieDetailViewModel(MovieDatabase db, int movieId) {
        movie = db.movieDao().findById(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}

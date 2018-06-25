package com.example.android.popularmovies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.MovieDatabase;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private MovieDatabase db;
    private int movieId;

    public MovieDetailViewModelFactory(MovieDatabase db, int movieId) {
        this.db = db;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailViewModel(db, movieId);
    }
}

package com.example.android.popularmovies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.models.Movie;

import java.util.List;

class MainViewModel extends AndroidViewModel {

    final private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        movies = MovieDatabase.getInstance(application).movieDao().getAll();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}

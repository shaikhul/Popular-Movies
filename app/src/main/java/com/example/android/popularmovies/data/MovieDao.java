package com.example.android.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.models.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY internal_id ASC")
    LiveData<List<Movie>> getAll();

    @Query("SELECT * FROM movies WHERE id = :id")
    LiveData<Movie> findById(Integer id);

    @Query("DELETE FROM movies where id = :id")
    void deleteById(Integer id);

    @Insert()
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM movies")
    void deleteAll();
}

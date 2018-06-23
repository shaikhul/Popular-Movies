package com.example.android.popularmovies.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.popularmovies.models.Movie;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    private static final String MOVIE_DB = "movie_db";

    private static MovieDatabase instance;

    public static MovieDatabase getInstance(Context context) {
        if (instance != null) {
            return  instance;
        }

        instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MovieDatabase.class,
                MOVIE_DB
        ).build();

        return instance;
    }

    public abstract MovieDao movieDao();
}

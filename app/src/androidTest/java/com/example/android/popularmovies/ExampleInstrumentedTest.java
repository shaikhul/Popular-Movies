package com.example.android.popularmovies;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.models.Movie;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context appContext;
    MovieDatabase db;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getTargetContext();
        db = MovieDatabase.getInstance(appContext);
        db.movieDao().deleteAll();
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.android.popularmovies", appContext.getPackageName());
        assertEquals(0, db.movieDao().getAll().size());
    }

    @Test
    public void movieCRUDTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        MovieDatabase db = MovieDatabase.getInstance(appContext);

        Movie movie = new Movie();
        movie.setId(123456);
        movie.setTitle("MovieTitle");
        movie.setOverview("Long overview");
        movie.setPosterPath("xkcd");

        db.movieDao().insert(movie);

        List<Movie> movies = db.movieDao().getAll();
        assertEquals(1, movies.size());
    }
}

package com.example.android.popularmovies;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Movie>> {
    private RecyclerView mRecyclerView;
    private MovieAdapter moviesAdapter;
    private RecyclerView.LayoutManager gridLayoutManager;

    private static final int MOVIE_LOADER_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        // use a linear layout manager
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        moviesAdapter = new MovieAdapter();
        //moviesAdapter.setDataset(loadMovies());
        mRecyclerView.setAdapter(moviesAdapter);

        int loaderId = MOVIE_LOADER_ID;
        Bundle bundleForLoader = null;
        LoaderCallbacks<ArrayList<Movie>> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    private ArrayList<Movie> loadMovies() {
        ArrayList<Movie> movies = new ArrayList<Movie>();

        movies.add(new Movie(1, "/7WsyChQLEftFiDOVTGkv3hFpyyt.jpg"));
        movies.add(new Movie(2, "/r70GGoZ5PqqokDDRnVfTN7PPDtJ.jpg"));
        movies.add(new Movie(3, "/jjPJ4s3DWZZvI4vw8Xfi4Vqa1Q8.jpg"));
        movies.add(new Movie(4, "/uxzzxijgPIY7slzFvMotPv8wjKA.jpg"));
        movies.add(new Movie(5, "/to0spRl1CMDvyUbOnbb4fTk3VAd.jpg"));
        movies.add(new Movie(6, "/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg"));

        return movies;
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> movies = new ArrayList<Movie>();

            @Override
            public ArrayList<Movie> loadInBackground() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                movies = loadMovies();
                return movies;
            }

            @Override
            protected void onStartLoading() {
                if (movies != null) {
                    deliverResult(movies);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                movies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        moviesAdapter.setDataset(data);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }
}

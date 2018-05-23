package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter moviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private int sortBy;

    private static final int MOVIE_LOADER_ID = 123;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        RecyclerView.LayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        moviesAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(moviesAdapter);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        boolean isConnected = isConnected();

        sortBy = R.id.action_sort_by_popular_movies;
        LoaderCallbacks<ArrayList<Movie>> callback = MainActivity.this;
        if (isConnected) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, MovieDetailActivity.class);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private ArrayList<Movie> loadMovies(String type) {
        if (isConnected() == false) {
            return null;
        }

        String moviesJsonStr = NetworkUtils.getResponseFromHttpUrl(type);
        try {
            return MovieDbJsonUtils.getMoviesFromJson(moviesJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetData() {
        moviesAdapter.setMovies(null);
    }

    private void showMoviesDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> movies = new ArrayList<Movie>();

            @Override
            public ArrayList<Movie> loadInBackground() {
                String type = POPULAR;

                if (sortBy == R.id.action_sort_by_popular_movies) {
                    type = POPULAR;
                } else if (sortBy == R.id.action_sort_by_top_rated) {
                    type = TOP_RATED;
                }

                movies = loadMovies(type);
                return movies;
            }

            @Override
            protected void onStartLoading() {
                if (movies.size() > 0) {
                    deliverResult(movies);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
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
    public void onLoadFinished(@NonNull Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesAdapter.setMovies(data);

        if (data == null || data.size() <= 0) {
            showErrorMessage();
        } else {
            showMoviesDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Movie>> loader) {
        // noop
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id != sortBy) {
            sortBy = item.getItemId();
            resetData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

        return super.onOptionsItemSelected(item);
    }
}

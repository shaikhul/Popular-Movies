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

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.utilities.MovieDbApiClient;
import com.example.android.popularmovies.utilities.MovieUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter moviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private int sortBy;
    private static final String SORT_BY_KEY = "sortBy";

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

        if (savedInstanceState != null) {
            sortBy = savedInstanceState.getInt(SORT_BY_KEY);
        } else {
            sortBy = R.id.action_sort_by_popular_movies;
        }

        LoaderCallbacks<List<Movie>> callback = MainActivity.this;
        if (NetworkUtils.hasInternetConnection(this)) {
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_BY_KEY, sortBy);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sortBy = savedInstanceState.getInt(SORT_BY_KEY);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, MovieDetailActivity.class);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    private List<Movie> loadMovies(String movieType) {
        if (NetworkUtils.hasInternetConnection(this) == false) {
            return null;
        }

        try {
            return new MovieDbApiClient().getMovies(movieType);
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
        mErrorMessageDisplay.setText(R.string.error_message);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movies = new ArrayList<Movie>();

            @Override
            public List<Movie> loadInBackground() {
                switch (sortBy) {
                    case R.id.action_sort_by_popular_movies:
                        movies = loadMovies(POPULAR);
                        break;
                    case R.id.action_sort_by_top_rated:
                        movies = loadMovies(TOP_RATED);
                        break;
                    case R.id.action_my_favorites:
                        movies = getMyFavoriteMovies();
                        break;
                }

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
            public void deliverResult(List<Movie> data) {
                movies = data;
                super.deliverResult(data);
            }
        };
    }

    private List<Movie> getMyFavoriteMovies() {
        return MovieUtils.getMyFavoriteMovies(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesAdapter.setMovies(data);

        if (data == null) {
            showErrorMessage();
        } else if (data.size() == 0) {
            showEmptyState();
        } else {
            showMoviesDataView();
        }
    }

    private void showEmptyState() {
        mErrorMessageDisplay.setText(R.string.empty_state_message);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
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

        switch (id) {
            case R.id.action_sort_by_popular_movies:
                setTitle(R.string.app_name);
                break;
            case R.id.action_sort_by_top_rated:
                setTitle(R.string.title_top_rated);
                break;
            case R.id.action_my_favorites:
                setTitle(R.string.title_my_favorites);
                break;
        }

        if (id != sortBy) {
            sortBy = item.getItemId();
            resetData();
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.utilities.MovieDbApiClient;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Movie>>, MovieAdapter.MovieAdapterOnClickHandler {
    private RecyclerView mRecyclerView;
    private MovieAdapter moviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView.LayoutManager gridLayoutManager;

    private int sortBy;
    private static final String SORT_BY_KEY = "sortBy";
    private static final String LIST_STATE = "list_state";
    private static Parcelable mListState;

    private MainViewModel mViewModel;

    private static final int MOVIE_LOADER_ID = 123;
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("main page state", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

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

        setPageTitle();
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (sortBy == R.id.action_my_favorites) {
            setupViewModel();
        } else {
            LoaderCallbacks<List<Movie>> callback = MainActivity.this;
            if (NetworkUtils.hasInternetConnection(this)) {
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, callback);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SORT_BY_KEY, sortBy);

        mListState = gridLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE, mListState);

        super.onSaveInstanceState(outState);

        Log.v("main page state", "onSaveInstance");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        sortBy = savedInstanceState.getInt(SORT_BY_KEY);
        mListState = savedInstanceState.getParcelable(LIST_STATE);

        Log.v("main page state", "onRestoreInstance");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("main page state", "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("main page state", "onStart");
        if (sortBy == R.id.action_my_favorites) {
            setupViewModel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("main page state", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("main page state", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("main page state", "onResume");

        if (mListState != null) {
            gridLayoutManager.onRestoreInstanceState(mListState);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, MovieDetailActivity.class);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);
    }

    private List<Movie> loadMovies(String movieType) {
        if (!NetworkUtils.hasInternetConnection(this)) {
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
        sortBy = item.getItemId();
        switch (sortBy) {
            case R.id.action_sort_by_popular_movies:
                break;
            case R.id.action_sort_by_top_rated:
                break;
            case R.id.action_my_favorites:
                break;
        }

        setPageTitle();
        resetData();
        if (sortBy == R.id.action_my_favorites) {
            setupViewModel();
        } else {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewModel() {
        mViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (sortBy == R.id.action_my_favorites) {
                    moviesAdapter.setMovies(movies);
                    if (movies == null) {
                        showErrorMessage();
                    } else if (movies.size() == 0) {
                        showEmptyState();
                    }
                }
            }
        });
    }

    private void setPageTitle() {
        switch (sortBy) {
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
    }
}

package com.example.android.popularmovies;

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

import com.example.android.popularmovies.utilities.MovieDbJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Movie>> {
    private RecyclerView mRecyclerView;
    private MovieAdapter moviesAdapter;
    private RecyclerView.LayoutManager gridLayoutManager;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private int sortBy;

    private static final int MOVIE_LOADER_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        moviesAdapter = new MovieAdapter();
        mRecyclerView.setAdapter(moviesAdapter);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int loaderId = MOVIE_LOADER_ID;
        sortBy = R.id.action_sort_by_popular_movies;
        Bundle bundleForLoader = null;
        LoaderCallbacks<ArrayList<Movie>> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);
    }

    private ArrayList<Movie> loadMovies(String type) {
        String moviesJsonStr = NetworkUtils.getResponseFromHttpUrl(type);

        try {
            return MovieDbJsonUtils.getMoviesFromJson(this, moviesJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetData() {
        moviesAdapter.setDataset(null);
    }

    private void showMoviesDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {

            ArrayList<Movie> movies = new ArrayList<Movie>();

            @Override
            public ArrayList<Movie> loadInBackground() {
                String type = "popular";

                if (sortBy == R.id.action_sort_by_popular_movies) {
                    type = "popular";
                } else if (sortBy == R.id.action_sort_by_top_rated) {
                    type = "top_rated";
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
                Log.d("deliverResult", Integer.toString(movies.size()));
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesAdapter.setDataset(data);

        if (data == null || data.size() <= 0) {
            showErrorMessage();
        } else {
            showMoviesDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

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

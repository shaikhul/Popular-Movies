package com.example.android.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieDatabase;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.MovieReview;
import com.example.android.popularmovies.models.MovieTrailer;
import com.example.android.popularmovies.utilities.MovieDbApiClient;
import com.example.android.popularmovies.utilities.MovieUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieTrailer>>, MovieTrailerAdapter.MovieTrailerOnClickHandler {
    private RecyclerView mTrailersRecyclerView, mReviewsRecyclerView;
    private MovieTrailerAdapter movieTrailerAdapter;
    private RecyclerView.LayoutManager mTrailersLayoutManager, mReviewsLayoutManager;
    private MovieReviewAdapter movieReviewAdapter;

    private Button favoritesButton;

    private Movie movie;
    private Boolean movieState;
    private static Parcelable trailerRVState, reviewRVState;

    private static final String MOVIE_KEY = "movie";
    private static final String MOVIE_STATE_KEY = "movie_state";
    private static final String TRAILER_RV_STATE_KEY = "trailer_rv_state";
    private static final String REVIEW_RV_STATE_KEY = "review_rv_state";

    private static final int TRAILER_LOADER_ID = 150;
    private static final int REVIEWS_LOADER_ID = 200;

    private class addToMyFavoritesAsyncTask extends AsyncTask<Movie, Void, String> {

        final private MovieDatabase db;

        addToMyFavoritesAsyncTask(MovieDatabase db) {
            this.db = db;
        }

        @Override
        protected String doInBackground(Movie... movies) {
            Movie movie = movies[0];
            String msg;

            if (movieState) {
                db.movieDao().deleteById(movie.getId());
                msg = getResources().getString(R.string.msg_movie_deleted);
            } else {
                int internalId = movies[0].getInternalId();
                movies[0].setInternalId(null);
                db.movieDao().insert(movies[0]);
                movies[0].setInternalId(internalId);
                msg = getResources().getString(R.string.msg_movie_added);
            }
            movieState = !movieState;
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            super.onPostExecute(msg);
            showToastMessage(msg);
        }
    }

    private void showToastMessage(String msg) {
        Toast toast = Toast.makeText(MovieDetailActivity.this, msg, Toast.LENGTH_SHORT);
        if (toast != null) {
            toast.show();
        }
        toggleFavIcon();
    }

    private void toggleFavIcon() {
        if (movieState) {
            favoritesButton.setBackgroundResource(android.R.drawable.star_big_on);
        } else {
            favoritesButton.setBackgroundResource(android.R.drawable.star_big_off);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("detail page state", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        setTitle(R.string.title_movie_detail);

        TextView mTitleTextView, mOverviewTextView, mReleaseDate;
        ImageView mImageView;
        RatingBar mRatingBar;

        Bundle bundle;
        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(MOVIE_KEY);
            movieState = savedInstanceState.getBoolean(MOVIE_STATE_KEY);
        } else {
            bundle = getIntent().getExtras();
            movie = (Movie) bundle.getParcelable("movie");
            movieState = false;
        }

        favoritesButton = (Button) findViewById(R.id.btn_favorites);
        toggleFavIcon();

        if (movie != null && movie.getInternalId() != -1) {
            // movie loaded from db
            favoritesButton.setVisibility(View.INVISIBLE);
        } else {
            // movie from api
            favoritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new addToMyFavoritesAsyncTask(MovieDatabase.getInstance(MovieDetailActivity.this)).execute(movie);
                }
            });
        }

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mTitleTextView.setText(movie.getTitle());

        mImageView = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        String url = MovieUtils.getMovieThumbnailSrc(movie.getPosterPath());
        Picasso.with(mImageView.getContext()).load(url).into(mImageView);

        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mOverviewTextView.setText(movie.getOverview());

        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mReleaseDate.setText(movie.getReleaseDate());

        mRatingBar = (RatingBar) findViewById(R.id.rb_vote_avg);
        mRatingBar.setRating(movie.getVoteAverage().intValue());

        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_trailers);
        mTrailersLayoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerView.setLayoutManager(mTrailersLayoutManager);

        movieTrailerAdapter = new MovieTrailerAdapter(this);
        mTrailersRecyclerView.setAdapter(movieTrailerAdapter);

        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);

        mReviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);

        movieReviewAdapter = new MovieReviewAdapter();
        mReviewsRecyclerView.setAdapter(movieReviewAdapter);

        LoaderManager.LoaderCallbacks<List<MovieReview>> reviewLoaderListener = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @NonNull
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
                return new AsyncTaskLoader<List<MovieReview>>(MovieDetailActivity.this) {
                    List<MovieReview> movieReviews = new ArrayList<MovieReview>();

                    @Nullable
                    @Override
                    public List<MovieReview> loadInBackground() {
                        movieReviews = loadMovieReviews();
                        return movieReviews;
                    }

                    @Override
                    protected void onStartLoading() {
                        if (movieReviews.size() > 0) {
                            deliverResult(movieReviews);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public void deliverResult(@Nullable List<MovieReview> data) {
                        movieReviews = data;
                        super.deliverResult(data);
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<MovieReview>> loader, List<MovieReview> data) {
                movieReviewAdapter.setMovieReviews(data);
                if (reviewRVState != null) {
                    mReviewsLayoutManager.onRestoreInstanceState(reviewRVState);
                    Log.v("review_rv_state", "restored from loadFinished");
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {
                // do nothing
            }
        };

        if (NetworkUtils.hasInternetConnection(this)) {
            getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
            getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, reviewLoaderListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_KEY, movie);
        outState.putBoolean(MOVIE_STATE_KEY, movieState);

        trailerRVState = mTrailersLayoutManager.onSaveInstanceState();
        outState.putParcelable(TRAILER_RV_STATE_KEY, trailerRVState);

        reviewRVState = mReviewsLayoutManager.onSaveInstanceState();
        outState.putParcelable(REVIEW_RV_STATE_KEY, reviewRVState);

        Log.v("detail page state", "onSaveInstance");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie = savedInstanceState.getParcelable(MOVIE_KEY);
        movieState = savedInstanceState.getBoolean(MOVIE_STATE_KEY);
        trailerRVState = savedInstanceState.getParcelable(TRAILER_RV_STATE_KEY);
        reviewRVState = savedInstanceState.getParcelable(REVIEW_RV_STATE_KEY);

        Log.v("detail page state", "onRestoreInstance");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("detail page state", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("detail page state", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (trailerRVState != null) {
            mTrailersLayoutManager.onRestoreInstanceState(trailerRVState);
        }

        if (reviewRVState != null) {
            mReviewsLayoutManager.onRestoreInstanceState(reviewRVState);
        }

        Log.v("detail page state", "onResume");
    }

    private List<MovieReview> loadMovieReviews() {
        if (!NetworkUtils.hasInternetConnection(this)) {
            return null;
        }

        try {
            return new MovieDbApiClient().getMovieReviews(movie.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public Loader<List<MovieTrailer>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<MovieTrailer>>(this) {
            List<MovieTrailer> movieTrailers = new ArrayList<MovieTrailer>();

            @Nullable
            @Override
            public List<MovieTrailer> loadInBackground() {
                movieTrailers = loadMovieTrailers();
                return movieTrailers;
            }

            @Override
            protected void onStartLoading() {
                if (movieTrailers.size() > 0) {
                    deliverResult(movieTrailers);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(@Nullable List<MovieTrailer> data) {
                movieTrailers = data;
                super.deliverResult(data);
            }
        };
    }

    private List<MovieTrailer> loadMovieTrailers() {
        if (!NetworkUtils.hasInternetConnection(this)) {
            return null;
        }

        try {
            return new MovieDbApiClient().getMovieTrailers(movie.getId());
        } catch (JSONException e) {
            Log.v("Got exception", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
        movieTrailerAdapter.setMovieTrailers(data);
        if (trailerRVState != null) {
            Log.v("trailer_rv_state", "restored from loadFinished");
            mTrailersLayoutManager.onRestoreInstanceState(trailerRVState);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        // do nothing
    }

    @Override
    public void onClick(MovieTrailer movieTrailer) {
        Uri location = Uri.parse("https://www.youtube.com/watch?v=" + movieTrailer.getKey());
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, location);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(videoIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(videoIntent);
        }
    }
}

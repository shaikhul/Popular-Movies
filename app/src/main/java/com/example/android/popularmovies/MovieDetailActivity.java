package com.example.android.popularmovies;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.MovieReview;
import com.example.android.popularmovies.models.MovieTrailer;
import com.example.android.popularmovies.utilities.MovieDbApiClient;
import com.example.android.popularmovies.utilities.MovieUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieTrailer>>, MovieTrailerAdapter.MovieTrailerOnClickHandler {

    private RecyclerView mRecyclerView;
    private MovieTrailerAdapter movieTrailerAdapter;

    private RecyclerView mReviewRecyclerView;
    private MovieReviewAdapter movieReviewAdapter;

    private Movie movie;
    private static final String MOVIE_KEY = "movie";

    private LoaderManager.LoaderCallbacks<List<MovieReview>> reviewLoaderListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView mTitleTextView, mOverviewTextView, mRelaseDate;
        ImageView mImageView;
        RatingBar mRatingBar;

        if (savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(MOVIE_KEY);
        } else {
            Bundle bundle = getIntent().getExtras();
            movie = (Movie) bundle.getParcelable("movie");
        }

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mTitleTextView.setText(movie.getTitle());

        mImageView = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        String url = MovieUtils.getMovieThumbnailSrc(movie.getPosterPath());
        Picasso.with(mImageView.getContext()).load(url).into(mImageView);

        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mOverviewTextView.setText(movie.getOverview());

        mRelaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRelaseDate.setText(movie.getReleaseDate());

        mRatingBar = (RatingBar) findViewById(R.id.rb_vote_avg);
        mRatingBar.setRating(movie.getVoteAverage().intValue());

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_trailers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        movieTrailerAdapter = new MovieTrailerAdapter(this);
        mRecyclerView.setAdapter(movieTrailerAdapter);

        LoaderManager.LoaderCallbacks<List<MovieTrailer>> callback = MovieDetailActivity.this;
        getSupportLoaderManager().initLoader(150, null, callback);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);

        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);

        movieReviewAdapter = new MovieReviewAdapter();
        mReviewRecyclerView.setAdapter(movieReviewAdapter);

        reviewLoaderListener = new LoaderManager.LoaderCallbacks<List<MovieReview>>() {
            @NonNull
            @Override
            public Loader<List<MovieReview>> onCreateLoader(int id, @Nullable Bundle args) {
                return new AsyncTaskLoader<List<MovieReview>>(MovieDetailActivity.this) {
                    List<MovieReview> movieReviews = new ArrayList<>();

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
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<MovieReview>> loader) {
                // do nothing
            }
        };

        getSupportLoaderManager().initLoader(175, null, reviewLoaderListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(MOVIE_KEY, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        movie = savedInstanceState.getParcelable(MOVIE_KEY);
    }

    private List<MovieReview> loadMovieReviews() {
        try {
            return new MovieDbApiClient().getMovieReviews(movie.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<MovieTrailer>>(this) {
            List<MovieTrailer> movieTrailers = new ArrayList<>();

            @Nullable
            @Override
            public List<MovieTrailer> loadInBackground() {
                movieTrailers = getMovieTrailers();
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

    private List<MovieTrailer> getMovieTrailers() {
        // @todo add internet connection check
        Log.v("load trailer", "Loading trailers");

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
        } else {
            Log.v("button", "cannot start the video");
        }
    }
}

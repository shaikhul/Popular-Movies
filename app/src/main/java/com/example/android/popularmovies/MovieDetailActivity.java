package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        TextView mTitleTextView, mOverviewTextView, mRelaseDate;
        ImageView mImageView;
        RatingBar mRatingBar;

        Bundle bundle = getIntent().getExtras();

        Movie movie = (Movie) bundle.getParcelable("movie");

        mTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mTitleTextView.setText(movie.getTitle());

        mImageView = (ImageView) findViewById(R.id.iv_movie_thumbnail);
        String url = NetworkUtils.MOVIE_DB_POSTER_BASE_URL + movie.getPosterPath();
        Picasso.with(mImageView.getContext()).load(url).into(mImageView);

        mOverviewTextView = (TextView) findViewById(R.id.tv_overview);
        mOverviewTextView.setText(movie.getOverview());

        mRelaseDate = (TextView) findViewById(R.id.tv_release_date);
        mRelaseDate.setText(movie.getReleaseDate());

        mRatingBar = (RatingBar) findViewById(R.id.rb_vote_avg);
        mRatingBar.setRating(movie.getVoteAverage().intValue());
    }
}

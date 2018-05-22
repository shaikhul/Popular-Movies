package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MovieDetailActivity extends AppCompatActivity {

    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Bundle bundle = getIntent().getExtras();
        Movie movie = (Movie) bundle.getParcelable("movie");

        mTextView = (TextView) findViewById(R.id.tv_movie_title);
        mTextView.setText(movie.getTitle());
    }
}

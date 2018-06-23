package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.models.MovieReview;

import java.util.ArrayList;
import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {
    private List<MovieReview> movieReviews;

    public MovieReviewAdapter() {
        this.movieReviews = new ArrayList<>();
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mReviewTextView;
        final TextView mReviewedByTextView;

        ViewHolder(View itemView) {
            super(itemView);
            this.mReviewTextView = (TextView) itemView.findViewById(R.id.tv_movie_review);
            this.mReviewedByTextView = (TextView) itemView.findViewById(R.id.tv_reviewed_by);
        }

        @Override
        public void onClick(View view) {
            // do nothing
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieReview movieReview = movieReviews.get(position);
        holder.mReviewTextView.setText(movieReview.getContent());
        holder.mReviewedByTextView.setText("- by " + movieReview.getAuthor());
    }

    @Override
    public int getItemCount() {
        if (movieReviews != null) {
            return movieReviews.size();
        }
        return 0;
    }
}

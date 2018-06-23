package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.models.MovieTrailer;

import java.util.ArrayList;
import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {
    private List<MovieTrailer> movieTrailers;

    final private MovieTrailerOnClickHandler mClickHandler;

    public void setMovieTrailers(List<MovieTrailer> movieTrailers) {
        this.movieTrailers = movieTrailers;
        notifyDataSetChanged();
    }

    public interface MovieTrailerOnClickHandler {
        void onClick(MovieTrailer movieTrailer);
    }

    public MovieTrailerAdapter(MovieTrailerOnClickHandler clickHandler) {
        this.mClickHandler = clickHandler;
        this.movieTrailers = new ArrayList<>();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final TextView mTrailerTextView;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.ib_trailer);
            this.mTrailerTextView = (TextView) itemView.findViewById(R.id.tv_movie_trailer);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer movieTrailer = movieTrailers.get(adapterPosition);
            mClickHandler.onClick(movieTrailer);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_trailer, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // MovieTrailer movieTrailer = movieTrailers.get(position);
        holder.mTrailerTextView.setText("Trailer " + Integer.toString(position + 1));
    }

    @Override
    public int getItemCount() {
        if (movieTrailers != null) {
            return movieTrailers.size();
        }

        return 0;
    }
}

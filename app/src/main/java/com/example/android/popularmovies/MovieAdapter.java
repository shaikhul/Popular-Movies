package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;

    private MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        movies = new ArrayList<Movie>();
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mImageView;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.iv_movie_thumbnail);
            v.setOnClickListener(this);
        }
//        ViewHolder(View v) {
//            super(v);
//
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, MovieDetailActivity.class);
//                    intent.putExtra("movie", movies.get(getAdapterPosition()));
//                    context.startActivity(intent);
//                }
//            });
//
//            mImageView = (ImageView) v.findViewById(R.id.iv_movie_thumbnail);
//            view.setOnClickListener(this);
//        }


        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = movies.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_thumbnail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String url = NetworkUtils.MOVIE_DB_POSTER_BASE_URL + movie.getPosterPath();
        Picasso.with(holder.mImageView.getContext()).load(url).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }
}

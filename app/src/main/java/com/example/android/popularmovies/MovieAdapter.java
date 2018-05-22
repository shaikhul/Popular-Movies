package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;

    public MovieAdapter() {
        movies = new ArrayList<Movie>();
    }

    public void setDataset(ArrayList<Movie> mDataset) {
        this.movies = mDataset;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    Log.d("Poster Clicked", "Element " + getAdapterPosition() + " clicked.");


                    intent.putExtra("movie", movies.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            mImageView = (ImageView) v.findViewById(R.id.iv_movie_thumbnail);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_thumbnail, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        String url = "http://image.tmdb.org/t/p/w185/" + movie.getPosterPath();
        Log.d("URL", url);
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

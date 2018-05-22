package com.example.android.popularmovies.utilities;

import android.content.Context;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDbJsonUtils {

    public static ArrayList<Movie> getMoviesFromJson(Context context, String moviesJsonStr) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<Movie>();

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        if (moviesJson.has("results")) {
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                Integer movieId = movieObject.getInt("id");
                String movieTitle = movieObject.getString("title");
                String posterPath = movieObject.getString("poster_path");
                Double voteAverage = movieObject.getDouble("vote_average");
                String releaseDate = movieObject.getString("release_date");
                String overview = movieObject.getString("overview");

                movies.add(new Movie(movieId, movieTitle, posterPath, voteAverage, releaseDate, overview));
            }
        }

        return movies;
    }
}

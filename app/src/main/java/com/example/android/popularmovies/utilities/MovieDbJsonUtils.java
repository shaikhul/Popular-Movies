package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.MovieReview;
import com.example.android.popularmovies.models.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDbJsonUtils {

    public static List<Movie> getMoviesFromJson(String moviesJsonStr) throws JSONException {
        List<Movie> movies = new ArrayList<Movie>();

        if (moviesJsonStr == null) {
            return movies;
        }

        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        if (moviesJson.has("results")) {
            JSONArray moviesArray = moviesJson.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);

                Integer movieId = movieObject.getInt("id");
                String movieTitle = movieObject.getString("original_title");
                String posterPath = movieObject.getString("poster_path");
                Double voteAverage = movieObject.getDouble("vote_average");
                String releaseDate = movieObject.getString("release_date");
                String overview = movieObject.getString("overview");

                movies.add(new Movie(movieId, movieTitle, posterPath, voteAverage, releaseDate, overview));
            }
        }

        return movies;
    }

    public static List<MovieTrailer> getMovieTrailersFromJsonResponse(String movieTrailersJsonResponse) throws JSONException {
        List<MovieTrailer> movieTrailers = new ArrayList<>();

        if (movieTrailersJsonResponse.isEmpty()) {
            return movieTrailers;
        }

        JSONObject trailersJosn = new JSONObject(movieTrailersJsonResponse);

        if (trailersJosn.has("results")) {
            JSONArray trailersArray = trailersJosn.getJSONArray("results");

            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailerObject = trailersArray.getJSONObject(i);

                String trailerKey = trailerObject.getString("key");
                movieTrailers.add(new MovieTrailer(trailerKey));
            }
        }
        return movieTrailers;
    }

    public static List<MovieReview> getMovieReviewsFromJsonResponse(String movieReviewJsonResponse) throws JSONException {
        List<MovieReview> movieReviews = new ArrayList<>();

        if (movieReviewJsonResponse.isEmpty()) {
            return movieReviews;
        }

        JSONObject reviewsJson = new JSONObject((movieReviewJsonResponse));

        if (reviewsJson.has("results")) {
            JSONArray reviewsArray = reviewsJson.getJSONArray("results");

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject reviewObject = reviewsArray.getJSONObject(i);

                String author = reviewObject.getString("author");
                String content = reviewObject.getString("content");

                movieReviews.add(new MovieReview(author, content));
            }
        }

        return movieReviews;
    }
}

package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.models.Movie;
import com.example.android.popularmovies.models.MovieReview;
import com.example.android.popularmovies.models.MovieTrailer;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MovieDbApiClient {
    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/";

    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    final String apiKey;

    public MovieDbApiClient(String apiKey) {
        this.apiKey = apiKey.isEmpty() ? BuildConfig.MOVIE_DB_API_KEY : apiKey;
    }

    public MovieDbApiClient() {
        this.apiKey = BuildConfig.MOVIE_DB_API_KEY;
    }

    public List<Movie> getMovies(String movieType) throws JSONException {
        switch (movieType) {
            case POPULAR:
                return getPopularMovies();
            case TOP_RATED:
                return getTopRatedMovies();
            default:
                throw new UnsupportedOperationException();
        }
    }

    List<Movie> getPopularMovies() throws JSONException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + "popular").buildUpon()
                .appendQueryParameter("api_key", this.apiKey)
                .build();

        URL url = getUrl(builtUri);

        String response = NetworkUtils.getResponseFromHttpUrl(url);

        return MovieDbJsonUtils.getMoviesFromJson(response);
    }

    List<Movie> getTopRatedMovies() throws JSONException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + "top_rated").buildUpon()
                .appendQueryParameter("api_key", this.apiKey)
                .build();

        URL url = getUrl(builtUri);

        String response = NetworkUtils.getResponseFromHttpUrl(url);

        return MovieDbJsonUtils.getMoviesFromJson(response);
    }

    public List<MovieTrailer> getMovieTrailers(Integer movieId) throws JSONException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + movieId + "/videos").buildUpon()
                .appendQueryParameter("api_key", this.apiKey)
                .build();

        URL url = getUrl(builtUri);

        String response = NetworkUtils.getResponseFromHttpUrl(url);

        return MovieDbJsonUtils.getMovieTrailersFromJsonResponse(response);
    }

    @Nullable
    private URL getUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public List<MovieReview> getMovieReviews(Integer movieId) throws JSONException {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + movieId + "/reviews").buildUpon()
                .appendQueryParameter("api_key", this.apiKey)
                .build();

        URL url = getUrl(builtUri);

        String response = NetworkUtils.getResponseFromHttpUrl(url);

        return MovieDbJsonUtils.getMovieReviewsFromJsonResponse(response);
    }
}

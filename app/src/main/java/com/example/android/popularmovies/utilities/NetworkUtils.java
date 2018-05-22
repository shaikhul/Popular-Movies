package com.example.android.popularmovies.utilities;

import android.net.Uri;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY = "68ff5ad5d2c88ea4d1e04f1730b7351b";

    public static URL buildUrl(String type) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + type).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(String type) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(buildUrl(type))
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

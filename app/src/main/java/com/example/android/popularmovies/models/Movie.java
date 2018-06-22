package com.example.android.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable implementation inspired by https://dzone.com/articles/using-android-parcel
 */
public class Movie implements Parcelable {
    private Integer id;
    private String title;
    private String posterPath;
    private Double voteAverage;
    private String releaseDate;
    private String overview;

    public Movie(Integer id, String title, String posterPath, Double voteAverage, String releaseDate, String overview) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    private Movie(Parcel in) {
        setId(in.readInt());
        setTitle(in.readString());
        setPosterPath(in.readString());
        setVoteAverage(in.readDouble());
        setReleaseDate(in.readString());
        setOverview(in.readString());
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    private void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    private void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    private void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    private void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getId());
        parcel.writeString(getTitle());
        parcel.writeString(getPosterPath());
        parcel.writeDouble(getVoteAverage());
        parcel.writeString(getReleaseDate());
        parcel.writeString(getOverview());
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

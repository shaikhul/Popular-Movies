package com.example.android.popularmovies.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable implementation inspired by https://dzone.com/articles/using-android-parcel
 */
@Entity(tableName = "movies")
public class Movie implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "internal_id")
    private Integer internalId;

    @ColumnInfo(name = "id")
    private Integer id;

    private String title;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "vote_average")
    private Double voteAverage;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    private String overview;

    @Ignore
    public Movie() {

    }

    public Movie(Integer internalId, Integer id, String title, String posterPath, Double voteAverage, String releaseDate, String overview) {
        this.internalId = internalId;
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
    }

    @Ignore
    public Movie(Integer id, String title, String posterPath, Double voteAverage, String releaseDate, String overview) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.internalId = -1;
    }

    @Ignore
    private Movie(Parcel in) {
        setInternalId(in.readInt());
        setId(in.readInt());
        setTitle(in.readString());
        setPosterPath(in.readString());
        setVoteAverage(in.readDouble());
        setReleaseDate(in.readString());
        setOverview(in.readString());
    }

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(getInternalId());
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

package com.example.android.popularmovies;

public class Movie {
    private Integer id;
    private Integer title;
    private String posterPath;
    private Integer voteAverage;
    private String releasedDate;

    public Movie(Integer id, Integer title, String posterPath, Integer voteAverage, String releasedDate) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.voteAverage = voteAverage;
        this.releasedDate = releasedDate;
    }

    public Movie(Integer id, String posterPath) {
        this.id = id;
        this.posterPath = posterPath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Integer getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Integer voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }
}

package com.example.finalmovieapp.model;

import java.io.Serializable;

public class Movie implements Serializable {

    private String id;
    private String title;
    private String releaseDate;
    private String vote;
    private String popularity;
    private String synopsis;
    private String image;


    public Movie() {
    }

    public Movie(String id, String title, String releaseDate, String vote, String popularity, String synopsis, String image) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.popularity = popularity;
        this.synopsis = synopsis;
        this.image = image;

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.title = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVote() {
        return vote;
    }
    public void setVote(String synopsis) {
        this.vote = vote;
    }

    public String getPopularity() {
        return popularity;
    }
    public void setPopularity(String synopsis) {
        this.popularity = popularity;
    }

    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }



}

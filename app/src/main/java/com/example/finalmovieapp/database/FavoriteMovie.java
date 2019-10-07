package com.example.finalmovieapp.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="FavoriteMovie")
public class FavoriteMovie {

    @PrimaryKey
    private int id;
    private String title;
    private String releaseDate;
    private String vote;
    private String popularity;
    private String synopsis;
    private String image;


    public FavoriteMovie(int id, String title, String releaseDate, String vote, String popularity, String synopsis, String image) {
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.vote = vote;
        this.popularity = popularity;
        this.synopsis = synopsis;
        this.image = image;

    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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


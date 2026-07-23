package com.example.musiclibrarydb.sqlite.model;

public class Artist {
    long id;
    String name;
    long genreId;
    String genreName; //easier view

    //constructors
    public Artist() {
    }

    public Artist(String name, String genreName) {
        this.name = name;
        this.genreName = genreName;
    }

    public Artist(long id, String name, long genreId, String genreName) {
        this.id = id;
        this.name = name;
        this.genreId = genreId;
        this.genreName = genreName;
    }

    //setters and getters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getGenreId() {
        return genreId;
    }

    public void setGenreId(long genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

}

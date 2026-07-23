package com.example.musiclibrarydb.sqlite.model;

public class Song {
    long id;
    String title;
    long artistId;
    String artistName;  // easier view
    long genreId;
    String genreName;   // easier view


    //constructors
    public Song() {
    }

    public Song(String title, String artistName, String genreName) {
        this.title = title;
        this.artistName = artistName;
        this.genreName = genreName;
    }

    public Song(long id, String title, long artistId, String artistName, long genreId, String genreName) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
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

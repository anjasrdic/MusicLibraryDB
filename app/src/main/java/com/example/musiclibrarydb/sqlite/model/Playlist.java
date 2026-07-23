package com.example.musiclibrarydb.sqlite.model;

import java.util.ArrayList;

public class Playlist {
    long id;
    String name;
    long userId;
    ArrayList<Song> songs;  //songs in playlist

    //constructors
    public Playlist() {
        this.songs = new ArrayList<>();
    }

    public Playlist(String name, long userId) {
        this.name = name;
        this.userId = userId;
        this.songs = new ArrayList<>();
    }

    public Playlist(long id, String name, long userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songs = new ArrayList<>();
    }

    public Playlist(long id, String name, long userId, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.songs = songs;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
    }
}

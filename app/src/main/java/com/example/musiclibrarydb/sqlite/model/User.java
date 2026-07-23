package com.example.musiclibrarydb.sqlite.model;

public class User {
    long id;
    String username;
    String password;

    //constructors
    public User(){
    }

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    public User(long id,String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // setters and getters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}

package com.example.musiclibrarydb.sqlite.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.util.Log;

import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Playlist;
import com.example.musiclibrarydb.sqlite.model.Song;
import com.example.musiclibrarydb.sqlite.model.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;

    //Logcat tag
    private static final String LOG = "DatabaseHelper";

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "MusicLibraryDB";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase(); // dobijanje hendla (reference) na bp u koju zelimo da upisemo
    }

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ARTISTS = "artists";
    private static final String TABLE_GENRES = "genres";
    private static final String TABLE_SONGS = "songs";
    private static final String TABLE_PLAYLISTS = "playlists";
    private static final String TABLE_PLAYLIST_SONGS = "playlist_songs";

    // Common column names - ponavljaju se na vise mesta
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    // USERS Table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    // ARTISTS Table - column names
    private static final String KEY_ARTIST_GENRE_ID = "genre_id";

    // SONGS Table - column names
    private static final String KEY_SONG_TITLE = "title";
    private static final String KEY_SONG_ARTIST_ID = "artist_id";
    private static final String KEY_SONG_GENRE_ID = "genre_id";

    // PLAYLISTS Table - column names
    private static final String KEY_PLAYLIST_USER_ID = "user_id";

    // PLAYLIST_SONGS Table - column names
    private static final String KEY_PLAYLIST_ID = "playlist_id";
    private static final String KEY_SONG_ID = "song_id";

    // Table Create Statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_USERNAME + " TEXT UNIQUE," //ne mogu 2 imati isti username
            + KEY_PASSWORD + " TEXT" + ")";

    // Genres table create statement
    private static final String CREATE_TABLE_GENRES = "CREATE TABLE IF NOT EXISTS "
            + TABLE_GENRES + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT UNIQUE" + ")";

    // Artists table create statement
    private static final String CREATE_TABLE_ARTISTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ARTISTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT UNIQUE,"
            + KEY_ARTIST_GENRE_ID + " INTEGER,"
            + "FOREIGN KEY(" + KEY_ARTIST_GENRE_ID + ") REFERENCES "
            + TABLE_GENRES + "(" + KEY_ID + ") ON DELETE CASCADE" + ")";

    // Songs table create statement
    private static final String CREATE_TABLE_SONGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SONGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_SONG_TITLE + " TEXT,"
            + KEY_SONG_ARTIST_ID + " INTEGER,"
            + KEY_SONG_GENRE_ID + " INTEGER,"
            + "FOREIGN KEY(" + KEY_SONG_ARTIST_ID + ") REFERENCES "
            + TABLE_ARTISTS + "(" + KEY_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY(" + KEY_SONG_GENRE_ID + ") REFERENCES "
            + TABLE_GENRES + "(" + KEY_ID + ") ON DELETE CASCADE" + ")";

    // Playlists table create statement
    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PLAYLISTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_PLAYLIST_USER_ID + " INTEGER,"
            + "FOREIGN KEY(" + KEY_PLAYLIST_USER_ID + ") REFERENCES "
            + TABLE_USERS + "(" + KEY_ID + ") ON DELETE CASCADE" + ")";

    // Playlist_songs table create statement
    private static final String CREATE_TABLE_PLAYLIST_SONGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PLAYLIST_SONGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PLAYLIST_ID + " INTEGER,"
            + KEY_SONG_ID + " INTEGER,"
            + "FOREIGN KEY(" + KEY_PLAYLIST_ID + ") REFERENCES "
            + TABLE_PLAYLISTS + "(" + KEY_ID + ") ON DELETE CASCADE,"
            + "FOREIGN KEY(" + KEY_SONG_ID + ") REFERENCES "
            + TABLE_SONGS + "(" + KEY_ID + ") ON DELETE CASCADE" + ")";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_PLAYLIST_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        // create new tables
        onCreate(db);
    }

    //This to be used when needed to create tables from scratch (maybe after tables are deleted)
    public void createTables() {
        if (db == null)
            db = getWritableDatabase();

        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_GENRES);
        db.execSQL(CREATE_TABLE_ARTISTS);
        db.execSQL(CREATE_TABLE_SONGS);
        db.execSQL(CREATE_TABLE_PLAYLISTS);
        db.execSQL(CREATE_TABLE_PLAYLIST_SONGS);
    }

    //Delete all tables
    public void dropTables() {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLIST_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYLISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    }

    /*
     * Creating a user
     */
    public long createUser(User user) {

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername()); //kljuc mora da se slaze sa nazivom kolone
        values.put(KEY_PASSWORD, user.getPassword());

        // insert row
        long user_id = db.insert(TABLE_USERS, null, values);

        //now we know id obtained after writing user to a database, update existing user
        user.setId(user_id);

        return user_id;
    }

    /*
     * Creating a genre
     */
    public long createGenre(Genre genre) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, genre.getName());

        // insert row
        long genre_id = db.insert(TABLE_GENRES, null, values);

        //now we know id obtained after writing genre to a database, update existing genre
        genre.setId(genre_id);

        return genre_id;
    }

    /*
     * Creating an artist
     */
    public long createArtist(Artist artist) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, artist.getName());
        values.put(KEY_ARTIST_GENRE_ID, artist.getGenreId());

        // insert row
        long artist_id = db.insert(TABLE_ARTISTS, null, values);

        //now we know id obtained after writing artist to a database, update existing artist
        artist.setId(artist_id);

        return artist_id;
    }

    /*
     * Creating a song
     */
    public long createSong(Song song) {

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_TITLE, song.getTitle());
        values.put(KEY_SONG_ARTIST_ID, song.getArtistId());
        values.put(KEY_SONG_GENRE_ID, song.getGenreId());

        // insert row
        long song_id = db.insert(TABLE_SONGS, null, values);

        //now we know id obtained after writing song to a database, update existing song
        song.setId(song_id);

        return song_id;
    }

    /*
     * Creating a playlist
     */
    public long createPlaylist(Playlist playlist) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, playlist.getName());
        values.put(KEY_PLAYLIST_USER_ID, playlist.getUserId());

        // insert row
        long playlist_id = db.insert(TABLE_PLAYLISTS, null, values);

        //now we know id obtained after writing playlist to a database, update existing playlist
        playlist.setId(playlist_id);

        return playlist_id;
    }

    /*
     * Adding songs to playlist
     */
    public void addSongsToPlaylist(Playlist playlist) {
        //read all songs for a given playlist and for each song insert a row in table playlist_songs
        for (Song song : playlist.getSongs()) {
            ContentValues values = new ContentValues();
            values.put(KEY_PLAYLIST_ID, playlist.getId());
            values.put(KEY_SONG_ID, song.getId());
            // insert row
            db.insert(TABLE_PLAYLIST_SONGS, null, values);
        }
    }

    /*
     * get single user like
     * SELECT * FROM users WHERE id = 1;
     */
    public User getUser(long user_id) {

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_ID + " = " + user_id;
        //Alternative to use selectionArgs in rawQuery
        //String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
        //        + KEY_ID + " = ?";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        //If using selectionArgs, the number of passed strings must match the number of ? characters
        //within selectQuery string
        //Cursor c = db.rawQuery(selectQuery, new String[]{Long.toString(user_id)});

        if (c != null)
            c.moveToFirst();

        //create user based on data read from a database
        User us = new User();
        us.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        us.setUsername((c.getString(c.getColumnIndexOrThrow(KEY_USERNAME))));
        us.setPassword(c.getString(c.getColumnIndexOrThrow(KEY_PASSWORD)));

        return us;
    }

    /*
     * get user by username only (for checking if username exists)
     * SELECT * FROM users WHERE username = 'john';
     */
    public User getUserByUsername(String username) {

        String selectQuery = "SELECT * FROM " + TABLE_USERS + " WHERE "
                + KEY_USERNAME + " = '" + username + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            User us = new User();
            us.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            us.setUsername((c.getString(c.getColumnIndexOrThrow(KEY_USERNAME))));
            us.setPassword(c.getString(c.getColumnIndexOrThrow(KEY_PASSWORD)));
            c.close();
            return us;
        }

        if (c != null) c.close();
        return null;
    }

    /*
     * get user by username and password (for login)
     * SELECT * FROM users WHERE username = 'john' AND password = '123';
     */
    public User getUserByUsernameAndPassword(String username, String password) {

        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " WHERE "
                + KEY_USERNAME + " = '" + username + "' AND "
                + KEY_PASSWORD + " = '" + password + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null && c.moveToFirst()) {
            User us = new User();
            us.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
            us.setUsername((c.getString(c.getColumnIndexOrThrow(KEY_USERNAME))));
            us.setPassword(c.getString(c.getColumnIndexOrThrow(KEY_PASSWORD)));
            c.close();
            return us;
        }

        if (c != null) c.close();
        return null;
    }

    /*
     * get single genre like
     * SELECT * FROM genres WHERE id = 1;
     */
    public Genre getGenre(long genre_id) {

        String selectQuery = "SELECT  * FROM " + TABLE_GENRES + " WHERE "
                + KEY_ID + " = " + genre_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Genre g = new Genre();
        g.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        g.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));

        return g;
    }

    /*
     * get single artist like
     * SELECT * FROM artists WHERE id = 1;
     */
    public Artist getArtist(long artist_id) {

        String selectQuery = "SELECT  * FROM " + TABLE_ARTISTS + " WHERE "
                + KEY_ID + " = " + artist_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Artist a = new Artist();
        a.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        a.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));
        a.setGenreId(c.getInt(c.getColumnIndexOrThrow(KEY_ARTIST_GENRE_ID)));

        return a;
    }

    /*
     * get single song like
     * SELECT * FROM songs WHERE id = 1;
     */
    public Song getSong(long song_id) {

        String selectQuery = "SELECT  * FROM " + TABLE_SONGS + " WHERE "
                + KEY_ID + " = " + song_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Song s = new Song();
        s.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        s.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_SONG_TITLE)));
        s.setArtistId(c.getInt(c.getColumnIndexOrThrow(KEY_SONG_ARTIST_ID)));
        s.setGenreId(c.getInt(c.getColumnIndexOrThrow(KEY_SONG_GENRE_ID)));

        return s;
    }

    /*
     * get single playlist like
     * SELECT * FROM playlists WHERE id = 1;
     */
    public Playlist getPlaylist(long playlist_id) {

        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLISTS + " WHERE "
                + KEY_ID + " = " + playlist_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Playlist p = new Playlist();
        p.setId(c.getInt(c.getColumnIndexOrThrow(KEY_ID)));
        p.setName(c.getString(c.getColumnIndexOrThrow(KEY_NAME)));
        p.setUserId(c.getInt(c.getColumnIndexOrThrow(KEY_PLAYLIST_USER_ID)));

        return p;
    }

    /*
     * Updating a User using data in an object user
     */
    public int updateUser(User user) {

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, user.getUsername());
        values.put(KEY_PASSWORD, user.getPassword());

        // updating row
        return db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    /*
     * Updating a Genre using data in an object genre
     */
    public int updateGenre(Genre genre) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, genre.getName());

        // updating row
        return db.update(TABLE_GENRES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(genre.getId())});
    }

    /*
     * Updating an Artist using data in an object artist
     */
    public int updateArtist(Artist artist) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, artist.getName());
        values.put(KEY_ARTIST_GENRE_ID, artist.getGenreId());

        // updating row
        return db.update(TABLE_ARTISTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(artist.getId())});
    }

    /*
     * Updating a Song using data in an object song
     */
    public int updateSong(Song song) {

        ContentValues values = new ContentValues();
        values.put(KEY_SONG_TITLE, song.getTitle());
        values.put(KEY_SONG_ARTIST_ID, song.getArtistId());
        values.put(KEY_SONG_GENRE_ID, song.getGenreId());

        // updating row
        return db.update(TABLE_SONGS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(song.getId())});
    }

    /*
     * Updating a Playlist using data in an object playlist
     */
    public int updatePlaylist(Playlist playlist) {

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, playlist.getName());
        values.put(KEY_PLAYLIST_USER_ID, playlist.getUserId());

        // updating row
        return db.update(TABLE_PLAYLISTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(playlist.getId())});
    }

    /*
     * Deleting a User using user id
     */
    public void deleteUser(long user_id) {

        db.delete(TABLE_USERS, KEY_ID + " = ?",
                new String[]{String.valueOf(user_id)});
    }

    /*
     * Deleting a Genre using genre id
     */
    public void deleteGenre(long genre_id) {

        db.delete(TABLE_GENRES, KEY_ID + " = ?",
                new String[]{String.valueOf(genre_id)});
    }

    /*
     * Deleting an Artist using artist id
     */
    public void deleteArtist(long artist_id) {

        db.delete(TABLE_ARTISTS, KEY_ID + " = ?",
                new String[]{String.valueOf(artist_id)});
    }

    /*
     * Deleting a Song using song id
     */
    public void deleteSong(long song_id) {

        db.delete(TABLE_SONGS, KEY_ID + " = ?",
                new String[]{String.valueOf(song_id)});
    }

    /*
     * Deleting a Playlist using playlist id
     */
    public void deletePlaylist(long playlist_id) {

        db.delete(TABLE_PLAYLISTS, KEY_ID + " = ?",
                new String[]{String.valueOf(playlist_id)});
    }

    /*
     * getting all users
     * SELECT * FROM users;
     */
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User u = new User();
                u.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                u.setUsername((c.getString(c.getColumnIndexOrThrow(KEY_USERNAME))));
                u.setPassword(c.getString(c.getColumnIndexOrThrow(KEY_PASSWORD)));

                // adding to list
                users.add(u);
            } while (c.moveToNext());
        }

        return users;
    }

    /*
     * getting all genres
     * SELECT * FROM genres;
     */
    public ArrayList<Genre> getAllGenres() {
        ArrayList<Genre> genres = new ArrayList<Genre>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENRES;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Genre g = new Genre();
                g.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                g.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));

                genres.add(g);
            } while (c.moveToNext());
        }

        return genres;
    }

    /*
     * getting all artists
     * SELECT * FROM artists;
     */
    public ArrayList<Artist> getAllArtists() {
        ArrayList<Artist> artists = new ArrayList<Artist>();
        String selectQuery = "SELECT  * FROM " + TABLE_ARTISTS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Artist a = new Artist();
                a.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                a.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));
                a.setGenreId(c.getInt(c.getColumnIndexOrThrow(KEY_ARTIST_GENRE_ID)));

                artists.add(a);
            } while (c.moveToNext());
        }

        return artists;
    }

    /*
     * getting all songs
     * SELECT * FROM songs;
     */
    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> songs = new ArrayList<Song>();
        String selectQuery = "SELECT  * FROM " + TABLE_SONGS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Song s = new Song();
                s.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                s.setTitle((c.getString(c.getColumnIndexOrThrow(KEY_SONG_TITLE))));
                s.setArtistId(c.getInt(c.getColumnIndexOrThrow(KEY_SONG_ARTIST_ID)));
                s.setGenreId(c.getInt(c.getColumnIndexOrThrow(KEY_SONG_GENRE_ID)));

                songs.add(s);
            } while (c.moveToNext());
        }

        return songs;
    }

    /*
     * getting all playlists
     * SELECT * FROM playlists;
     */
    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLISTS;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Playlist p = new Playlist();
                p.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                p.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));
                p.setUserId(c.getInt(c.getColumnIndexOrThrow(KEY_PLAYLIST_USER_ID)));

                playlists.add(p);
            } while (c.moveToNext());
        }

        return playlists;
    }

    /*
     * getting all artists by genre
     * SELECT a.id, a.name FROM artists a, genres g WHERE g.name LIKE 'Rock%' AND a.genre_id = g.id;
     */
    public ArrayList<Artist> getArtistsByGenre(String genreName) {
        ArrayList<Artist> artists = new ArrayList<Artist>();

        String selectQuery = "SELECT  a." + KEY_ID + ", a." + KEY_NAME + " FROM "
                + TABLE_ARTISTS + " a, " + TABLE_GENRES + " g WHERE UPPER(g."
                + KEY_NAME + ") LIKE '" + genreName.toUpperCase() + "%'" + " AND a."
                + KEY_ARTIST_GENRE_ID + " = " + "g." + KEY_ID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                long artist_id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
                Artist a = getArtist(artist_id);
                artists.add(a);
            } while (c.moveToNext());
        }

        return artists;
    }

    /*
     * getting all songs by artist
     * SELECT s.id, s.title, s.artist_id, s.genre_id FROM songs s, artists a WHERE a.name LIKE 'Beatles%' AND s.artist_id = a.id;
     */
    public ArrayList<Song> getSongsByArtist(String artistName) {
        ArrayList<Song> songs = new ArrayList<Song>();

        String selectQuery = "SELECT s." + KEY_ID + ", s." + KEY_SONG_TITLE + ", s."
                + KEY_SONG_ARTIST_ID + ", s." + KEY_SONG_GENRE_ID
                + " FROM " + TABLE_SONGS + " s, " + TABLE_ARTISTS + " a "
                + "WHERE UPPER(a." + KEY_NAME + ") LIKE '" + artistName.toUpperCase() + "%' "
                + "AND s." + KEY_SONG_ARTIST_ID + " = a." + KEY_ID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(c.getLong(c.getColumnIndexOrThrow(KEY_ID)));
                song.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_SONG_TITLE)));
                song.setArtistId(c.getLong(c.getColumnIndexOrThrow(KEY_SONG_ARTIST_ID)));
                song.setGenreId(c.getLong(c.getColumnIndexOrThrow(KEY_SONG_GENRE_ID)));
                // artist name
                song.setArtistName(artistName);
                songs.add(song);
            } while (c.moveToNext());
        }
        c.close();

        Log.d(LOG, "Vracam " + songs.size() + " pesama za izvodjaca: " + artistName);
        return songs;
    }

    /*
     * getting all songs by genre
     * SELECT s.id, s.title, s.artist_id, s.genre_id FROM songs s, genres g WHERE g.name LIKE 'Rock%' AND s.genre_id = g.id;
     */
    public ArrayList<Song> getSongsByGenre(String genreName) {
        ArrayList<Song> songs = new ArrayList<Song>();

        String selectQuery = "SELECT s." + KEY_ID + ", s." + KEY_SONG_TITLE + ", s."
                + KEY_SONG_ARTIST_ID + ", s." + KEY_SONG_GENRE_ID
                + " FROM " + TABLE_SONGS + " s, " + TABLE_GENRES + " g "
                + "WHERE UPPER(g." + KEY_NAME + ") LIKE '" + genreName.toUpperCase() + "%' "
                + "AND s." + KEY_SONG_GENRE_ID + " = g." + KEY_ID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Song song = new Song();
                song.setId(c.getLong(c.getColumnIndexOrThrow(KEY_ID)));
                song.setTitle(c.getString(c.getColumnIndexOrThrow(KEY_SONG_TITLE)));
                song.setArtistId(c.getLong(c.getColumnIndexOrThrow(KEY_SONG_ARTIST_ID)));
                song.setGenreId(c.getLong(c.getColumnIndexOrThrow(KEY_SONG_GENRE_ID)));
                song.setGenreName(genreName);
                songs.add(song);
            } while (c.moveToNext());
        }
        c.close();

        Log.d(LOG, "Vracam " + songs.size() + " pesama za zanr: " + genreName);
        return songs;
    }

    /*
     * getting all playlists for a specific user (individual playlists)
     * SELECT * FROM playlists WHERE user_id = 1;
     */
    public ArrayList<Playlist> getPlaylistsByUser(long userId) {
        ArrayList<Playlist> playlists = new ArrayList<Playlist>();

        String selectQuery = "SELECT  * FROM " + TABLE_PLAYLISTS + " WHERE "
                + KEY_PLAYLIST_USER_ID + " = " + userId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Playlist p = new Playlist();
                p.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                p.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));
                p.setUserId(c.getInt(c.getColumnIndexOrThrow(KEY_PLAYLIST_USER_ID)));

                playlists.add(p);
            } while (c.moveToNext());
        }

        return playlists;
    }

    /*
     * getting all songs from a specific playlist
     * SELECT s.id, s.title FROM songs s, playlist_songs ps WHERE ps.playlist_id = 1 AND s.id = ps.song_id;
     */
    public ArrayList<Song> getSongsInPlaylist(long playlistId) {
        ArrayList<Song> songs = new ArrayList<Song>();

        String selectQuery = "SELECT  s." + KEY_ID + ", s." + KEY_SONG_TITLE + " FROM "
                + TABLE_SONGS + " s, " + TABLE_PLAYLIST_SONGS + " ps WHERE ps."
                + KEY_PLAYLIST_ID + " = " + playlistId + " AND s." + KEY_ID
                + " = " + "ps." + KEY_SONG_ID;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                long song_id = c.getInt(c.getColumnIndexOrThrow(KEY_ID));
                Song s = getSong(song_id);
                songs.add(s);
            } while (c.moveToNext());
        }

        return songs;
    }

    /*
     * searching genres by name (for search functionality)
     * SELECT * FROM genres WHERE name LIKE '%rock%';
     */
    public ArrayList<Genre> searchGenresByName(String query) {
        ArrayList<Genre> genres = new ArrayList<Genre>();

        String selectQuery = "SELECT  * FROM " + TABLE_GENRES + " WHERE UPPER("
                + KEY_NAME + ") LIKE '%" + query.toUpperCase() + "%'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Genre g = new Genre();
                g.setId(c.getInt((c.getColumnIndexOrThrow(KEY_ID))));
                g.setName((c.getString(c.getColumnIndexOrThrow(KEY_NAME))));

                genres.add(g);
            } while (c.moveToNext());
        }

        return genres;
    }

    // closing database
    public void closeDB() {
        if (db != null && db.isOpen())
            db.close();
    }

}

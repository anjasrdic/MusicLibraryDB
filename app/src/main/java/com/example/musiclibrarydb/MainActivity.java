package com.example.musiclibrarydb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnSongs, btnArtists, btnGenres, btnPlaylists, btnLogout;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // kreiranje baze i inicijalnih podataka
        createTablesAndInitData();

        btnSongs = findViewById(R.id.btnSongs);
        btnArtists = findViewById(R.id.btnArtists);
        btnGenres = findViewById(R.id.btnGenres);
        btnPlaylists = findViewById(R.id.btnPlaylists);
        btnLogout = findViewById(R.id.btnLogout);

        btnSongs.setOnClickListener(v -> startActivity(new Intent(this, SongsActivity.class)));
        btnArtists.setOnClickListener(v -> startActivity(new Intent(this, ArtistsActivity.class)));
        btnGenres.setOnClickListener(v -> startActivity(new Intent(this, GenresActivity.class)));
        btnPlaylists.setOnClickListener(v -> startActivity(new Intent(this, PlaylistsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // metoda za kreiranje baze i inicijalnih podataka
    void createTablesAndInitData() {
        databaseHelper.createTables();

        if (databaseHelper.getAllArtists().size() == 0) {
            // ubacivanje zanrova i pamcenje IDs
            Genre g1 = new Genre("Rock");
            Genre g2 = new Genre("Pop");
            Genre g3 = new Genre("Alternative");
            Genre g4 = new Genre("New Wave");

            long rockId = databaseHelper.createGenre(g1);
            long popId = databaseHelper.createGenre(g2);
            long altId = databaseHelper.createGenre(g3);
            long newWaveId = databaseHelper.createGenre(g4);

            // ubacivanje izvodjaca i pamcenje IDs
            Artist a1 = new Artist("R.E.M.", "Alternative");
            Artist a2 = new Artist("The Cranberries", "Alternative");
            Artist a3 = new Artist("Blondie", "Pop");
            Artist a4 = new Artist("Culture Club", "New Wave");
            Artist a5 = new Artist("Tina Turner", "Pop");
            Artist a6 = new Artist("Red Hot Chili Peppers", "Rock");
            Artist a7 = new Artist("Men at Work", "New Wave");

            long remId = databaseHelper.createArtist(a1);
            long cranberriesId = databaseHelper.createArtist(a2);
            long blondieId = databaseHelper.createArtist(a3);
            long cultureId = databaseHelper.createArtist(a4);
            long tinaId = databaseHelper.createArtist(a5);
            long rhcpId = databaseHelper.createArtist(a6);
            long menAtWorkId = databaseHelper.createArtist(a7);

            //ubacivanje pesama sa IDs
            Song s1 = new Song("Losing My Religion", "R.E.M.", "Alternative");
            s1.setArtistId(remId);
            s1.setGenreId(altId);
            databaseHelper.createSong(s1);

            Song s2 = new Song("Zombie", "The Cranberries", "Alternative");
            s2.setArtistId(cranberriesId);
            s2.setGenreId(altId);
            databaseHelper.createSong(s2);

            Song s3 = new Song("Heart of Glass", "Blondie", "Pop");
            s3.setArtistId(blondieId);
            s3.setGenreId(popId);
            databaseHelper.createSong(s3);

            Song s4 = new Song("Karma Chameleon", "Culture Club", "New Wave");
            s4.setArtistId(cultureId);
            s4.setGenreId(newWaveId);
            databaseHelper.createSong(s4);

            Song s5 = new Song("What's Love Got to Do with It", "Tina Turner", "Pop");
            s5.setArtistId(tinaId);
            s5.setGenreId(popId);
            databaseHelper.createSong(s5);

            Song s6 = new Song("Under the Bridge", "Red Hot Chili Peppers", "Rock");
            s6.setArtistId(rhcpId);
            s6.setGenreId(rockId);
            databaseHelper.createSong(s6);

            Song s7 = new Song("Down Under", "Men at Work", "New Wave");
            s7.setArtistId(menAtWorkId);
            s7.setGenreId(newWaveId);
            databaseHelper.createSong(s7);

            Song s8 = new Song("Call Me", "Blondie", "Pop");
            s8.setArtistId(blondieId);
            s8.setGenreId(popId);
            databaseHelper.createSong(s8);

            Song s9 = new Song("Otherside", "Red Hot Chili Peppers", "Rock");
            s9.setArtistId(rhcpId);
            s9.setGenreId(rockId);
            databaseHelper.createSong(s9);

            Song s10 = new Song("Dreams", "The Cranberries", "Alternative");
            s10.setArtistId(cranberriesId);
            s10.setGenreId(altId);
            databaseHelper.createSong(s10);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.closeDB();
        }
    }
}
package com.example.musiclibrarydb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;

import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Playlist;
import com.example.musiclibrarydb.sqlite.model.Song;
import com.example.musiclibrarydb.sqlite.model.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // Database Helper
    DatabaseHelper databaseHelper;
    Spinner spnArtists;
    Spinner spnGenres;
    ListView lvSongs;
    Button btnDeleteDatabase;
    Button btnSearchByArtist;
    Button btnSearchByGenre;
    Button btnRestoreDatabase;
    Button btnDeleteArtist;
    Button btnDeleteGenre;
    Button btnDeleteSong;
    Button btnDeletePlaylist;
    Button btnLogout;

    // cuvanje trenutno selektovane pesme za brisanje
    private String selectedSongTitle;
    private long selectedSongId;

    @Override
    protected void onStop() {
        super.onStop();
        //databaseHelper.closeDB();

    }

    //zatvaranje baze u onDestroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.closeDB();
        }
    }
    @Override
    protected void onNightModeChanged(int mode) {
        super.onNightModeChanged(mode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spnArtists = (Spinner) findViewById(R.id.spnArtists);
        spnGenres = (Spinner) findViewById(R.id.spnGenres);
        lvSongs = (ListView) findViewById(R.id.lvSongs);

        btnDeleteDatabase = (Button) findViewById(R.id.btnDeleteDatabase);
        btnDeleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (databaseHelper != null) {
                    databaseHelper.dropTables();
                    loadSpinnerDataArtists(new ArrayList<Artist>());
                    loadSpinnerDataGenres(new ArrayList<>());
                    loadSongsList(new ArrayList<>());
                }

            }
        });

        btnSearchByArtist = (Button) findViewById(R.id.btnSearchByArtist);
        btnSearchByArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spnArtists.getSelectedItem() != null) {
                    String artist = spnArtists.getSelectedItem().toString();
                    List<Song> lst = databaseHelper.getSongsByArtist(artist);

//                    Toast.makeText(MainActivity.this,
//                            "Pronadjeno: " + lst.size() + " pesama",
//                            Toast.LENGTH_LONG).show();

                    loadSongsList((ArrayList<Song>) lst);
                }
            }
        });

        btnSearchByGenre = (Button) findViewById(R.id.btnSearchByGenre);
        btnSearchByGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spnGenres.getSelectedItem() != null) {
                    String genre = spnGenres.getSelectedItem().toString();
                    List<Song> lst = databaseHelper.getSongsByGenre(genre);

//                    Toast.makeText(MainActivity.this,
//                            "Pronadjeno: " + lst.size() + " pesama",
//                            Toast.LENGTH_LONG).show();

                    loadSongsList((ArrayList<Song>) lst);
                }
            }
        });

        btnRestoreDatabase = (Button) findViewById(R.id.btnRestoreDatabase);
        btnRestoreDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTablesAndInitData();
            }
        });

        btnDeleteArtist = (Button) findViewById(R.id.btnDeleteArtist);
        btnDeleteArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spnArtists.getSelectedItem() != null) {
                    String artistName = spnArtists.getSelectedItem().toString();
                    Artist artist = getArtistByName(artistName);
                    if (artist != null) {
                        // provera da li postoje pesme vezane za ovog izvodjaca
                        List<Song> songs = databaseHelper.getSongsByArtist(artistName);
                        if (songs.size() > 0) {
                            // poruka da će i pesme biti obrisane (CASCADE - zato je moralo FK da se naglasi!!!)
                            Toast.makeText(MainActivity.this,
                                    "Brisem izvodjaca i " + songs.size() + " njegovih pesama!",
                                    Toast.LENGTH_LONG).show();
                        }
                        databaseHelper.deleteArtist(artist.getId());
                        refreshData();
                        Toast.makeText(MainActivity.this,
                                "Izvodjac '" + artistName + "' obrisan!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Izaberi izvodjaca za brisanje!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteGenre = (Button) findViewById(R.id.btnDeleteGenre);
        btnDeleteGenre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spnGenres.getSelectedItem() != null) {
                    String genreName = spnGenres.getSelectedItem().toString();
                    Genre genre = getGenreByName(genreName);
                    if (genre != null) {
                        // proveri da li postoje pesme vezane za ovaj zanr
                        List<Song> songs = databaseHelper.getSongsByGenre(genreName);
                        if (songs.size() > 0) {
                            // poruka da će i pesme biti obrisane (CASCADE - zato je moralo FK da se naglasi!!!)
                            Toast.makeText(MainActivity.this,
                                    "Brisem zanr i " + songs.size() + " pesama tog zanra!",
                                    Toast.LENGTH_LONG).show();
                        }
                        databaseHelper.deleteGenre(genre.getId());
                        refreshData();
                        Toast.makeText(MainActivity.this,
                                "Zanr '" + genreName + "' obrisan!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,
                            "Izaberi zanr za brisanje!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeleteSong = (Button) findViewById(R.id.btnDeleteSong);
        btnDeleteSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSongTitle != null && selectedSongId != 0) {
                    //provera da li pesma postoji u nekoj playlisti, za sada samo delete
                    databaseHelper.deleteSong(selectedSongId);
                    refreshData();
                    Toast.makeText(MainActivity.this,
                            "Pesma '" + selectedSongTitle + "' obrisana!",
                            Toast.LENGTH_SHORT).show();
                    selectedSongTitle = null;
                    selectedSongId = 0;
                } else {
                    Toast.makeText(MainActivity.this,
                            "Selektuj pesmu u listi za brisanje!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnDeletePlaylist = (Button) findViewById(R.id.btnDeletePlaylist);
        btnDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,
                        "WIP - ide u PlaylistsActivity",
                        Toast.LENGTH_SHORT).show();
            }
        });


        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // selektovanje pesme iz liste za brisanje
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // hendl na sve pesme iz trenutnog adaptera
                List<Song> allSongs = databaseHelper.getAllSongs();
                // najdi pesmu na poziciji, prema nazivu
                String songTitle = (String) parent.getItemAtPosition(position);
                for (Song s : allSongs) {
                    if (s.getTitle().equals(songTitle)) {
                        selectedSongId = s.getId();
                        selectedSongTitle = s.getTitle();
                        Toast.makeText(MainActivity.this,
                                "Selektovana pesma: " + selectedSongTitle,
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());

        createTablesAndInitData();

    }

    void createTablesAndInitData(){
        databaseHelper.createTables();

        if (databaseHelper.getAllArtists().size() == 0) {
            //ubacivanje zanrova i pamcenje IDeva
            Genre g1 = new Genre("Rock");
            Genre g2 = new Genre("Pop");
            Genre g3 = new Genre("Alternative");
            Genre g4 = new Genre("New Wave");

            long rockId = databaseHelper.createGenre(g1);
            long popId = databaseHelper.createGenre(g2);
            long altId = databaseHelper.createGenre(g3);
            long newWaveId = databaseHelper.createGenre(g4);

            //ubacivanje artisa i pamcenje IDeva
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

            // ubacivanje pesama sa IDevima
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

        List<Artist> la = databaseHelper.getAllArtists();
        loadSpinnerDataArtists((ArrayList<Artist>) la);
        List<Genre> lg = databaseHelper.getAllGenres();
        loadSpinnerDataGenres((ArrayList<Genre>) lg);
        List<Song> ls = databaseHelper.getAllSongs();
        loadSongsList((ArrayList<Song>) ls);
    }

    //refresh
    private void refreshData() {
        List<Artist> artists = databaseHelper.getAllArtists();
        loadSpinnerDataArtists((ArrayList<Artist>) artists);
        List<Genre> genres = databaseHelper.getAllGenres();
        loadSpinnerDataGenres((ArrayList<Genre>) genres);
        List<Song> songs = databaseHelper.getAllSongs();
        loadSongsList((ArrayList<Song>) songs);
        // resetuj selektovanu pesmu
        selectedSongTitle = null;
        selectedSongId = 0;
    }

    // handle
    private Artist getArtistByName(String name) {
        List<Artist> allArtists = databaseHelper.getAllArtists();
        for (Artist a : allArtists) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    private Genre getGenreByName(String name) {
        List<Genre> allGenres = databaseHelper.getAllGenres();
        for (Genre g : allGenres) {
            if (g.getName().equals(name)) {
                return g;
            }
        }
        return null;
    }


    void loadSpinnerDataArtists (ArrayList<Artist> al){
        ArrayList<String> artistnames = new ArrayList<>();
        for (Artist artist : al){
            artistnames.add(artist.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, artistnames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnArtists.setAdapter(dataAdapter);
    }

    void loadSpinnerDataGenres (ArrayList<Genre> al){
        ArrayList<String> genrename = new ArrayList<>();
        for (Genre genre : al){
            genrename.add(genre.getName());
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, genrename);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnGenres.setAdapter(dataAdapter);
    }

    void loadSongsList(ArrayList<Song> list) {

        Log.d("MainActivity", "loadSongsList: " + list.size() + " pesama");

        ArrayList<String> songNames = new ArrayList<>();
        for (Song s : list) {
            songNames.add(s.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songNames);
        lvSongs.setAdapter(adapter);
    }

}
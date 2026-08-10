package com.example.musiclibrarydb;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Song;
import java.util.ArrayList;
import java.util.List;

public class SongsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView lvSongs;
    Spinner spnArtists, spnGenres;
    Button btnSearchByArtist, btnSearchByGenre, btnAddSong, btnDeleteSong, btnEditSong,btnBackToMain;

    // cuvanje trenutno selektovane pesme za brisanje
    private String selectedSongTitle;
    private long selectedSongId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        databaseHelper = new DatabaseHelper(this);

        lvSongs = findViewById(R.id.lvSongs);
        spnArtists = findViewById(R.id.spnArtists);
        spnGenres = findViewById(R.id.spnGenres);
        btnSearchByArtist = findViewById(R.id.btnSearchByArtist);
        btnSearchByGenre = findViewById(R.id.btnSearchByGenre);
        btnAddSong = findViewById(R.id.btnAddSong);
        btnDeleteSong = findViewById(R.id.btnDeleteSong);
        btnEditSong = findViewById(R.id.btnEditSong);

        loadSpinners();
        refreshData();

        // pretraga pesama po izvodjacu
        btnSearchByArtist.setOnClickListener(v -> {
            if (spnArtists.getSelectedItem() != null) {
                String artist = spnArtists.getSelectedItem().toString();
                List<Song> songs = databaseHelper.getSongsByArtist(artist);
                loadSongsList((ArrayList<Song>) songs);
            }
        });

        // pretraga pesama po zanru
        btnSearchByGenre.setOnClickListener(v -> {
            if (spnGenres.getSelectedItem() != null) {
                String genre = spnGenres.getSelectedItem().toString();
                List<Song> songs = databaseHelper.getSongsByGenre(genre);
                loadSongsList((ArrayList<Song>) songs);
            }
        });

        // ddodavanje nove pesme
        btnAddSong.setOnClickListener(v -> showAddSongDialog());

        // brisanje pesme
        btnDeleteSong.setOnClickListener(v -> {
            if (selectedSongTitle != null && selectedSongId != 0) {
                //provera da li pesma postoji u nekoj playlisti, za sada samo delete
                databaseHelper.deleteSong(selectedSongId);
                refreshData();
                Toast.makeText(this, "Pesma '" + selectedSongTitle + "' obrisana!", Toast.LENGTH_SHORT).show();
                selectedSongTitle = null;
                selectedSongId = 0;
            } else {
                Toast.makeText(this, "Selektuj pesmu u listi za brisanje!", Toast.LENGTH_SHORT).show();
            }
        });

        // editovanje pesme
        btnEditSong.setOnClickListener(v -> {
            if (selectedSongTitle != null && selectedSongId != 0) {
                Song song = databaseHelper.getSong(selectedSongId);
                if (song != null) showEditSongDialog(song);
            } else {
                Toast.makeText(this, "Selektuj pesmu u listi za izmenu!", Toast.LENGTH_SHORT).show();
            }
        });

        // selektovanje pesme iz liste za brisanje
        lvSongs.setOnItemClickListener((parent, view, position, id) -> {
            // hendl na sve pesme iz trenutnog adaptera
            List<Song> allSongs = databaseHelper.getAllSongs();
            // najdi pesmu na poziciji, prema nazivu
            String songTitle = (String) parent.getItemAtPosition(position);
            for (Song s : allSongs) {
                if (s.getTitle().equals(songTitle)) {
                    selectedSongId = s.getId();
                    selectedSongTitle = s.getTitle();
                    Toast.makeText(this, "Selektovana pesma: " + selectedSongTitle, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        // back to main
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(SongsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadSpinners() {
        List<Artist> artists = databaseHelper.getAllArtists();
        List<Genre> genres = databaseHelper.getAllGenres();

        ArrayAdapter<String> artistAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getNames(artists));
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnArtists.setAdapter(artistAdapter);

        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getGenreNames(genres));
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenres.setAdapter(genreAdapter);
    }

    private ArrayList<String> getNames(List<Artist> artists) {
        ArrayList<String> names = new ArrayList<>();
        for (Artist a : artists) names.add(a.getName());
        return names;
    }

    private ArrayList<String> getGenreNames(List<Genre> genres) {
        ArrayList<String> names = new ArrayList<>();
        for (Genre g : genres) names.add(g.getName());
        return names;
    }

    private void loadSongsList(ArrayList<Song> list) {
        ArrayList<String> songNames = new ArrayList<>();
        for (Song s : list) songNames.add(s.getTitle());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songNames);
        lvSongs.setAdapter(adapter);
    }

    private void refreshData() {
        List<Song> songs = databaseHelper.getAllSongs();
        loadSongsList((ArrayList<Song>) songs);
        selectedSongTitle = null;
        selectedSongId = 0;
    }

    // dijalog za dodavanje pesme
    private void showAddSongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj novu pesmu");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etTitle = new EditText(this);
        etTitle.setHint("Naziv pesme");
        layout.addView(etTitle);

        // spinner za artista
        TextView tvArtist = new TextView(this);
        tvArtist.setText("Izvodjac:");
        layout.addView(tvArtist);

        Spinner spinnerArtist = new Spinner(this);
        List<Artist> artists = databaseHelper.getAllArtists();
        ArrayAdapter<String> artistAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getNames(artists));
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);
        layout.addView(spinnerArtist);

        // spinner za zanr
        TextView tvGenre = new TextView(this);
        tvGenre.setText("Zanr:");
        layout.addView(tvGenre);

        Spinner spinnerGenre = new Spinner(this);
        List<Genre> genres = databaseHelper.getAllGenres();
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getGenreNames(genres));
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);
        layout.addView(spinnerGenre);

        builder.setView(layout);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                String artistName = spinnerArtist.getSelectedItem().toString();
                String genreName = spinnerGenre.getSelectedItem().toString();
                Artist artist = getArtistByName(artistName);
                Genre genre = getGenreByName(genreName);
                if (artist != null && genre != null) {
                    Song song = new Song(title, artistName, genreName);
                    song.setArtistId(artist.getId());
                    song.setGenreId(genre.getId());
                    databaseHelper.createSong(song);
                    refreshData();
                    Toast.makeText(this, "Pesma '" + title + "' dodata!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    // dijalog za izmenu pesme
    private void showEditSongDialog(Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Izmeni pesmu");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // polje za novi naziv
        EditText etNewTitle = new EditText(this);
        etNewTitle.setHint("Novi naziv pesme");
        etNewTitle.setText(song.getTitle());
        layout.addView(etNewTitle);

        // spinner za artista
        TextView tvArtist = new TextView(this);
        tvArtist.setText("Izvodjac:");
        layout.addView(tvArtist);

        Spinner spinnerArtist = new Spinner(this);
        List<Artist> artists = databaseHelper.getAllArtists();
        ArrayList<String> artistNames = new ArrayList<>();
        for (Artist a : artists) {
            artistNames.add(a.getName());
        }
        ArrayAdapter<String> artistAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, artistNames);
        artistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArtist.setAdapter(artistAdapter);

        // postavlja trenutnog artista
        int currentArtistIndex = artistNames.indexOf(song.getArtistName());
        if (currentArtistIndex >= 0) {
            spinnerArtist.setSelection(currentArtistIndex);
        }
        layout.addView(spinnerArtist);

        // spinner za zanr
        TextView tvGenre = new TextView(this);
        tvGenre.setText("Zanr:");
        layout.addView(tvGenre);

        Spinner spinnerGenre = new Spinner(this);
        List<Genre> genres = databaseHelper.getAllGenres();
        ArrayList<String> genreNames = new ArrayList<>();
        for (Genre g : genres) {
            genreNames.add(g.getName());
        }
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genreNames);
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(genreAdapter);

        // postavlja trenutni zanr
        int currentGenreIndex = genreNames.indexOf(song.getGenreName());
        if (currentGenreIndex >= 0) {
            spinnerGenre.setSelection(currentGenreIndex);
        }
        layout.addView(spinnerGenre);

        builder.setView(layout);

        builder.setPositiveButton("Sacuvaj", (dialog, which) -> {
            String newTitle = etNewTitle.getText().toString().trim();
            if (!newTitle.isEmpty()) {
                String newArtistName = spinnerArtist.getSelectedItem().toString();
                String newGenreName = spinnerGenre.getSelectedItem().toString();

                //pronadji id izvodjaca
                Artist artist = getArtistByName(newArtistName);
                // pronadji id zanrs
                Genre genre = getGenreByName(newGenreName);

                if (artist != null && genre != null) {
                    song.setTitle(newTitle);
                    song.setArtistId(artist.getId());
                    song.setArtistName(newArtistName);
                    song.setGenreId(genre.getId());
                    song.setGenreName(newGenreName);
                    databaseHelper.updateSong(song);
                    refreshData();
                    Toast.makeText(this, "Pesma izmenjena!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    private Artist getArtistByName(String name) {
        for (Artist a : databaseHelper.getAllArtists()) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }

    private Genre getGenreByName(String name) {
        for (Genre g : databaseHelper.getAllGenres()) {
            if (g.getName().equals(name)) return g;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.closeDB();
    }
}
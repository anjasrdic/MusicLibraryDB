package com.example.musiclibrarydb;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;
import com.example.musiclibrarydb.sqlite.model.Artist;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.ArrayList;
import java.util.List;

public class ArtistsActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView lvArtists;
    Spinner spnGenres;
    Button btnSearchByGenre, btnAddArtist, btnDeleteArtist, btnEditArtist, btnBackToMain;

    private String selectedArtistName;
    private long selectedArtistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        databaseHelper = new DatabaseHelper(this);

        lvArtists = findViewById(R.id.lvArtists);
        spnGenres = findViewById(R.id.spnGenres);
        btnSearchByGenre = findViewById(R.id.btnSearchByGenre);
        btnAddArtist = findViewById(R.id.btnAddArtist);
        btnDeleteArtist = findViewById(R.id.btnDeleteArtist);
        btnEditArtist = findViewById(R.id.btnEditArtist);

        loadSpinners();
        refreshData();

        // petraga izvodjaca po zanru
        btnSearchByGenre.setOnClickListener(v -> {
            if (spnGenres.getSelectedItem() != null) {
                String genre = spnGenres.getSelectedItem().toString();
                List<Artist> artists = databaseHelper.getArtistsByGenre(genre);
                loadArtistsList((ArrayList<Artist>) artists);
            }
        });

        // dodavanje novog izvodjaca
        btnAddArtist.setOnClickListener(v -> showAddArtistDialog());

        // brisanje izvodjaca
        btnDeleteArtist.setOnClickListener(v -> {
            if (selectedArtistName != null && selectedArtistId != 0) {
                // provera da li postoje pesme vezane za ovog izvodjaca
                List<Song> songs = databaseHelper.getSongsByArtist(selectedArtistName);
                if (!songs.isEmpty()) {
                    // poruka da će i pesme biti obrisane (CASCADE - zato je moralo FK da se naglasi!!!)
                    Toast.makeText(this, "Brisem izvodjaca i " + songs.size() + " njegovih pesama!", Toast.LENGTH_LONG).show();
                }
                databaseHelper.deleteArtist(selectedArtistId);
                refreshData();
                Toast.makeText(this, "Izvodjac '" + selectedArtistName + "' obrisan!", Toast.LENGTH_SHORT).show();
                selectedArtistName = null;
                selectedArtistId = 0;
            } else {
                Toast.makeText(this, "Izaberi izvodjaca za brisanje!", Toast.LENGTH_SHORT).show();
            }
        });

        // editovanje izvodjaca
        btnEditArtist.setOnClickListener(v -> {
            if (selectedArtistName != null && selectedArtistId != 0) {
                Artist artist = databaseHelper.getArtist(selectedArtistId);
                if (artist != null) showEditArtistDialog(artist);
            } else {
                Toast.makeText(this, "Izaberi izvodjaca za izmenu!", Toast.LENGTH_SHORT).show();
            }
        });

        // selektovanje izvodjaca iz liste
        lvArtists.setOnItemClickListener((parent, view, position, id) -> {
            String artistName = (String) parent.getItemAtPosition(position);
            List<Artist> allArtists = databaseHelper.getAllArtists();
            for (Artist a : allArtists) {
                if (a.getName().equals(artistName)) {
                    selectedArtistId = a.getId();
                    selectedArtistName = a.getName();
                    Toast.makeText(this, "Selektovan izvodjac: " + selectedArtistName, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        // back to main
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(ArtistsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadSpinners() {
        List<Genre> genres = databaseHelper.getAllGenres();
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getGenreNames(genres));
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenres.setAdapter(genreAdapter);
    }

    private ArrayList<String> getGenreNames(List<Genre> genres) {
        ArrayList<String> names = new ArrayList<>();
        for (Genre g : genres) names.add(g.getName());
        return names;
    }

    private void loadArtistsList(ArrayList<Artist> list) {
        ArrayList<String> names = new ArrayList<>();
        for (Artist a : list) names.add(a.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        lvArtists.setAdapter(adapter);
    }

    private void refreshData() {
        List<Artist> artists = databaseHelper.getAllArtists();
        loadArtistsList((ArrayList<Artist>) artists);
        selectedArtistName = null;
        selectedArtistId = 0;
    }

    // dijalog za dodavanje izvodjaca
    private void showAddArtistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj novog izvodjaca");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etName = new EditText(this);
        etName.setHint("Naziv izvodjaca");
        layout.addView(etName);

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
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                String genreName = spinnerGenre.getSelectedItem().toString();
                Genre genre = getGenreByName(genreName);
                if (genre != null) {
                    Artist artist = new Artist(name, genreName);
                    artist.setGenreId(genre.getId());
                    databaseHelper.createArtist(artist);
                    refreshData();
                    Toast.makeText(this, "Izvodjac '" + name + "' dodat!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    // dijalog za izmenu izvodjaca
    private void showEditArtistDialog(Artist artist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Izmeni izvodjaca");

        // layout za dijalog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // polje za novi naziv
        EditText etNewName = new EditText(this);
        etNewName.setHint("Novi naziv izvodjaca");
        etNewName.setText(artist.getName());
        layout.addView(etNewName);

        builder.setView(layout);

        builder.setPositiveButton("Sacuvaj", (dialog, which) -> {
            String newName = etNewName.getText().toString().trim();
            if (!newName.isEmpty()) {
                artist.setName(newName);
                databaseHelper.updateArtist(artist);
                refreshData();
                Toast.makeText(this, "Izvodjac izmenjen!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
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
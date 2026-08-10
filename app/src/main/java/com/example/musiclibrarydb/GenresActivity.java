package com.example.musiclibrarydb;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;
import com.example.musiclibrarydb.sqlite.model.Genre;
import com.example.musiclibrarydb.sqlite.model.Song;

import java.util.ArrayList;
import java.util.List;

public class GenresActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    ListView lvGenres;
    EditText etSearch;
    Button btnSearch, btnAddGenre, btnDeleteGenre, btnEditGenre, btnBackToMain;

    private String selectedGenreName;
    private long selectedGenreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);

        databaseHelper = new DatabaseHelper(this);

        lvGenres = findViewById(R.id.lvGenres);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddGenre = findViewById(R.id.btnAddGenre);
        btnDeleteGenre = findViewById(R.id.btnDeleteGenre);
        btnEditGenre = findViewById(R.id.btnEditGenre);

        refreshData();

        // pretraga zanrpva
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                List<Genre> genres = databaseHelper.searchGenresByName(query);
                loadGenresList((ArrayList<Genre>) genres);
            } else {
                refreshData();
            }
        });

        // dodavanje novog zanra
        btnAddGenre.setOnClickListener(v -> showAddGenreDialog());

        // brisanje zanra
        btnDeleteGenre.setOnClickListener(v -> {
            if (selectedGenreName != null && selectedGenreId != 0) {
                // proveri da li postoje pesme vezane za ovaj zanr
                List<Song> songs = databaseHelper.getSongsByGenre(selectedGenreName);
                if (!songs.isEmpty()) {
                    // poruka da će i pesme biti obrisane (CASCADE - zato je moralo FK da se naglasi!!!)
                    Toast.makeText(this, "Brisem zanr i " + songs.size() + " pesama tog zanra!", Toast.LENGTH_LONG).show();
                }
                databaseHelper.deleteGenre(selectedGenreId);
                refreshData();
                Toast.makeText(this, "Zanr '" + selectedGenreName + "' obrisan!", Toast.LENGTH_SHORT).show();
                selectedGenreName = null;
                selectedGenreId = 0;
            } else {
                Toast.makeText(this, "Izaberi zanr za brisanje!", Toast.LENGTH_SHORT).show();
            }
        });

        // editovanje zanra
        btnEditGenre.setOnClickListener(v -> {
            if (selectedGenreName != null && selectedGenreId != 0) {
                Genre genre = databaseHelper.getGenre(selectedGenreId);
                if (genre != null) showEditGenreDialog(genre);
            } else {
                Toast.makeText(this, "Izaberi zanr za izmenu!", Toast.LENGTH_SHORT).show();
            }
        });

        // selektovanje zanra iz liste
        lvGenres.setOnItemClickListener((parent, view, position, id) -> {
            String genreName = (String) parent.getItemAtPosition(position);
            List<Genre> allGenres = databaseHelper.getAllGenres();
            for (Genre g : allGenres) {
                if (g.getName().equals(genreName)) {
                    selectedGenreId = g.getId();
                    selectedGenreName = g.getName();
                    Toast.makeText(this, "Selektovan zanr: " + selectedGenreName, Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        // back to main
        btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(GenresActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadGenresList(ArrayList<Genre> list) {
        ArrayList<String> names = new ArrayList<>();
        for (Genre g : list) names.add(g.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        lvGenres.setAdapter(adapter);
    }

    private void refreshData() {
        List<Genre> genres = databaseHelper.getAllGenres();
        loadGenresList((ArrayList<Genre>) genres);
        selectedGenreName = null;
        selectedGenreId = 0;
    }

    // dijalog za dodavanje zanra
    private void showAddGenreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dodaj novi zanr");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etName = new EditText(this);
        etName.setHint("Naziv zanra");
        layout.addView(etName);

        builder.setView(layout);

        builder.setPositiveButton("Dodaj", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                Genre genre = new Genre(name);
                databaseHelper.createGenre(genre);
                refreshData();
                Toast.makeText(this, "Zanr '" + name + "' dodat!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    // dijalog za izmenu zanra
    private void showEditGenreDialog(Genre genre) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Izmeni zanr");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etNewName = new EditText(this);
        etNewName.setHint("Novi naziv zanra");
        etNewName.setText(genre.getName());
        layout.addView(etNewName);

        builder.setView(layout);

        builder.setPositiveButton("Sacuvaj", (dialog, which) -> {
            String newName = etNewName.getText().toString().trim();
            if (!newName.isEmpty()) {
                genre.setName(newName);
                databaseHelper.updateGenre(genre);
                refreshData();
                Toast.makeText(this, "Zanr izmenjen!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Naziv ne moze biti prazan!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Odustani", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.closeDB();
    }
}
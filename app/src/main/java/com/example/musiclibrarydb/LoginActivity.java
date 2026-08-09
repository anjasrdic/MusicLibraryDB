package com.example.musiclibrarydb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musiclibrarydb.sqlite.helper.DatabaseHelper;
import com.example.musiclibrarydb.sqlite.model.User;

public class LoginActivity extends AppCompatActivity {

    // Database Helper
    DatabaseHelper databaseHelper;

    // UI elementi
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inicijalizacija UI elemenata
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Database Helper
        databaseHelper = new DatabaseHelper(this);

        // btn za login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                //da li su polja prazna
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,
                            "Unesite korisnicko ime i sifru!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //da li korisnik postoji u bazi
                User user = databaseHelper.getUserByUsernameAndPassword(username, password);

                if (user != null) {
                    //korisnik postoji, pass correct -> prelazak na MainAct
                    Toast.makeText(LoginActivity.this,
                            "Dobrodosli, " + username + "!",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //username or pass ne postoje -> provera da li username vec postoji (incorr pass) ili je novi user
                    User existingUser = databaseHelper.getUserByUsername(username);

                    if (existingUser != null) {
                        //username postoji, ali pass incorrect
                        Toast.makeText(LoginActivity.this,
                                "Pogresna sifra!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // novi korisnik -> dodaje ga u bazu
                        User newUser = new User(username, password);
                        long userId = databaseHelper.createUser(newUser);

                        if (userId != -1) {
                            Toast.makeText(LoginActivity.this,
                                    "Novi korisnik kreiran! Dobrodosli, " + username + "!",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Greska pri kreiranju korisnika!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.closeDB();
        }
    }
}
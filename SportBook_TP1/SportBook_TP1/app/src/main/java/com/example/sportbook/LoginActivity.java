package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editEmail;
    private CheckBox checkSouvenir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Lier le layout XML à cette activité
        setContentView(R.layout.activity_login);

        // 2) Récupérer les vues déclarées dans le XML grâce à leur id
        editNom = findViewById(R.id.editNom);
        editEmail = findViewById(R.id.editEmail);
        checkSouvenir = findViewById(R.id.checkSouvenir);
        Button btnConnexion = findViewById(R.id.btnConnexion);

        // 3) Réagir au clic sur le bouton
        btnConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seConnecter();
            }
        });
    }

    private void seConnecter() {
        String nom = editNom.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        // Validation des champs
        if (nom.isEmpty()) {
            editNom.setError(getString(R.string.err_nom));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.err_email));
            return;
        }

        if (checkSouvenir.isChecked()) {
            Toast.makeText(this, getString(R.string.souvenir), Toast.LENGTH_SHORT).show();
        }

        // 4) Lien vers l'activité suivante avec un Intent explicite
        Intent intent = new Intent(LoginActivity.this, AccueilActivity.class);
        startActivity(intent);
    }
}

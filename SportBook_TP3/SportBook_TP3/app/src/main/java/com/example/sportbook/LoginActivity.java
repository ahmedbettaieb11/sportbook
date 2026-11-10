package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TP2 – Activité 1 (améliorée) : connexion.
 * Nouveauté : les données saisies sont envoyées à l'Accueil dans un Bundle.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText editNom;
    private EditText editEmail;
    private CheckBox checkSouvenir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editNom = findViewById(R.id.editNom);
        editEmail = findViewById(R.id.editEmail);
        checkSouvenir = findViewById(R.id.checkSouvenir);
        Button btnConnexion = findViewById(R.id.btnConnexion);

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

        if (nom.isEmpty()) {
            editNom.setError(getString(R.string.err_nom));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.err_email));
            return;
        }

        // 1) Créer le Bundle et y mettre les données
        Bundle b = new Bundle();
        b.putString(Keys.NOM, nom);
        b.putString(Keys.EMAIL, email);
        b.putBoolean(Keys.SOUVENIR, checkSouvenir.isChecked());

        // 2) Créer l'Intent, 3) y insérer le Bundle, 4) lancer l'activité
        Intent intent = new Intent(LoginActivity.this, AccueilActivity.class);
        intent.putExtra(Keys.EXTRA_BUNDLE, b);
        startActivity(intent);
    }
}

package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * TP2 – NOUVELLE activité 5 : récapitulatif à valider.
 * Ouverte avec startActivityForResult par ReservationActivity.
 *   « Confirmer » → setResult(RESULT_OK) + n° de réservation
 *   « Modifier »  → setResult(RESULT_CANCELED)
 */
public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        Bundle recu = getIntent().getBundleExtra(Keys.EXTRA_BUNDLE);
        if (recu == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        String options = "";
        if (recu.getBoolean(Keys.MATERIEL)) options += getString(R.string.opt_materiel);
        if (recu.getBoolean(Keys.ECLAIRAGE)) {
            options += (options.isEmpty() ? "" : ", ") + getString(R.string.opt_eclairage);
        }
        if (options.isEmpty()) options = getString(R.string.conf_aucune);

        ((TextView) findViewById(R.id.tvConfNom)).setText(getString(R.string.conf_nom, recu.getString(Keys.NOM, "")));
        ((TextView) findViewById(R.id.tvConfEmail)).setText(getString(R.string.conf_email, recu.getString(Keys.EMAIL, "")));
        ((TextView) findViewById(R.id.tvConfSport)).setText(getString(R.string.conf_sport, recu.getString(Keys.SPORT, "")));
        ((TextView) findViewById(R.id.tvConfJoueurs)).setText(getString(R.string.conf_joueurs, recu.getInt(Keys.JOUEURS, 0)));
        ((TextView) findViewById(R.id.tvConfTerrain)).setText(getString(R.string.conf_terrain, recu.getString(Keys.TERRAIN, "")));
        ((TextView) findViewById(R.id.tvConfCreneau)).setText(getString(R.string.conf_creneau,
                recu.getString(Keys.DATE, ""), recu.getString(Keys.HEURE, "")));
        ((TextView) findViewById(R.id.tvConfDuree)).setText(getString(R.string.conf_duree, recu.getInt(Keys.DUREE, 0)));
        ((TextView) findViewById(R.id.tvConfOptions)).setText(getString(R.string.conf_options, options));
        ((TextView) findViewById(R.id.tvConfTotal)).setText(getString(R.string.conf_total, recu.getInt(Keys.TOTAL, 0)));

        Button btnConfirmer = findViewById(R.id.btnConfirmer);
        Button btnModifier = findViewById(R.id.btnModifier);

        // RETOUR « OK » avec un numéro de réservation
        btnConfirmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "SB-" + (1000 + new Random().nextInt(9000));
                Bundle b = new Bundle();
                b.putString(Keys.NUMERO, numero);

                Intent resultat = new Intent();
                resultat.putExtra(Keys.EXTRA_BUNDLE, b);
                setResult(RESULT_OK, resultat);
                finish();
            }
        });

        // RETOUR « annulé » : l'utilisateur veut modifier
        btnModifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}

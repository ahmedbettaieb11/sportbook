package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TP1 – Activité 2 : choix du sport (RadioButton), terrain couvert (Switch)
 * et nombre de joueurs (SeekBar). Ouvre l'activité Réservation.
 */
public class AccueilActivity extends AppCompatActivity {

    private static final int JOUEURS_MIN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        final RadioGroup rgSport = findViewById(R.id.rgSport);
        final Switch switchCouvert = findViewById(R.id.switchCouvert);
        final SeekBar seekJoueurs = findViewById(R.id.seekJoueurs);
        final TextView tvJoueurs = findViewById(R.id.tvJoueurs);
        Button btnContinuer = findViewById(R.id.btnContinuer);
        Button btnDeconnexion = findViewById(R.id.btnDeconnexion);

        // Affichage initial du nombre de joueurs
        tvJoueurs.setText(getString(R.string.label_joueurs, seekJoueurs.getProgress() + JOUEURS_MIN));

        // Mise à jour du texte quand on fait glisser la SeekBar
        seekJoueurs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvJoueurs.setText(getString(R.string.label_joueurs, progress + JOUEURS_MIN));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnContinuer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rgSport.getCheckedRadioButtonId();
                int sportRes = (id == R.id.radioBasket) ? R.string.sport_basket
                        : (id == R.id.radioFoot) ? R.string.sport_foot
                        : R.string.sport_padel;
                String resume = getString(sportRes) + " · "
                        + getString(R.string.label_joueurs, seekJoueurs.getProgress() + JOUEURS_MIN)
                        + (switchCouvert.isChecked() ? " · " + getString(R.string.terrain_couvert) : "");
                Toast.makeText(AccueilActivity.this, resume, Toast.LENGTH_SHORT).show();

                // Lien vers l'activité 3
                startActivity(new Intent(AccueilActivity.this, ReservationActivity.class));
            }
        });

        // Déconnexion : on ferme cet écran et on revient à la connexion
        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

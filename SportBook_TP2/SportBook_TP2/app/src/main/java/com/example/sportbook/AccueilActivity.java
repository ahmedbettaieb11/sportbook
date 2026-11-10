package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TP2 – Activité 2 (améliorée) : reçoit le nom et l'e-mail (Bundle),
 * affiche « Bonjour … », puis transmet le Bundle enrichi (sport, couvert,
 * joueurs) à la Réservation.
 */
public class AccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        // Réception : getIntent() puis getBundleExtra()
        final Bundle recu = getIntent().getBundleExtra(Keys.EXTRA_BUNDLE);
        if (recu == null) {          // toujours vérifier le Bundle reçu
            finish();
            return;
        }

        TextView tvBienvenue = findViewById(R.id.tvBienvenue);
        TextView tvSousTitre = findViewById(R.id.tvSousTitre);
        tvBienvenue.setText(getString(R.string.accueil_bonjour, recu.getString(Keys.NOM, "")));
        tvSousTitre.setText(recu.getString(Keys.EMAIL, ""));

        final RadioGroup rgSport = findViewById(R.id.rgSport);
        final Switch switchCouvert = findViewById(R.id.switchCouvert);
        final SeekBar seekJoueurs = findViewById(R.id.seekJoueurs);
        final TextView tvJoueurs = findViewById(R.id.tvJoueurs);
        Button btnContinuer = findViewById(R.id.btnContinuer);
        Button btnDeconnexion = findViewById(R.id.btnDeconnexion);

        tvJoueurs.setText(getString(R.string.label_joueurs, seekJoueurs.getProgress() + Keys.JOUEURS_MIN));
        seekJoueurs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvJoueurs.setText(getString(R.string.label_joueurs, progress + Keys.JOUEURS_MIN));
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
                int code;
                int libelle;
                if (id == R.id.radioBasket) {
                    code = Keys.SPORT_BASKET;
                    libelle = R.string.sport_basket;
                } else if (id == R.id.radioFoot) {
                    code = Keys.SPORT_FOOT;
                    libelle = R.string.sport_foot;
                } else {
                    code = Keys.SPORT_PADEL;
                    libelle = R.string.sport_padel;
                }

                // Copie du Bundle reçu (on garde nom + e-mail) + nouvelles données
                Bundle b = new Bundle(recu);
                b.putInt(Keys.SPORT_CODE, code);
                b.putString(Keys.SPORT, getString(libelle));
                b.putBoolean(Keys.COUVERT, switchCouvert.isChecked());
                b.putInt(Keys.JOUEURS, seekJoueurs.getProgress() + Keys.JOUEURS_MIN);

                Intent intent = new Intent(AccueilActivity.this, ReservationActivity.class);
                intent.putExtra(Keys.EXTRA_BUNDLE, b);
                startActivity(intent);
            }
        });

        btnDeconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

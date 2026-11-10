package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * TP2 – Activité 3 (améliorée) : c'est le « centre » des allers-retours.
 *
 *   ALLER 1  : bouton « Choisir »  → startActivityForResult(TerrainsActivity, REQ_TERRAIN)
 *   RETOUR 1 : onActivityResult(REQ_TERRAIN)       → terrain + prix reçus
 *   ALLER 2  : fin de la ProgressBar → startActivityForResult(ConfirmationActivity, REQ_CONFIRMATION)
 *   RETOUR 2 : onActivityResult(REQ_CONFIRMATION)  → n° de réservation (OK) ou « Modifier » (CANCELED)
 */
@SuppressWarnings("deprecation") // startActivityForResult est la méthode vue en cours
public class ReservationActivity extends AppCompatActivity {

    private static final int INTERVALLE_MS = 100;
    private static final String ETAT_TERRAIN = "etat_terrain";
    private static final String ETAT_PRIX = "etat_prix";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Bundle recu;

    private TextView tvTerrainChoisi;
    private TextView tvStatut;
    private EditText editDate;
    private EditText editHeure;
    private RadioGroup rgDuree;
    private CheckBox checkMateriel;
    private CheckBox checkEclairage;
    private ProgressBar progressBar;
    private Button btnReserver;

    private String terrain = null;   // rempli par le retour de TerrainsActivity
    private int prixHeure = 0;
    private int progression = 0;
    private boolean enCours = false;

    private final Runnable miseAJour = new Runnable() {
        @Override
        public void run() {
            progression += 5;
            progressBar.setProgress(progression);
            tvStatut.setText(getString(R.string.progression, progression));
            if (progression >= 100) {
                enCours = false;
                ouvrirConfirmation();
            } else if (enCours) {
                handler.postDelayed(this, INTERVALLE_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        recu = getIntent().getBundleExtra(Keys.EXTRA_BUNDLE);
        if (recu == null) {
            finish();
            return;
        }

        TextView tvRecap = findViewById(R.id.tvRecap);
        tvRecap.setText(getString(R.string.resa_recap,
                recu.getString(Keys.SPORT, ""), recu.getInt(Keys.JOUEURS, Keys.JOUEURS_MIN)));

        tvTerrainChoisi = findViewById(R.id.tvTerrainChoisi);
        tvStatut = findViewById(R.id.tvStatut);
        editDate = findViewById(R.id.editDate);
        editHeure = findViewById(R.id.editHeure);
        rgDuree = findViewById(R.id.rgDuree);
        checkMateriel = findViewById(R.id.checkMateriel);
        checkEclairage = findViewById(R.id.checkEclairage);
        progressBar = findViewById(R.id.progressResa);
        btnReserver = findViewById(R.id.btnReserver);
        Button btnChoisirTerrain = findViewById(R.id.btnChoisirTerrain);
        Button btnRetour = findViewById(R.id.btnRetour);

        // Garder le terrain choisi si l'écran tourne
        if (savedInstanceState != null) {
            terrain = savedInstanceState.getString(ETAT_TERRAIN);
            prixHeure = savedInstanceState.getInt(ETAT_PRIX, 0);
        }
        afficherTerrain();

        // ===== ALLER 1 : vers TerrainsActivity, on attend un résultat =====
        btnChoisirTerrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(Keys.SPORT, recu.getString(Keys.SPORT, ""));
                b.putInt(Keys.SPORT_CODE, recu.getInt(Keys.SPORT_CODE, Keys.SPORT_PADEL));
                b.putBoolean(Keys.COUVERT, recu.getBoolean(Keys.COUVERT, false));

                Intent intent = new Intent(ReservationActivity.this, TerrainsActivity.class);
                intent.putExtra(Keys.EXTRA_BUNDLE, b);
                startActivityForResult(intent, Keys.REQ_TERRAIN);
            }
        });

        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demarrerReservation();
            }
        });

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void afficherTerrain() {
        if (terrain == null) {
            tvTerrainChoisi.setText(R.string.aucun_terrain);
        } else {
            tvTerrainChoisi.setText(getString(R.string.terrain_choisi, terrain, prixHeure));
        }
    }

    private int dureeChoisie() {
        int id = rgDuree.getCheckedRadioButtonId();
        if (id == R.id.radio90) return 90;
        if (id == R.id.radio120) return 120;
        return 60;
    }

    private void demarrerReservation() {
        if (terrain == null) {
            Toast.makeText(this, R.string.err_terrain, Toast.LENGTH_SHORT).show();
            return;
        }
        if (editDate.getText().toString().trim().isEmpty()) {
            editDate.setError(getString(R.string.err_date));
            return;
        }
        if (editHeure.getText().toString().trim().isEmpty()) {
            editHeure.setError(getString(R.string.err_heure));
            return;
        }
        progression = 0;
        enCours = true;
        btnReserver.setEnabled(false);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        tvStatut.setVisibility(View.VISIBLE);
        handler.postDelayed(miseAJour, INTERVALLE_MS);
    }

    // ===== ALLER 2 : vers ConfirmationActivity, on attend un résultat =====
    private void ouvrirConfirmation() {
        int duree = dureeChoisie();
        boolean materiel = checkMateriel.isChecked();
        boolean eclairage = checkEclairage.isChecked();

        Bundle b = new Bundle(recu);   // nom, e-mail, sport, joueurs…
        b.putString(Keys.TERRAIN, terrain);
        b.putInt(Keys.PRIX_HEURE, prixHeure);
        b.putString(Keys.DATE, editDate.getText().toString().trim());
        b.putString(Keys.HEURE, editHeure.getText().toString().trim());
        b.putInt(Keys.DUREE, duree);
        b.putBoolean(Keys.MATERIEL, materiel);
        b.putBoolean(Keys.ECLAIRAGE, eclairage);
        b.putInt(Keys.TOTAL, Keys.calculerTotal(prixHeure, duree, materiel, eclairage));

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra(Keys.EXTRA_BUNDLE, b);
        startActivityForResult(intent, Keys.REQ_CONFIRMATION);

        btnReserver.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    // ===== RETOURS : Android appelle cette méthode quand l'activité ouverte se ferme =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Keys.REQ_TERRAIN) {
            if (resultCode == RESULT_OK && data != null) {
                Bundle retour = data.getBundleExtra(Keys.EXTRA_BUNDLE);
                if (retour != null) {
                    terrain = retour.getString(Keys.TERRAIN);
                    prixHeure = retour.getInt(Keys.PRIX_HEURE, 0);
                    afficherTerrain();
                }
            } else {
                Toast.makeText(this, R.string.terrain_annule, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == Keys.REQ_CONFIRMATION) {
            if (resultCode == RESULT_OK && data != null) {
                Bundle retour = data.getBundleExtra(Keys.EXTRA_BUNDLE);
                String numero = (retour != null) ? retour.getString(Keys.NUMERO, "") : "";
                tvStatut.setText(getString(R.string.resa_numero, numero));
                tvStatut.setVisibility(View.VISIBLE);
                Toast.makeText(this, R.string.resa_ok, Toast.LENGTH_LONG).show();
            } else {
                tvStatut.setVisibility(View.GONE);
                Toast.makeText(this, R.string.resa_modifier, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ETAT_TERRAIN, terrain);
        outState.putInt(ETAT_PRIX, prixHeure);
    }

    // ----- Cycle de vie de la ProgressBar -----
    @Override
    protected void onResume() {
        super.onResume();
        if (enCours) handler.postDelayed(miseAJour, INTERVALLE_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(miseAJour);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

package com.example.sportbook;

import android.app.Activity;
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
 *
 * TP3 (style de l'annexe Activity1) :
 *   ALLER 3  : bouton « Calculer » → startActivityForResult(CalculPrixActivity, 101)
 *              avec val1 = prix de l'heure, val2 = durée en heures (putExtras)
 *   RETOUR 3 : onActivityResult(101) → "vresult" = prix du créneau (getExtras)
 */
@SuppressWarnings("deprecation") // startActivityForResult est la méthode vue en cours
public class ReservationActivity extends AppCompatActivity {

    private static final int INTERVALLE_MS = 100;
    private static final String ETAT_TERRAIN = "etat_terrain";
    private static final String ETAT_PRIX = "etat_prix";
    private static final String ETAT_PRIX_CALCULE = "etat_prix_calcule";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Bundle recu;

    private TextView tvTerrainChoisi;
    private TextView tvPrixCalcule;
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
    private double prixCalcule = -1;  // TP3 : rempli par le retour de CalculPrixActivity (-1 = pas calculé)
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
        tvPrixCalcule = findViewById(R.id.tvPrixCalcule);
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
            prixCalcule = savedInstanceState.getDouble(ETAT_PRIX_CALCULE, -1);
        }
        afficherTerrain();
        afficherPrixCalcule();

        // Si la durée change, l'ancien prix calculé n'est plus valable
        rgDuree.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                prixCalcule = -1;
                afficherPrixCalcule();
            }
        });

        // ===== TP3 – ALLER 3 : vers CalculPrixActivity (comme Activity1 de l'annexe) =====
        Button btnCalculerPrix = findViewById(R.id.btnCalculerPrix);
        btnCalculerPrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (terrain == null) {
                    Toast.makeText(ReservationActivity.this, R.string.err_terrain, Toast.LENGTH_SHORT).show();
                    return;
                }
                // obtenir les deux valeurs
                Double v1 = (double) prixHeure;            // prix de l'heure
                Double v2 = dureeChoisie() / 60.0;         // durée en heures (1 ; 1,5 ; 2)

                // créer l'Intent pour appeler CalculPrixActivity
                Intent myIntentA1A2 = new Intent(ReservationActivity.this, CalculPrixActivity.class);
                // créer un conteneur pour envoyer les données
                Bundle myData = new Bundle();
                myData.putDouble("val1", v1);
                myData.putDouble("val2", v2);
                // attacher le conteneur à l'Intent
                myIntentA1A2.putExtras(myData);
                // appeler CalculPrixActivity et attendre la réponse (requestCode = 101)
                startActivityForResult(myIntentA1A2, Keys.REQ_CALCUL);
            }
        });

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

    private void afficherPrixCalcule() {
        if (prixCalcule < 0) {
            tvPrixCalcule.setText(R.string.prix_non_calcule);
        } else {
            tvPrixCalcule.setText(getString(R.string.prix_calcule, prixCalcule));
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
                    prixCalcule = -1;          // nouveau terrain → prix à recalculer
                    afficherPrixCalcule();
                }
            } else {
                Toast.makeText(this, R.string.terrain_annule, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == Keys.REQ_CALCUL) {
            // ===== TP3 – RETOUR 3 : comme onActivityResult de l'annexe Activity1 =====
            try {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Bundle myResults = data.getExtras();
                    prixCalcule = myResults.getDouble("vresult");
                    afficherPrixCalcule();
                }
            } catch (Exception e) {
                tvPrixCalcule.setText("Problèmes : " + requestCode + " " + resultCode);
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
        outState.putDouble(ETAT_PRIX_CALCULE, prixCalcule);
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

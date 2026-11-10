package com.example.sportbook;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TP1 – Activité 3 : saisie du créneau + options (CheckBox)
 * et ProgressBar pilotée par un Handler (respect du cycle de vie).
 */
public class ReservationActivity extends AppCompatActivity {

    private static final int INTERVALLE_MS = 100;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ProgressBar progressBar;
    private TextView tvStatut;
    private Button btnReserver;
    private EditText editDate;
    private EditText editHeure;

    private int progression = 0;
    private boolean enCours = false;

    /** Tâche répétée toutes les 100 ms : fait avancer la barre de 5 %. */
    private final Runnable miseAJour = new Runnable() {
        @Override
        public void run() {
            progression += 5;
            progressBar.setProgress(progression);
            tvStatut.setText(getString(R.string.progression, progression));

            if (progression >= 100) {
                enCours = false;
                terminerReservation();
            } else if (enCours) {
                handler.postDelayed(this, INTERVALLE_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        editDate = findViewById(R.id.editDate);
        editHeure = findViewById(R.id.editHeure);
        progressBar = findViewById(R.id.progressResa);
        tvStatut = findViewById(R.id.tvStatut);
        btnReserver = findViewById(R.id.btnReserver);
        Button btnRetour = findViewById(R.id.btnRetour);

        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demarrerReservation();
            }
        });

        // Retour à l'activité précédente (Accueil)
        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void demarrerReservation() {
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

    private void terminerReservation() {
        CheckBox checkMateriel = findViewById(R.id.checkMateriel);
        CheckBox checkEclairage = findViewById(R.id.checkEclairage);

        String resume = getString(R.string.resa_resume,
                editDate.getText().toString().trim(),
                editHeure.getText().toString().trim());
        if (checkMateriel.isChecked()) resume += "\n+ " + getString(R.string.opt_materiel);
        if (checkEclairage.isChecked()) resume += "\n+ " + getString(R.string.opt_eclairage);

        tvStatut.setText(resume);
        btnReserver.setEnabled(true);
        Toast.makeText(this, R.string.resa_ok, Toast.LENGTH_LONG).show();
    }

    // ----- Cycle de vie : arrêter / reprendre la tâche répétée -----
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

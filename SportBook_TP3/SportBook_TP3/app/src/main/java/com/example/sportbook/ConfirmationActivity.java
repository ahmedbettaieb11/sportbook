package com.example.sportbook;

import android.app.Activity;
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
 *
 * TP3 : elle appelle à son tour PartageActivity (style de l'annexe Activity1) :
 *   « Prix par joueur » → startActivityForResult(PartageActivity, 102)
 *   avec val1 = total, val2 = nombre de joueurs → retour "vresult" = prix par joueur
 */
@SuppressWarnings("deprecation") // startActivityForResult est la méthode vue en cours
public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfParJoueur;

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

        // ===== TP3 – ALLER : vers PartageActivity (comme Activity1 de l'annexe) =====
        tvConfParJoueur = findViewById(R.id.tvConfParJoueur);
        final Bundle donneesResa = recu;
        Button btnPartager = findViewById(R.id.btnPartager);
        btnPartager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double v1 = (double) donneesResa.getInt(Keys.TOTAL, 0);    // total
                Double v2 = (double) donneesResa.getInt(Keys.JOUEURS, 1);  // joueurs

                Intent myIntent = new Intent(ConfirmationActivity.this, PartageActivity.class);
                Bundle myData = new Bundle();
                myData.putDouble("val1", v1);
                myData.putDouble("val2", v2);
                myIntent.putExtras(myData);
                startActivityForResult(myIntent, Keys.REQ_PARTAGE);   // requestCode = 102
            }
        });

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

    // ===== TP3 – RETOUR : réponse de PartageActivity =====
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == Keys.REQ_PARTAGE) && (resultCode == Activity.RESULT_OK)) {
                Bundle myResults = data.getExtras();
                Double vresult = myResults.getDouble("vresult");
                tvConfParJoueur.setText(getString(R.string.conf_par_joueur, vresult));
                tvConfParJoueur.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            tvConfParJoueur.setText("Problèmes : " + requestCode + " " + resultCode);
            tvConfParJoueur.setVisibility(View.VISIBLE);
        }
    }
}

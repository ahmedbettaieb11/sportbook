package com.example.sportbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * TP2 – NOUVELLE activité 4 : liste des terrains.
 * Elle est ouverte AVEC startActivityForResult par ReservationActivity,
 * et lui RENVOIE le terrain choisi avec setResult(RESULT_OK, intent) + finish().
 */
public class TerrainsActivity extends AppCompatActivity {

    private static final int[] CARTES = {R.id.carte1, R.id.carte2, R.id.carte3};
    private static final int[] NOMS = {R.string.terrain_1, R.string.terrain_2, R.string.terrain_3};
    private static final int INDEX_EXTERIEUR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terrains);

        Bundle recu = getIntent().getBundleExtra(Keys.EXTRA_BUNDLE);
        if (recu == null) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        int sportCode = recu.getInt(Keys.SPORT_CODE, Keys.SPORT_PADEL);
        boolean couvert = recu.getBoolean(Keys.COUVERT, false);

        TextView tvTitre = findViewById(R.id.tvTitreTerrains);
        TextView tvSousTitre = findViewById(R.id.tvSousTitreTerrains);
        tvTitre.setText(getString(R.string.terrains_titre, recu.getString(Keys.SPORT, "")));
        tvSousTitre.setText(couvert ? R.string.terrains_sous_titre_couvert : R.string.terrains_sous_titre_tous);

        for (int k = 0; k < CARTES.length; k++) {
            View carte = findViewById(CARTES[k]);
            TextView tvNom = carte.findViewById(R.id.tvNomTerrain);
            TextView tvPrix = carte.findViewById(R.id.tvPrixTerrain);
            Button btnChoisir = carte.findViewById(R.id.btnChoisir);

            final String nomTerrain = getString(NOMS[k]);
            final int prix = Keys.prixTerrain(sportCode, k);
            tvNom.setText(nomTerrain);
            tvPrix.setText(getString(R.string.prix_heure, prix));

            // Si l'utilisateur veut un terrain couvert, le terrain extérieur est grisé
            if (couvert && k == INDEX_EXTERIEUR) {
                carte.setAlpha(0.4f);
                btnChoisir.setEnabled(false);
            }

            btnChoisir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renvoyerTerrain(nomTerrain, prix);
                }
            });
        }

        // Annuler : on revient sans résultat
        Button btnAnnuler = findViewById(R.id.btnAnnulerTerrain);
        btnAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /** RETOUR vers ReservationActivity : Bundle dans un Intent « résultat ». */
    private void renvoyerTerrain(String nomTerrain, int prix) {
        Bundle b = new Bundle();
        b.putString(Keys.TERRAIN, nomTerrain);
        b.putInt(Keys.PRIX_HEURE, prix);

        Intent resultat = new Intent();
        resultat.putExtra(Keys.EXTRA_BUNDLE, b);
        setResult(RESULT_OK, resultat);
        finish();   // ferme cet écran → Android appelle onActivityResult chez l'appelant
    }
}

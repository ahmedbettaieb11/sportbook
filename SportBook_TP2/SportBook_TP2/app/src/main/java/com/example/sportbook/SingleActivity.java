package com.example.sportbook;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * DÉFI 1 : la MÊME application avec UNE SEULE activité.
 *
 * - On réutilise exactement les mêmes fichiers XML que la version multi-activités.
 * - Pour « changer d'écran », on appelle setContentView() avec un autre layout.
 * - Toutes les données sont gardées dans UN seul Bundle « donnees »
 *   (sauvegardé dans onSaveInstanceState si l'écran tourne).
 * - Les allers-retours (startActivityForResult) deviennent de simples changements d'écran.
 */
public class SingleActivity extends AppCompatActivity {

    private static final int ECRAN_LOGIN = 0;
    private static final int ECRAN_ACCUEIL = 1;
    private static final int ECRAN_RESA = 2;
    private static final int ECRAN_TERRAINS = 3;
    private static final int ECRAN_CONF = 4;

    private static final String KEY_ECRAN = "ecran";
    private static final String KEY_STATUT = "statut";
    private static final int INTERVALLE_MS = 100;

    private Bundle donnees = new Bundle();
    private int ecran = ECRAN_LOGIN;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable miseAJour;
    private boolean enCours = false;
    private int progression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Bundle sauve = savedInstanceState.getBundle(Keys.EXTRA_BUNDLE);
            if (sauve != null) donnees = sauve;
            ecran = savedInstanceState.getInt(KEY_ECRAN, ECRAN_LOGIN);
        }

        // Bouton « retour » du téléphone : on revient à l'écran précédent
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                arreterProgression();
                switch (ecran) {
                    case ECRAN_TERRAINS:
                    case ECRAN_CONF:
                        afficher(ECRAN_RESA);
                        break;
                    case ECRAN_RESA:
                        afficher(ECRAN_ACCUEIL);
                        break;
                    case ECRAN_ACCUEIL:
                        afficher(ECRAN_LOGIN);
                        break;
                    default:
                        finish();
                }
            }
        });

        afficher(ecran);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ecran == ECRAN_RESA) sauverFormulaire();
        outState.putBundle(Keys.EXTRA_BUNDLE, donnees);
        outState.putInt(KEY_ECRAN, ecran);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Même règle que dans le cours : on arrête la tâche répétée en pause
        if (enCours) {
            arreterProgression();
            afficher(ECRAN_RESA);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void arreterProgression() {
        enCours = false;
        handler.removeCallbacksAndMessages(null);
    }

    /** Change d'écran : c'est l'équivalent de startActivity / finish. */
    private void afficher(int nouvelEcran) {
        ecran = nouvelEcran;
        switch (ecran) {
            case ECRAN_ACCUEIL:  afficherAccueil(); break;
            case ECRAN_RESA:     afficherReservation(); break;
            case ECRAN_TERRAINS: afficherTerrains(); break;
            case ECRAN_CONF:     afficherConfirmation(); break;
            default:             afficherLogin();
        }
    }

    // =================== Écran 1 : Connexion ===================
    private void afficherLogin() {
        setContentView(R.layout.activity_login);
        final EditText editNom = findViewById(R.id.editNom);
        final EditText editEmail = findViewById(R.id.editEmail);
        final CheckBox checkSouvenir = findViewById(R.id.checkSouvenir);
        editNom.setText(donnees.getString(Keys.NOM, ""));
        editEmail.setText(donnees.getString(Keys.EMAIL, ""));
        checkSouvenir.setChecked(donnees.getBoolean(Keys.SOUVENIR, false));

        findViewById(R.id.btnConnexion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                donnees.putString(Keys.NOM, nom);
                donnees.putString(Keys.EMAIL, email);
                donnees.putBoolean(Keys.SOUVENIR, checkSouvenir.isChecked());
                afficher(ECRAN_ACCUEIL);
            }
        });
    }

    // =================== Écran 2 : Accueil ===================
    private void afficherAccueil() {
        setContentView(R.layout.activity_accueil);
        ((TextView) findViewById(R.id.tvBienvenue)).setText(
                getString(R.string.accueil_bonjour, donnees.getString(Keys.NOM, "")));
        ((TextView) findViewById(R.id.tvSousTitre)).setText(donnees.getString(Keys.EMAIL, ""));

        final RadioGroup rgSport = findViewById(R.id.rgSport);
        final Switch switchCouvert = findViewById(R.id.switchCouvert);
        final SeekBar seekJoueurs = findViewById(R.id.seekJoueurs);
        final TextView tvJoueurs = findViewById(R.id.tvJoueurs);

        // Restaurer les choix précédents
        int code = donnees.getInt(Keys.SPORT_CODE, Keys.SPORT_PADEL);
        rgSport.check(code == Keys.SPORT_BASKET ? R.id.radioBasket
                : code == Keys.SPORT_FOOT ? R.id.radioFoot : R.id.radioPadel);
        switchCouvert.setChecked(donnees.getBoolean(Keys.COUVERT, false));
        if (donnees.containsKey(Keys.JOUEURS)) {
            seekJoueurs.setProgress(donnees.getInt(Keys.JOUEURS) - Keys.JOUEURS_MIN);
        }
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

        findViewById(R.id.btnContinuer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = rgSport.getCheckedRadioButtonId();
                int nouveauCode = (id == R.id.radioBasket) ? Keys.SPORT_BASKET
                        : (id == R.id.radioFoot) ? Keys.SPORT_FOOT : Keys.SPORT_PADEL;
                int libelle = (nouveauCode == Keys.SPORT_BASKET) ? R.string.sport_basket
                        : (nouveauCode == Keys.SPORT_FOOT) ? R.string.sport_foot : R.string.sport_padel;

                // Si le sport ou le choix « couvert » change, le terrain choisi n'est plus valable
                if (nouveauCode != donnees.getInt(Keys.SPORT_CODE, -1)
                        || switchCouvert.isChecked() != donnees.getBoolean(Keys.COUVERT, false)) {
                    donnees.remove(Keys.TERRAIN);
                    donnees.remove(Keys.PRIX_HEURE);
                }
                donnees.putInt(Keys.SPORT_CODE, nouveauCode);
                donnees.putString(Keys.SPORT, getString(libelle));
                donnees.putBoolean(Keys.COUVERT, switchCouvert.isChecked());
                donnees.putInt(Keys.JOUEURS, seekJoueurs.getProgress() + Keys.JOUEURS_MIN);
                donnees.remove(KEY_STATUT);
                afficher(ECRAN_RESA);
            }
        });

        findViewById(R.id.btnDeconnexion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afficher(ECRAN_LOGIN);
            }
        });
    }

    // =================== Écran 3 : Réservation ===================
    private void afficherReservation() {
        setContentView(R.layout.activity_reservation);
        ((TextView) findViewById(R.id.tvRecap)).setText(getString(R.string.resa_recap,
                donnees.getString(Keys.SPORT, ""), donnees.getInt(Keys.JOUEURS, Keys.JOUEURS_MIN)));

        final TextView tvTerrain = findViewById(R.id.tvTerrainChoisi);
        final EditText editDate = findViewById(R.id.editDate);
        final EditText editHeure = findViewById(R.id.editHeure);
        final RadioGroup rgDuree = findViewById(R.id.rgDuree);
        final CheckBox checkMateriel = findViewById(R.id.checkMateriel);
        final CheckBox checkEclairage = findViewById(R.id.checkEclairage);
        final ProgressBar progressBar = findViewById(R.id.progressResa);
        final TextView tvStatut = findViewById(R.id.tvStatut);
        final Button btnReserver = findViewById(R.id.btnReserver);

        // Restaurer le formulaire (setContentView recrée des vues vides)
        String terrain = donnees.getString(Keys.TERRAIN);
        tvTerrain.setText(terrain == null ? getString(R.string.aucun_terrain)
                : getString(R.string.terrain_choisi, terrain, donnees.getInt(Keys.PRIX_HEURE)));
        editDate.setText(donnees.getString(Keys.DATE, ""));
        editHeure.setText(donnees.getString(Keys.HEURE, ""));
        int duree = donnees.getInt(Keys.DUREE, 60);
        rgDuree.check(duree == 90 ? R.id.radio90 : duree == 120 ? R.id.radio120 : R.id.radio60);
        checkMateriel.setChecked(donnees.getBoolean(Keys.MATERIEL, false));
        checkEclairage.setChecked(donnees.getBoolean(Keys.ECLAIRAGE, false));
        if (donnees.containsKey(KEY_STATUT)) {
            tvStatut.setText(donnees.getString(KEY_STATUT));
            tvStatut.setVisibility(View.VISIBLE);
        }

        // « Choisir » : au lieu de startActivityForResult, on change d'écran
        findViewById(R.id.btnChoisirTerrain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauverFormulaire();
                afficher(ECRAN_TERRAINS);
            }
        });

        findViewById(R.id.btnRetour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauverFormulaire();
                afficher(ECRAN_ACCUEIL);
            }
        });

        btnReserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (donnees.getString(Keys.TERRAIN) == null) {
                    Toast.makeText(SingleActivity.this, R.string.err_terrain, Toast.LENGTH_SHORT).show();
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
                sauverFormulaire();

                progression = 0;
                enCours = true;
                btnReserver.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                tvStatut.setVisibility(View.VISIBLE);
                miseAJour = new Runnable() {
                    @Override
                    public void run() {
                        progression += 5;
                        progressBar.setProgress(progression);
                        tvStatut.setText(getString(R.string.progression, progression));
                        if (progression >= 100) {
                            enCours = false;
                            afficher(ECRAN_CONF);
                        } else if (enCours) {
                            handler.postDelayed(this, INTERVALLE_MS);
                        }
                    }
                };
                handler.postDelayed(miseAJour, INTERVALLE_MS);
            }
        });
    }

    /** Copie le contenu du formulaire de réservation dans le Bundle. */
    private void sauverFormulaire() {
        EditText editDate = findViewById(R.id.editDate);
        if (editDate == null) return;     // on n'est pas sur l'écran Réservation
        EditText editHeure = findViewById(R.id.editHeure);
        RadioGroup rgDuree = findViewById(R.id.rgDuree);
        CheckBox checkMateriel = findViewById(R.id.checkMateriel);
        CheckBox checkEclairage = findViewById(R.id.checkEclairage);

        int id = rgDuree.getCheckedRadioButtonId();
        int duree = (id == R.id.radio90) ? 90 : (id == R.id.radio120) ? 120 : 60;
        donnees.putString(Keys.DATE, editDate.getText().toString().trim());
        donnees.putString(Keys.HEURE, editHeure.getText().toString().trim());
        donnees.putInt(Keys.DUREE, duree);
        donnees.putBoolean(Keys.MATERIEL, checkMateriel.isChecked());
        donnees.putBoolean(Keys.ECLAIRAGE, checkEclairage.isChecked());
        donnees.putInt(Keys.TOTAL, Keys.calculerTotal(donnees.getInt(Keys.PRIX_HEURE, 0),
                duree, checkMateriel.isChecked(), checkEclairage.isChecked()));
    }

    // =================== Écran 4 : Terrains ===================
    private void afficherTerrains() {
        setContentView(R.layout.activity_terrains);
        int code = donnees.getInt(Keys.SPORT_CODE, Keys.SPORT_PADEL);
        boolean couvert = donnees.getBoolean(Keys.COUVERT, false);
        ((TextView) findViewById(R.id.tvTitreTerrains)).setText(
                getString(R.string.terrains_titre, donnees.getString(Keys.SPORT, "")));
        ((TextView) findViewById(R.id.tvSousTitreTerrains)).setText(
                couvert ? R.string.terrains_sous_titre_couvert : R.string.terrains_sous_titre_tous);

        int[] cartes = {R.id.carte1, R.id.carte2, R.id.carte3};
        int[] noms = {R.string.terrain_1, R.string.terrain_2, R.string.terrain_3};
        for (int k = 0; k < cartes.length; k++) {
            View carte = findViewById(cartes[k]);
            final String nomTerrain = getString(noms[k]);
            final int prix = Keys.prixTerrain(code, k);
            ((TextView) carte.findViewById(R.id.tvNomTerrain)).setText(nomTerrain);
            ((TextView) carte.findViewById(R.id.tvPrixTerrain)).setText(getString(R.string.prix_heure, prix));
            Button btnChoisir = carte.findViewById(R.id.btnChoisir);
            if (couvert && k == 1) {
                carte.setAlpha(0.4f);
                btnChoisir.setEnabled(false);
            }
            btnChoisir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Équivalent de setResult(RESULT_OK) : on garde le choix et on revient
                    donnees.putString(Keys.TERRAIN, nomTerrain);
                    donnees.putInt(Keys.PRIX_HEURE, prix);
                    afficher(ECRAN_RESA);
                }
            });
        }

        findViewById(R.id.btnAnnulerTerrain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SingleActivity.this, R.string.terrain_annule, Toast.LENGTH_SHORT).show();
                afficher(ECRAN_RESA);
            }
        });
    }

    // =================== Écran 5 : Confirmation ===================
    private void afficherConfirmation() {
        setContentView(R.layout.activity_confirmation);

        String options = "";
        if (donnees.getBoolean(Keys.MATERIEL)) options += getString(R.string.opt_materiel);
        if (donnees.getBoolean(Keys.ECLAIRAGE)) {
            options += (options.isEmpty() ? "" : ", ") + getString(R.string.opt_eclairage);
        }
        if (options.isEmpty()) options = getString(R.string.conf_aucune);

        ((TextView) findViewById(R.id.tvConfNom)).setText(getString(R.string.conf_nom, donnees.getString(Keys.NOM, "")));
        ((TextView) findViewById(R.id.tvConfEmail)).setText(getString(R.string.conf_email, donnees.getString(Keys.EMAIL, "")));
        ((TextView) findViewById(R.id.tvConfSport)).setText(getString(R.string.conf_sport, donnees.getString(Keys.SPORT, "")));
        ((TextView) findViewById(R.id.tvConfJoueurs)).setText(getString(R.string.conf_joueurs, donnees.getInt(Keys.JOUEURS, 0)));
        ((TextView) findViewById(R.id.tvConfTerrain)).setText(getString(R.string.conf_terrain, donnees.getString(Keys.TERRAIN, "")));
        ((TextView) findViewById(R.id.tvConfCreneau)).setText(getString(R.string.conf_creneau,
                donnees.getString(Keys.DATE, ""), donnees.getString(Keys.HEURE, "")));
        ((TextView) findViewById(R.id.tvConfDuree)).setText(getString(R.string.conf_duree, donnees.getInt(Keys.DUREE, 0)));
        ((TextView) findViewById(R.id.tvConfOptions)).setText(getString(R.string.conf_options, options));
        ((TextView) findViewById(R.id.tvConfTotal)).setText(getString(R.string.conf_total, donnees.getInt(Keys.TOTAL, 0)));

        findViewById(R.id.btnConfirmer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numero = "SB-" + (1000 + new Random().nextInt(9000));
                donnees.putString(KEY_STATUT, getString(R.string.resa_numero, numero));
                Toast.makeText(SingleActivity.this, R.string.resa_ok, Toast.LENGTH_LONG).show();
                afficher(ECRAN_RESA);
            }
        });

        findViewById(R.id.btnModifier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donnees.remove(KEY_STATUT);
                Toast.makeText(SingleActivity.this, R.string.resa_modifier, Toast.LENGTH_SHORT).show();
                afficher(ECRAN_RESA);
            }
        });
    }
}

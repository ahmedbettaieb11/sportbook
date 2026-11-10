package com.example.sportbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * TP3 – NOUVELLE activité 6 : calcul du prix du créneau.
 * Construite exactement comme l'annexe « Activity2 » :
 *   - reçoit deux valeurs (val1 = prix de l'heure, val2 = durée en heures) avec getExtras()
 *   - calcule le résultat (val1 × val2)
 *   - l'ajoute au Bundle sous la clé "vresult"
 *   - renvoie le tout avec setResult(RESULT_OK, intent)
 *   - le bouton « Terminé » ferme l'écran avec finish()
 * Elle est appelée par ReservationActivity avec startActivityForResult(intent, 101).
 */
public class CalculPrixActivity extends Activity implements OnClickListener {

    EditText dataReceived;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcul_prix);

        dataReceived = (EditText) findViewById(R.id.etDataReceived_calcul);
        btnDone = (Button) findViewById(R.id.btnDone_calcul);
        btnDone.setOnClickListener(this);

        // récupérer l'Intent qui a lancé cette activité
        Intent myLocalIntent = getIntent();
        // récupérer le Bundle envoyé par ReservationActivity
        Bundle myBundle = myLocalIntent.getExtras();
        if (myBundle == null) {               // bonne pratique : vérifier le Bundle
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }
        Double v1 = myBundle.getDouble("val1");   // prix de l'heure
        Double v2 = myBundle.getDouble("val2");   // durée en heures

        // opérer sur les données reçues
        Double vResult = v1 * v2;

        // afficher les données reçues et le résultat
        dataReceived.setText(getString(R.string.calcul_donnees, v1, v2, vResult));

        // ajouter le résultat au Bundle
        myBundle.putDouble("vresult", vResult);
        // rattacher le Bundle mis à jour à l'Intent
        myLocalIntent.putExtras(myBundle);
        // renvoyer le signal OK à l'activité appelante
        setResult(Activity.RESULT_OK, myLocalIntent);
    }

    @Override
    public void onClick(View v) {
        // fermer l'écran → Android appelle onActivityResult chez l'appelant
        finish();
    }
}

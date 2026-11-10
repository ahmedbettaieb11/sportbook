package com.example.sportbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * TP3 – NOUVELLE activité 7 : partage du prix entre les joueurs.
 * Même structure que l'annexe « Activity2 » :
 *   val1 = total de la réservation, val2 = nombre de joueurs
 *   "vresult" = val1 ÷ val2 (prix payé par chaque joueur)
 * Elle est appelée par ConfirmationActivity avec startActivityForResult(intent, 102).
 */
public class PartageActivity extends Activity implements OnClickListener {

    EditText dataReceived;
    Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partage);

        dataReceived = (EditText) findViewById(R.id.etDataReceived_partage);
        btnDone = (Button) findViewById(R.id.btnDone_partage);
        btnDone.setOnClickListener(this);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();
        if (myBundle == null) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }
        Double v1 = myBundle.getDouble("val1");   // total
        Double v2 = myBundle.getDouble("val2");   // nombre de joueurs

        // éviter la division par zéro
        Double vResult = (v2 > 0) ? v1 / v2 : v1;

        dataReceived.setText(getString(R.string.partage_donnees, v1, v2, vResult));

        myBundle.putDouble("vresult", vResult);
        myLocalIntent.putExtras(myBundle);
        setResult(Activity.RESULT_OK, myLocalIntent);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}

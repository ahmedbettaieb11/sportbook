package com.example.sportbook;

/**
 * TP2 – Constantes partagées par toutes les activités.
 * Bonne pratique : les clés du Bundle sont des "public static final String"
 * pour éviter les fautes de frappe.
 */
public final class Keys {

    private Keys() { }

    /** Nom de l'extra qui contient le Bundle dans chaque Intent. */
    public static final String EXTRA_BUNDLE = "data_bundle";

    // Clés du Bundle
    public static final String NOM = "nom";
    public static final String EMAIL = "email";
    public static final String SOUVENIR = "souvenir";
    public static final String SPORT = "sport";            // libellé affiché
    public static final String SPORT_CODE = "sport_code";  // 0 padel, 1 basket, 2 foot
    public static final String COUVERT = "couvert";
    public static final String JOUEURS = "joueurs";
    public static final String TERRAIN = "terrain";
    public static final String PRIX_HEURE = "prix_heure";
    public static final String DATE = "date";
    public static final String HEURE = "heure";
    public static final String DUREE = "duree";            // en minutes
    public static final String MATERIEL = "materiel";
    public static final String ECLAIRAGE = "eclairage";
    public static final String TOTAL = "total";
    public static final String NUMERO = "numero";          // renvoyé par Confirmation

    // Codes de requête pour startActivityForResult
    public static final int REQ_TERRAIN = 1;
    public static final int REQ_CONFIRMATION = 2;

    // Données métier
    public static final int SPORT_PADEL = 0;
    public static final int SPORT_BASKET = 1;
    public static final int SPORT_FOOT = 2;
    public static final int JOUEURS_MIN = 2;
    /** Prix de base (DT/heure) par sport : padel, basket, foot. */
    public static final int[] PRIX_BASE = {40, 30, 60};
    /** Coefficient  par terrain : intérieur, extérieur, premium. */
    public static final int[] COEF_TERRAIN = {12, 10, 15};
    public static final int PRIX_MATERIEL = 5;
    public static final int PRIX_ECLAIRAGE = 3;

    /** Prix d'un terrain pour un sport donné. */
    public static int prixTerrain(int sportCode, int indexTerrain) {
        if (sportCode < 0 || sportCode >= PRIX_BASE.length) sportCode = SPORT_PADEL;
        return PRIX_BASE[sportCode] * COEF_TERRAIN[indexTerrain] / 10;
    }

    /** Total = prix de l'heure × durée + options. */
    public static int calculerTotal(int prixHeure, int dureeMin, boolean materiel, boolean eclairage) {
        int total = prixHeure * dureeMin / 60;
        if (materiel) total += PRIX_MATERIEL;
        if (eclairage) total += PRIX_ECLAIRAGE;
        return total;
    }
}

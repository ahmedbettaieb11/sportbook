# SportBook – TP2

## Travail à faire
- 3 activités améliorées : Login, Accueil, Réservation (données transmises par Bundle).
- 2 nouvelles activités : TerrainsActivity et ConfirmationActivity,
  ouvertes avec startActivityForResult et qui renvoient un résultat (setResult + finish).

Allers-retours :
  Réservation --(Bundle: sport, couvert)--> Terrains --(RESULT_OK: terrain, prix)--> Réservation
  Réservation --(Bundle: tout)--> Confirmation --(RESULT_OK: n° / RESULT_CANCELED)--> Réservation

## Défi 1 – une seule activité
SingleActivity.java : mêmes XML, changement d'écran avec setContentView(). Icône « SportBook (1 écran) ».

## Défi 2 – multilingue
res/values/strings.xml (français) + res/values-en/strings.xml (anglais).
Tester : Paramètres du téléphone → Langue → English.

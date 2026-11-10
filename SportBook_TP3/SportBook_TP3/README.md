# SportBook – TP3

Base : le TP2 (5 activités). On ajoute 2 activités construites comme les annexes
Activity1 / Activity2 (putExtras, getExtras, "val1", "val2", "vresult", setResult, finish).

## Les 2 nouvelles activités
- CalculPrixActivity (requestCode 101) – appelée par ReservationActivity, bouton « Calculer »
  val1 = prix de l'heure, val2 = durée (h)  →  vresult = prix du créneau
- PartageActivity (requestCode 102) – appelée par ConfirmationActivity, bouton « Prix par joueur »
  val1 = total, val2 = nombre de joueurs  →  vresult = prix par joueur

## Défi 1 – une seule activité
SingleActivity.java : 7 écrans (setContentView), dont Calcul et Partage.

## Défi 2 – multilingue
res/values/strings.xml (FR) + res/values-en/strings.xml (EN), textes du TP3 inclus.

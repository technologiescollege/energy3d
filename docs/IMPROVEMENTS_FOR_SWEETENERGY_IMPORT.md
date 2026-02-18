# Améliorations côté Energy3D pour l'import SweetEnergy (Sweet Home 3D → .ng3)

Ce document liste les améliorations possibles **côté Energy3D** pour mieux supporter les fichiers `.ng3` exportés par le plugin SweetEnergy (conversion Sweet Home 3D → Energy3D).

---

## 1. **Chargement .ng3 sans image de sol (critique)**

**Constat**  
Le plugin SweetEnergy sérialise une `Scene` sans image de sol (`groundImage` est `transient` et non écrit). Dans `Scene.readObject()`, après `defaultReadObject()`, le code appelle systématiquement `groundImage = ImageIO.read(in)`. Si aucun octet d’image n’a été écrit, `ImageIO.read(in)` peut renvoyer `null` ou lever une exception selon le flux, ce qui peut faire échouer l’ouverture ou l’import d’un .ng3 exporté par le plugin.

**Amélioration**  
Rendre la désérialisation robuste quand aucune image n’est présente : ne lire l’image que si des données sont disponibles, ou entourer l’appel d’un try/catch et laisser `groundImage = null`.

**Fichier** : `org.concord.energy3d.scene.Scene.java` – méthode `readObject`.

---

## 2. **Documentation / contrat du format .ng3**

**Constat**  
Le format .ng3 est la sérialisation Java (`ObjectOutputStream.writeObject(Scene)`). Le plugin doit recréer une `Scene` + `Foundation` + murs via réflexion et précharger de nombreuses classes (Ardor3D, Energy3D). Toute évolution des champs sérialisés ou des classes peut casser la compatibilité.

**Amélioration**  
- Documenter (dans le wiki ou un fichier `docs/NG3_FORMAT.md`) :
  - liste des champs sérialisés de `Scene` (et des classes importantes comme `Foundation`, `Wall`) ;
  - comportement de `writeObject` / `readObject` (notamment l’image de sol optionnelle).
- Si possible, utiliser un **numéro de version** (ex. `serialVersionUID` ou un champ explicite dans le flux) et, en lecture, gérer les anciennes versions (champs manquants = valeurs par défaut).

Cela aide le plugin à rester aligné et à gérer plusieurs versions d’Energy3D.

---

## 3. **API « headless » ou librairie pour la création de scènes**

**Constat**  
Le plugin charge Energy3D via un `ClassLoader` séparé et utilise la réflexion pour :
- instancier `Scene`, `Foundation`, `Wall`, etc. ;
- précharger RenderState, OffsetState, MaterialState, etc.

C’est fragile (dépendances de classes, ordre de chargement).

**Amélioration**  
Exposer une API minimale « export / création de scène » utilisable sans UI, par exemple :
- un module ou JAR « energy3d-core » ou « energy3d-export » avec peu de dépendances (pas de `MainFrame`, `EnergyPanel`, etc.) ;
- ou des classes/factory dédiées (ex. `SceneBuilder` ou `SceneExportHelper`) qui permettent de construire une `Scene` (fondation + murs + fenêtres) et de la sérialiser en .ng3.

Le plugin pourrait alors appeler cette API au lieu de tout recréer par réflexion.

---

## 4. **Valeurs par défaut après chargement**

**Constat**  
À l’ouverture, Energy3D appelle `instance.cleanup()` puis `loadCameraLocation()`. Si le fichier exporté par le plugin n’a pas de caméra valide (`cameraLocation` / `cameraDirection`), `loadCameraLocation()` doit gérer les valeurs nulles (vue par défaut).

**Amélioration**  
Vérifier que `loadCameraLocation()` et l’initialisation de la vue gèrent correctement les champs optionnels (caméra, échelle du sol, etc.) lorsqu’ils sont absents ou nuls, pour éviter des vues vides ou des NPE au premier affichage d’un .ng3 SweetEnergy.

---

## 5. **Message ou marquage « source : Sweet Home 3D »**

**Constat**  
L’utilisateur peut ne pas savoir qu’un fichier vient de Sweet Home 3D. Certaines limitations (pas d’image de sol, pas de toit si le niveau « Toit-gen » est absent, etc.) peuvent prêter à confusion.

**Amélioration**  
- Soit le plugin écrit un champ ou une métadonnée dans la scène (ex. `sceneSource = "SweetHome3D"`) ;
- soit Energy3D détecte certains signes (ex. absence d’image de sol + certains champs vides) et affiche un message du type : « Ce fichier a été importé depuis Sweet Home 3D. Certaines options (image de sol, etc.) peuvent ne pas être disponibles. »

Optionnel mais utile pour le diagnostic et l’UX.

---

## 6. **Résumé des priorités**

| Priorité | Amélioration | Impact |
|----------|--------------|--------|
| **Haute** | Lecture robuste de l’image de sol dans `readObject()` | Évite les échecs à l’ouverture/import des .ng3 SweetEnergy |
| **Moyenne** | Documentation du format .ng3 et gestion de version | Stabilité à long terme du plugin et compatibilité ascendante |
| **Moyenne** | Valeurs par défaut (caméra, échelle) après chargement | Meilleure UX à l’ouverture d’un .ng3 exporté |
| **Basse** | API headless / SceneBuilder | Simplification et robustesse du plugin |
| **Basse** | Marquage ou message « source Sweet Home 3D » | Clarté pour l’utilisateur |

La modification proposée en **§ 1** est implémentée dans le dépôt (résilience de `Scene.readObject()` lorsque aucune image de sol n’est présente).

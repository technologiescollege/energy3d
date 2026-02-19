# Energy3D

![Energy3D](https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEj3iAVXaknH2WXaAKELD1OvGsIyV-CDF3hqFY051tsQrZ4_edXSLmha-D0aNNMxT87rXb-Wfx0SPCXZFn4dWx7fw3-4gzx8Eh7wWS3u5lZ5jDmxHrTxbSEd73b17TlZHQKRqOIMgBgb45E/s1600/Untitled-1.png)

**[English](#english)** · **[Français](#français)**

---

<a id="english"></a>

## English

### Learning to Build a Sustainable World

Energy3D is a **simulation-based engineering tool** for designing green buildings and power stations that harness renewable energy to achieve sustainable development. It is [NSF-funded free software](https://energy.concord.org/energy3d/) from the [Concord Consortium](https://concord.org/).

#### What you can do

- **Sketch** a realistic structure or **import** one from an existing CAD file
- **Superimpose** your design on a map (e.g. Google Maps or lot maps)
- **Evaluate** energy performance for any given day and location
- Get **time graphs** (like data loggers) and **heat maps** (like infrared cameras) from computational physics and weather data
- Use **AI** for generative design, optimization, and automatic assessment
- **Print** and cut out pieces to assemble a **physical scale model**

![Village Energy3D](https://energy.concord.org/energy3d/_assets/img/village01.jpg)

Energy3D is aimed at science and engineering education from middle school to graduate level. Because its simulations are accurate and its interface is user-friendly, it can also serve as an entry-level energy simulation tool for professionals.

#### Enhancements in this version (S.Canet)

- **Multilingual**: add your locale file in `app\locales` to support a new language.
- **Optimized import from SweetHome3D**: use the SweetEnergy3D plugin to export designs from [SweetHome3D](http://www.sweethome3d.com/) into Energy3D.
- **Interior walls**: support for adding and editing interior walls.
- **Textures**: support for applying textures to surfaces.

#### Requirements

- **512 MB memory** minimum
- Java (JRE 8 or compatible) for running; JDK 8 for building

#### How to run (developers)

- **Eclipse**: add JVM run arguments, e.g.  
  - Windows: `-Djava.library.path=./exe/lib/jogl/native/windows-32 -DrunInEclipse=true`  
  - Mac: `-Djava.library.path=./exe/lib/jogl/native/mac-universal -DrunInEclipse=true`  
  - Linux: `-Djava.library.path=./exe/lib/jogl/native/linux-64 -DrunInEclipse=true`
- **Portable Windows build**: run `build-portable.bat` (requires JDK 8 and [Launch4j](https://launch4j.sourceforge.net/)); output is in `dist\Energy3D_portable\`.

More setup details (JOGL, Ardor3D) are in `readme.txt`.

#### Citation

> Charles Xie, Corey Schimpf, Jie Chao, Saeid Nourian, and Joyce Massicotte, *Learning and Teaching Engineering Design through Modeling and Simulation on a CAD Platform*, Computer Applications in Engineering Education, 26(4), pp. 824-840, 2018 ([DOI: 10.1002/cae.21920](https://doi.org/10.1002/cae.21920))

#### Links

- Official site: [energy.concord.org/energy3d](https://energy.concord.org/energy3d/)
- Tutorial, showcase, downloads (Windows / Mac / Linux): [energy.concord.org/energy3d](https://energy.concord.org/energy3d/)

---

<a id="français"></a>

## Français

### Apprendre à construire un monde durable

Energy3D est un **outil d’ingénierie basé sur la simulation** pour concevoir des bâtiments verts et des centrales qui exploitent les énergies renouvelables dans une optique de développement durable. C’est un **logiciel libre** financé par la NSF, développé par le [Concord Consortium](https://concord.org/).

#### Ce que vous pouvez faire

- **Esquisser** une structure réaliste ou **importer** un fichier CAO existant
- **Superposer** votre conception sur une carte (Google Maps, plans de lot, etc.)
- **Évaluer** les performances énergétiques pour un jour et un lieu donnés
- Obtenir des **graphiques temporels** (type enregistreurs) et des **cartographies thermiques** (type caméra infrarouge) à partir de la physique de calcul et de données météo
- Utiliser l’**IA** pour la conception générative, l’optimisation et l’évaluation automatique
- **Imprimer** et découper les pièces pour assembler une **maquette physique à l’échelle**

![Village Energy3D](https://energy.concord.org/energy3d/_assets/img/village01.jpg)

Energy3D vise l’enseignement des sciences et de l’ingénierie, du collège au niveau universitaire. La précision des simulations et l’interface conviviale en font aussi un outil de simulation énergétique accessible aux professionnels.

#### Améliorations de cette version (S.Canet)

- **Multilingue** : il suffit d’ajouter son fichier de langue dans `app\locales` pour prendre en charge une nouvelle langue.
- **Import optimisé depuis SweetHome3D** : utilisation du plugin SweetEnergy3D pour exporter des projets depuis [SweetHome3D](http://www.sweethome3d.com/) dans Energy3D.
- **Murs intérieurs** : prise en charge de l’ajout et de l’édition des murs intérieurs.
- **Textures** : prise en charge de l’application de textures sur les surfaces.

#### Configuration requise

- **512 Mo de mémoire** minimum
- Java (JRE 8 ou compatible) pour l’exécution ; JDK 8 pour la compilation

#### Lancer le projet (développeurs)

- **Eclipse** : ajouter les arguments JVM, par ex.  
  - Windows : `-Djava.library.path=./exe/lib/jogl/native/windows-32 -DrunInEclipse=true`  
  - Mac : `-Djava.library.path=./exe/lib/jogl/native/mac-universal -DrunInEclipse=true`  
  - Linux : `-Djava.library.path=./exe/lib/jogl/native/linux-64 -DrunInEclipse=true`
- **Build portable Windows** : exécuter `build-portable.bat` (JDK 8 et [Launch4j](https://launch4j.sourceforge.net/) requis) ; le résultat est dans `dist\Energy3D_portable\`.

Plus de détails (JOGL, Ardor3D) sont dans `readme.txt`.

#### Citation

> Charles Xie, Corey Schimpf, Jie Chao, Saeid Nourian, et Joyce Massicotte, *Learning and Teaching Engineering Design through Modeling and Simulation on a CAD Platform*, Computer Applications in Engineering Education, 26(4), p. 824-840, 2018 ([DOI: 10.1002/cae.21920](https://doi.org/10.1002/cae.21920))

#### Liens

- Site officiel : [energy.concord.org/energy3d](https://energy.concord.org/energy3d/)
- Tutoriel, vitrine, téléchargements (Windows / Mac / Linux) : [energy.concord.org/energy3d](https://energy.concord.org/energy3d/)

---

*Energy3D © 2011– The Concord Consortium. Développement soutenu par la National Science Foundation et General Motors.*

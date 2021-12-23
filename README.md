## README - Description du jeu ICWars
ICWars est un jeu de stratégie de type "Advance Wars" en 2D, articulé autour de grilles de jeu. 

Il contient deux niveaux, les niveaux 0 et 1, et est prévu pour pouvoir jouer à 1 ou 2 joueurs : il est possible de jouer contre une IA ou contre un autre vrai joueur sur le même ordinateur.

## Règles du jeu
Le but du jeu est de détruire les unités adverses à l'aide de ses propres unités. Pour ce faire, à chaque tour chacun des joueurs peut déplacer chacune de ses unités dans un rayon de déplacement propre à chaque unité, puis lui faire effectuer une action au choix : attaquer ou attendre. Dans le cas d'une attaque, il est possible de sélectionner une cible ennemie dans son rayon d'attaque, et de lui infliger les dégâts correspondant à la puissance de feu de l'unité. 

## Grille de jeu
Les grilles de jeu comportent 4 types de terrain : la route, l'eau, l'herbe et les montagnes. Chacun de ces types de terrains possède un nombre d'étoiles de défense qui lui est spécifique, et permettant aux unités présentes de subir moins de dégâts en cas d'attaque. De plus, les rivières ne permettent pas aux unités de stationner, elle font uniquement figure de décor. Le deuxième niveau de jeu contient une fonctionnalité supplémentaire : les cités. Ces dernières peuvent être capturées par les tanks en se positionnant dessus et s'avèrent très utiles : pour chaque cité capturée, à chaque nouveau tour toutes les unités de la faction conquérante gagnent un point de vie. Les cités peuvent être libérées par l'adversaire et rendues neutres grâce à une frappe d'artilleur, ou reprises par l'adversaire qui la capture à son tour.

## Unités
Il y a trois types d'unités : les soldats, les tanks et les artilleurs, chacun d'eux ayant une faction : alliée ou ennemie. Les tanks ont une puissance de feu et des points de vie supérieurs aux soldats et aux artilleurs, tandis que les artilleurs ont la faculté de provoquer des dégâts de zone en attaquant : ils peuvent attaquer plusieurs unités à la fois si elles se trouvent à proximité. Attention cependant : les roquettes touchent aussi les unités alliées !

## Lancement du jeu
Il suffit de lancer la fonction main() du fichier Play.java à la racine du projet afin de lancer le jeu : une fenêtre s'ouvre automatiquement.
Sinon, ouvrir un terminal puis se placer dans le dossier du projet.
Taper ensuite les commandes suivantes :

    cd java/ch/epfl/cs107/play
    javac Play.java
    java Play

Pour avoir une musique de fond, il suffira de placer le fichier backgroundmusic.wav fourni dans le dossier res/music et de décommenter le fichier AudioFilePlayer.java
## Déroulement du jeu
Au lancement du jeu, un panneau permet de sélectionner l'adversaire désiré : une IA ou un vrai joueur. S'ensuit le déroulement de première manche dans la première grille de jeu. Lorsque le premier des deux joueurs voit toutes ses unités détruites (n'ayant plus de points de vie), la deuxième manche se lance. Il est à nouveau possible de sélectionner son adversaire, puis une deuxième manche se déroule dans la deuxième grille de jeu. Lorsque le premier des deux joueurs est éliminé, un panneau Game Over s'affiche, offrant le choix de recommencer une partie ou de quitter le jeu.

## Commandes 
|Touche|Action|
|R| Choisir un adversaire réel |
|A| Choisir un adversaire IA / Choisir d'attaquer |
|W| Choisir d'attendre |
|C| Capturer une cité |
|S| Reset le jeu |
|N| Forcer le passage au niveau suivant |
|Q| Quitter le jeu (lors du Game Over) |
|G| Relancer le jeu (lors du Game Over) |
|ENTER| Sélectionner l'unité à déplacer / Confirmer sa destination / Confirmer la cible à attaquer |
|TAB| Annuler la sélection d'une unité / Passer au joueur suivant |
|Touches directionnelles| Déplacer le curseur et ses unités / sélectionner la cible à attaquer |







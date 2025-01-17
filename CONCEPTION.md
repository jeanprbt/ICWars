﻿# CONCEPTION - Structure du jeu

Nous avons codé la partie obligatoire du projet en suivant les consignes et en ne modifiant pas l'architecture de la maquette proposée.

Nous avons réalisé toutes les étapes et avons ajouté quelques fonctionnalités supplémentaires.

## Partie obligatoire

La partie la plus libre du projet concernait le **AIPlayer**, et notamment son algorithme de positionnement des unités.

Nous avons fait le choix de ne pas utiliser la méthode `waitFor()` proposée par l'énoncé, et de plutôt implémenter la nôtre qui nous donnait plus de liberté quant au temps d'attente entre les étapes du AIPlayer ; elle fonctionne en appelant la méthode `sleep()` du programme pendant un temps en millisecondes donné en paramètre.

Pour faire notre algorithme de positionnement,  nous avons procédé comme suit : lors du passage dans l'automate à états finis à l'état `MOVE_UNIT`, nous appelons la méthode `getClosestPositionPossible()`. Cette dernière renvoie des `DiscreteCoordinates` correspondant à la destination optimale pour le déplacement de l'unité sélectionnée par le AIPlayer, soit la position la plus proche de la cible visée, mais comprise dans le rayon de déplacement de l'unité sélectionnée.

Pour ce faire, la méthode vérifie premièrement que l'unité sélectionnée n'est pas déjà correctement positionnée (sur l'une des cellules voisines de la cible). Si ce n'est pas le cas, elle sépare ensuite les calculs en deux cas visant tous deux à produire une paire d'entiers `finalX` et `finalY` qui seront vérifiés ultérieurement.

Le premier cas se produit si la cible est déjà dans le rayon de déplacement de l'unité sélectionnée, le second si elle est en dehors : ce second cas fait appel à une méthode auxiliaire `optimalBorderXOrY()` qui renvoie la cellule aux confins du rayon de déplacements la plus proche de la cible.

Une fois la paire `finalX` - `finalY` obtenue, l'algorithme vérifie que la cellule correspondant à ces coordonnées est bien accessible via la méthode `canEnterAreaCells()` : si ce n'est pas le cas elle itère sur toutes les cellules voisines pour trouver la première adéquate.

## Extensions

### Rivières

La première fonctionnalité que nous avons ajoutée est l'impossibilité de se placer sur les **rivières**, ce qui rajoute des obstacles dans le placement des unités. Nous avons ajouté un booléen walkable à l'énumération des `ICWarsCellType`, est avons adapté la méthode `canEnter()` des `ICWarsCell` en conséquence. Nous avons seulement rencontré une difficulté causée par deux cas limite dans la seconde grille de jeu : la rivière prend la moitié de la largeur de la grille sur les deux dernières lignes, ce qui faisait rentrer notre **AIPlayer** dans une boucle infinie. Cela ne concernait cependant que deux cellules, aussi avons-nous décidé d'utiliser le mécanisme de gestion des exceptions fourni par Java : dans le sous-paquetage exception, nous avons créé la classe `WrongLocationException`, et nous avons lancé et géré cette exception dans la méthode `getClosestPositionPossible()` si les coordonnées de la cible correspondait à ces deux cas limites.

### Panneaux structurels

La seconde fonctionnalité que nous avons ajoutée est la présence de deux panneaux qui rendent le jeu plus complet et structuré : le panneau de sélection de l'adversaire et de gestion de la fin du jeu. Dans le sous-paquetage gui, nous avons ajouté deux classes `ICWarsOpponentPanel` et `ICWarsGameOverPanel` sur le même modèle que la classe `ICWarsActionPanel`, dont l'affichage est géré dans la classe `ICWars` par les méthodes respectives `selectPlayer()` et `selectEnd()` appellées continuellement par la méthode `update()`.

### Musique

La troisième fonctionnalité que nous avons ajoutée est une musique d'ambiance rudimentaire tournant en fond. Pour l'ajouter au jeu, nous avons ajouté le sous-paquetage music et créé la classe `AudioFilePlayer.java` s'occupant de jouer une musique, dont le fichier audio est placé dans les ressources. Pour lancer la musique, nous avons employé le multi-threading et avons donc lancé un second thread au démarrage du jeu s'occupant uniquement de la musique.

### Artilleurs

La quatrième fonctionnalité que nous avons ajoutée est un troisième type d'unité : les **artilleurs**. Ces derniers ont moins de points de vie et causent moins de dommage que les soldats et les tanks mais ont une faculté bien pratique : celle d'infliger des dégâts de zone. Ils sont codés dans la classe `RocketMan.java` dans le sous-paquetage `unit`. Lorsqu'ils attaquent, un curseur $3 \times 3$ que le joueur peut librement déplacer sur la grille apparaît. Pour ceci, nous avons créé une sous-classe spéciale héritant de `AttackAction`, `RocketManAttackAction`, qui possède un attribut `scope`. Ce dernier est une instance de la classe `RocketManScope`, codée dans le sous-paquetage scope, et héritant de `ICWarsActor` en implémentant l'interface `Interactor`. Cet héritage lui permet de pourvoir être déplacé sur la grille et de pouvoir bénéficier des interactions de contact avec les cellules sur lesquelles il est présent (nous avons redéfini `getCurrentCells()` pour que cette dernière renvoie les $9$ cellules occupées par le scope). Lorsque le joueur appuie sur la touche <kbd>Enter</kbd>, la méthode `interactWith()` de `RocketManScope` capture les unités avec lesquelles il interagit dans un tableau que la `RocketManAttackAction` peut récupérer pour infliger les dégâts coorespondants dans sa redéfinition de `doAction()`.

Pour adapter notre IA  à la présence d'artilleurs, nous avons redéfini la méthode `doAutoAction()` de `RocketManAttackAction` : cette dernière affiche le scope, puis dans la continuité de l'algorithme choisit la cible ayant le moins de vie pour se positionner et frapper.

### Animations

La cinquième fonctionnalité que nous avons ajoutée est la présence d'animations lors de l'attaque. En effet, nous trouvions dommage de ne pas avoir d'affichage concret lors des attaques des unités ce qui rendait le jeu quelque peu incompréhensible. Nous avons donc défini un nouveau constructeur dans la classe `Sprite` et un nouveau chemin dans la classe `RessourcePath` pour accéder à $7$ sprites présents dans le dossier `images/explosion` des ressources. Dans les classes `AttackAction` et `RocketManAttackAction`, nous avons défini une variable de type `Animation` prenant en paramètre un tableau des $7$ sprites et les faisant s'enchaîner à la vitesse `frameDuration`.

Cette variable `animation` est dessinée dans les méthodes `draw()` respectives de ces deux classes, en suivant des conditions imposées par la sélection des cibles et déterminées dans les méthodes `doAction()` des classes `AttackAction` et `RocketManAttackAction` et `interactWith()` du scope pour le `RocketMan`. Pour l'IA, il a fallu intégrer l'affichage des animations dans les méthodes `doAutoAction()`.

### Cités

La sixième fonctionnalité que nous avons intégrée est la présence de cités. Ces dernières sont présentes uniquement dans le niveau $2$, et permettent de gagner des points de vie à la faction conquérante. Une cité est représentée par la classe `ICWarsCity` héritant de `ICWarsActor`, et les cités sont automatiquement enregistrées dans le `ICWarsBehavior` grâce aux cases qui leurs sont dédiées. Leur implémentation en tant qu'acteurs leur permet de recevoir des interactions, ce qui permet aux tanks, lorsqu'ils sont positionnés dessus, d'interagir avec elles et de pouvoir les capturer en ajoutant à leur `actionsList` l'action **(C)apture**. La fonction `getCities()` dans `ICWarsArea` permet de retourner toutes les cités appartenant à une faction donnée et donc d'incrémenter les unités de la même faction d'un point de vie à chaque nouveau tour. L'artilleur a la possibilité de libérer des cités ennemies en les rendant neutres, grâce à l'interaction de contact de son scope. Nous avons adapté l'IA afin que celle-ci privilégie le positionnement sur une cité plutôt que l'attaque des unités ennemies si une cité est dans son rayon de déplacement.

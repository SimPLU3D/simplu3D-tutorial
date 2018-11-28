---
title: Principe de fonctionnement de SimPLU3D
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Principe du fonctionnement de SimPLU3D

Le simulateur de base fait ressortir trois composants principaux de SimPLU3D :

1. l'environnement géographique [environnement géographique](/../envgeo/intro.md) qui contient les objets géographiques modélisés nécessaires à la simulation avec SimPLU3D ;
2. la définition d'un [générateur de formes](/../generator/intro.md), qui définit à la fois la stratégie d'optimisation employé par le simulateur et la forme des configurations simulées ;
3. la définition de [contraintes morphologiques](/../rules/intro.md), qui s'appuient sur les éléments du modèle de l'environnement géographique et contraignent la génération des formes bâties.

# Présentation simple de l'approche

Pendant une simulation, SimPLU3D essaye itérativement différentes configurations générées en accord avec la générateur de formes. À chaque itération, une configuration est testée. Il faut tout d'abord que celle-ci respecte les règles morphologiques utilisées et définies relativement à l'environnement géographique (Par exemple, recul par rapport à la voirie, distance aux bâtiments existants, etc.). Ensuite, la configuration proposée est évaluée relativement à une fonction d'optimisation et la configuration est testée suivant des critères d'acceptation propres à la méthode employée (à savoir le [recuit simulé](https://fr.wikipedia.org/wiki/Recuit_simul%C3%A9)).

Dans les prochaines sections, ces différents points seront abordés ainsi que la présentation plus détaillée de l'algorithme.

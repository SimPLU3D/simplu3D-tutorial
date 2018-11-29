---
title: Premiers pas - Introduction
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Introduction

SimPLU3D est une bibliothèque de recherche ce qui signifie qu'il n'y a actuellement pas d'interface graphique dédiée à la paramétrisation du code et que la maintenance du code n'est pas effectuée de manière très régulière.

Cela signifie que pour être capable d'utiliser SimPLU3D, il est nécessaire d'avoir des compétences :

- dans le langage de programmation Java et notamment une bonne connaissance de l'héritage et des generics ;
- dans le gestionnaire de projet Maven afin de pouvoir importer les bibliothèques de SimPLU3D ;
- si possible, sur le fonctionnement de [GeOxygene](http://ignf.github.io/geoxygene/) et notamment [de son module 3D](http://ignf.github.io/geoxygene/documentation/application/3d.html). Cela peut être nécessaire étant donné que cette bibliothèque est utilisée pour les opérations SIG (néanmoins, si vous n'êtes pas familier avec ce projet, les classes et méthodes utilisées sont relativement communes aux autres API de développement SIG).

La documentation s'organise autour de trois parties présentant les trois aspects principaux du code de SimPLU3D :

- L'utilisation de données géographiques à travers son [environnement géographique](../envgeo/intro.md) ;
- [La génération de formes bâties par optimisation](../generator/intro.md) ;
- [La définition des contraintes morphologiques](../generator/intro.md) qui contraignent la génération.

Pour illustrer comment utiliser SimPLU3D, des codes commentés sont mis à disposition dans le projet [SimPLU3D-tutorial](https://github.com/SimPLU3D/simplu3D-tutorial). Ces codes partent de l'exemple d'une [première simulation](first_simulation.md) et des évolutions sont apportées suivant les aspects abordées dans ces parties. Pour chacune des parties, le degré de personnalisation progresse de manière croissante avec une difficulté également croissante. Cela débute par une simple paramétrisation de fichiers de configuration pour aller jusqu'à la création de nouvelles classes pour générer par exemple de nouvelles formes ou intégrer de nouvelles règles.

Pour débuter, il est tout d'abord nécessaire [d'installer SimPLU3D](installation.md).

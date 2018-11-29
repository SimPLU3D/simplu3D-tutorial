---
title: Générateur de formes - Introduction
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Introduction au générateur de formes

L'objectif de cette section est de décrire le code de génération de formes bâties. La génération de formes bâties se base sur une méthode d'optimisation du recuit-simulé transdimensionnel (en utilisant la bibliothèque [librjmcmc4j](https://github.com/IGNF/librjmcmc4j)).

Les codes qui permettent la génération de formes se trouvent dans le projet [SimPLU3D](https://github.com/SimPLU3D/simplu3D) et la javadoc est déployée à l'adresse suivante :  [https://SimPLU3D.github.io/simplu3D/](https://SimPLU3D.github.io/simplu3D/). Le package principal (*fr.ign.cogit.simplu3d.rjmcmc*) de SimPLU3D est composé de sous-packages en fonction des types de formes utilisées (*generic* pour toutes les formes, *cuboid* pour les boîtes, *paramShp* pour des formes paramétriques et *trapezoid* pour les trapezes).

Dans cette section, nous présenterons tout d'abord la méthode de [génération des formes bâties](principe.md), puis comment il est possible de :

- [Paramétrer l'algorithme](custom-generator.md) pour la génération de formes composées de boîtes ;
- [Modifier la fonction d'optimisation](custom-generator.md) ;
- [Générer des formes bâties composées de géométries autres que des cuboïdes](custom-shape.md).

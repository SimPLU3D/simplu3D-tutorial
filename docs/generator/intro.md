---
title: Générateur de formes - Introduction
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

L'objectif de cette section est de décrire le code de génération de formes bâties. La génération de formes bâties se base sur une méthode d'optimisation basée sur le recuit-simulé transdimensionnel (en utilisant la bibliothèque [librjmcmc4j](https://github.com/IGNF/librjmcmc4j)).

Les codes qui permettent la génération de formes se trouvent dans le projet [SimPLU3D](https://github.com/SimPLU3D/simplu3D) et la javadoc est déployée à l'adresse suivante :  [https://SimPLU3D.github.io/simplu3D/](https://SimPLU3D.github.io/simplu3D/).

Dans cette section, nous présenterons tout d'abord la méthode de [génération des formes bâties](principe.md), puis comment il est possible de :

- [Paramétrer l'algorithme](custom-generator.md) pour la génération de formes composées de cuboïdes ;
- [Modifier la fonction d'optimisation](custom-generator.md) ;
- [Générer des formes bâties composées de géométries autres que des cuboïdes](custom-shape.md).

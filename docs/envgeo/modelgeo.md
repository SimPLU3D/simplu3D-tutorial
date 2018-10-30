---
title:  Environnement géographique - Modèle géogrpahique
authors:
    - Mickaël Brasebin
date: 2018-10-39

---

# Introduction

Le modèle géographique a pour objet de décrire les objets à partir desquels on peut appliquer des contraintes morphologiques et décrire la réglementation urbaine. Le modèle proposé dans SimPLU3D a été établi pour modéliser les contraintes spécifiquement issues des [Plans Locaux d'Urbanisme](https://fr.wikipedia.org/wiki/Plan_local_d%27urbanisme). Ainsi, le modèle contient les concepts que l'on retrouve dans ces documents à partir d'un état de l'art. Par exemple, si l'on considère la règle suivante :

![Exemple de règle issu du PLU](./img/ex-regle.png)

le modèle permet de représenter les objets géographiques (en rouge), les propriétés (en bleu) et les relations (en vert). Ensuite, ces informations seront utilisées pendant la simulation pour vérifier si une configuration bâtie respecte ou non cette règle et cela est décrit dans la section [ définition de contraintes morphologiques(/../rules/intro.md).

Pour en savoir plus, le modèle a fait l'objet d'une publication scientifique :


> Brasebin, M., J. Perret, S. Mustière and C. Weber (2016) A Generic Model to Exploit Urban Regulation Knowledge, ISPRS International Journal of Geo-Information, vol. 5, n. 2, pp. 14, [doi:10.3390/ijgi5020014](https://www.mdpi.com/2220-9964/5/2/14)


# Vue global du modèle

Le modèle est certes complexe dans l'absolu, il n'est néanmoins (comme présenté dans l'[exemple de la simulation basique](../being/first_simulation.md)) pas nécessaire de renseigner toutes les classes pour permettre l'exécution du modèle. Seules les classes relatives aux parcelles doivent absolument être instanciées. Le fait de ne pas instancier une des classes du modèle rend impossible la capacité d'évaluer des contraintes s'appuyant sur les éléments non instanciés.

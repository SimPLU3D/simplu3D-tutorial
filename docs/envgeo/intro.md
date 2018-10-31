---
title: Environnement géographique - Introduction
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

L'environnement géographique de SimPLU3D permet de décrire l'ensemble des objets géographiques nécessaires à la génération de formes bâties et notamment à la vérification des contraintes morphologiques. Ces objets géographiques peuvent concerner d'autres objets de la ville qui seront utilisés pour vérifier le respect des contraintes morphologiques (Par exemple, les routes, les types de limites séparatives) ou en lien avec l'application de règlements d'urbanisme d'un territoire (Par exemple, les plans de zonage ou les servitudes d'utilité publique).

L'ensemble des codes gérant cet aspect se trouve dans le dépôt [SimPLU3D-rules](https://github.com/SimPLU3D/simplu3D-rules). Il contient :

- [un modèle géographique](modelgeo.md) ;
- des [méthodes automatiques d'intégration](integration.md), pour renseigner automatiquement les classes, les attributs et les relations nécessaires au modèle à partir de données géographiques usuelles ;
- un [exporteur](integration-test.md) pour visualiser dans un SIG les éléments créés ;
- des possibilités de [paramétrer le processus d'intégration](custom-integration) afin de l'adapter à différents types de données.

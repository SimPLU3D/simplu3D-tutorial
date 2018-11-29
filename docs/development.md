---
title: Liste des futurs développements
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Futurs développements

Cette page liste les futurs développements pour les projets de SimPLU3D :

- **Persistance de l'environnement géographique** : pour l'instant, l'instanciation du modèle et les étapes d'intégration s'effectuent lors de chaque chargement. Il pourrait être nécessaire de mettre en place un chargement direct des données (c'est à dire sans passer par les étapes d'intégration). Cela pourrait, par exemple, permettre de reprendre manuellement une étape automatique du processus d'intégration, comme l'affectation d'un type aux limites séparatives de parcelles ou gérer du versionnement.

- **Gestion du multi-objet** : SimPLU3D ne génère actuellement que des objets d'une seule classe, pourtant la libjrmcmc4j permet de générer différents objets de différentes classes. Il pourrait être intéressant de mieux formaliser la gestion du multi-objet afin de générer simultanément différents objets sur un parcelle comme par exemple, des formes bâties, de la végétation et des garages. Un premier exemple a été mis en oeuvre dans ce [code du projet SimPLU3D-IAUIDF](https://github.com/SimPLU3D/simplu3D-iauidf/blob/master/src/main/java/fr/ign/simplu3d/iauidf/optimizer/mix/MultipleBuildingsCuboid.java), mais cet aspect n'est pas encore suffisamment bien formalisé pour pouvoir être simplement réutilisé dans les projets de SimPLU3D.

- **Aspects architecturaux** : les grammaires utilisées dans SimPLU3D sont relativement simples, il s'agit d'un ensemble de formes sans lien les unes avec les autres sur lesquelles des contraintes sont ajoutées. Elles ne se basent pas sur des grammaires architecturales qui permettent de produire des bâtiments réalistes avec un haut niveau de détail. Il pourrait être implémenté des mécanismes plus complexes de formalisation des formes et d'optimisation comme ceux décrits dans l'article ;

> Talton, J. O., Lou, Y., Lesser, S., Duke, J., Měch, R., Koltun, V., Apr. 2011. Metropolis procedural modeling. ACM Trans. Graph. 30 (2). URL [http://doi.acm.org/10.1145/1944846.1944851](http://doi.acm.org/10.1145/1944846.1944851)

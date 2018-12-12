---
title: Vérificateurs de règles - Prédicats et vérification des règles
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Implémentation de la vérification de règles dans SimPLU3D

Pour vérifier les règles morphologiques, SimPLU3D utilise le concept de prédicat. Il s'agit d'un objet qui indique si une configuration respecte une règle ou non.

# Interface de predicat

 L'interface de la librjmcmc4j *fr.ign.rjmcmc.configuration.ConfigurationModificationPredicate<C extends Configuration<C, M>, M extends Modification<C, M>>* implémente cette notion de prédicat et permet la définition à elle seule du respect des règles.

 Cette interface ne définit que la méthode *boolean check(C c, M m);*. Avec :

- **c**, une instance de configuration, qui contient la liste des objets de la configuration courante  (c'est à dire avant application de la modification) ;
- **m**, la modification qui serait appliquée à la configuration **c** ;
- la méthode renvoie un *boolean* qui indique si les règles sont respectées suite à l'application de la modification **m** sur la configuration **c**.


# Implémentation à partir de l'interface de prédicat

Dans SimPLU3D, nous conseillons l'implémentation de cette interface à travers la définition suivante (et de regarder la-dite classe qui donne une idée de comment implémenter la méthode) :

```JAVA
 SamplePredicate<O extends ISimPLU3DPrimitive, C extends AbstractGraphConfiguration<O, C, M>, M extends AbstractBirthDeathModification<O, C, M>>
		implements ConfigurationModificationPredicate<C, M>
```

Ainsi, à travers la classe abstraite *AbstractGraphConfiguration*, il est possible d'accéder à la liste des objets de la configuration courante à travers un itérateur (méthode *iterator*) et à travers la classe *AbstractBirthDeathModification* d'accéder aux modifications à travers la création *m.getBirth()* et la destruction d'objets *m.getDeath()* de la classe utilisée pendant la génération.
Une modification est vue comme l'ajout et la suppression d'objet. Dans les modifications usuellement utilisées dans SimPLU3D, ces listes contiennent soit 0 soit 1 objet.

Il suffit ensuite de coder la vérification des règles pour les différents objets de la configuration après modification en utilisant les informations disponibles dans l'[environnement géographique](../envgeo/intro.md)). Ainsi, pour avoir la liste des objets de la classe O après modification, on peut utiliser le code suivant :

```JAVA
List<O> listOfObjects = new ArrayList<>();
  Iterator<O> iTBat = c.iterator();

  while ( iTBat.hasNext()) {
    listOfObjects.add(iTBat.next());
  }

  //La méthode equals() doit être définie pour la classe O
  listOfObjects.removeAll(m.getDeath());
  listOfObjects.addAll(m.getBirth());
```



> ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) **Attention**: comme l'évaluation de la méthode *check()* s'effectue à chaque itération et qu'une simulation peut compter des millions d'itérations, optimiser le temps d'exécution de cette méthode permet de diminuer de beaucoup le temps de calcul, d'autant plus que cette méthode fait généralement appel à des opérateurs géométriques qui peuvent être coûteux en termes de temps. Voici quelques astuces pour diminuer ce temps de calcul :

> 1/ Essayer de renvoyer *false* chaque fois dès qu'une règle n'est pas respectée ;

> 2/ Privilégier les géométries JTS aux géométries GeOxygene (les opérateurs géométriques appliquées aux géométries GeOxygene nécessitent une conversion JTS qui augmente le temps de calcul) ;

> 3/ Conserver des objets en cache lorsqu'ils sont fixes pour éviter de les re-générer à chaque étape ;

> 4/ Ne vérifier les règles que pour les objets nécessaires. En effet, les géométries dans la configuration avant modification vérifient déjà un certain nombre de règles, il n'est ainsi pas nécessaire de devoir les réévaluer pour celles-ci. Cela est par exemple vrai pour les règles de hauteur ou de distance 2D avec les limites séparatives.


# Implémentation à partir de la classe abstraite DefaultAbstractPredicate

La classe *fr.ign.cogit.simplu3d.util.regulation.DefaultAbstractPredicate* contient un certain nombre de fonctions de base qui sont issues de règles souvent utilisées dans SimPLU3D. Les méthodes de cette classe suivent les principes évoqués précédemment et permettent de diminuer le temps de calcul. En étendant cette classe abstraite et en utilisant son constructeur, il est possible de définir plus facilement la méthode *check()* d'un prédicat en utilisant les fonctions prédéfinies.

On retrouve trois types de fonctions :

- **des accesseurs**, pour accéder directement aux géométries JTS d'un certain nombre d'objets issus du modèle ;
- des **vérificateurs portant sur l'ensemble des objets** de la configuration courante ;
- des **vérificateurs qui peuvent ne porter que sur les nouveaux objets** proposés par la modification (cf principe 4 évoqué précédemment).


## Accesseurs

Ces méthodes permettent d'accéder directement à certaines géométriques JTS sans  avoir à parcourir l'ensemble du modèle

| Nom de la méthode               | Commentaire                                        |
|:--------------------------------|:---------------------------------------------------|
| getJtsCurveLimiteFondParcel     | Géométrie des limites de fond de parcelle          |
| getJtsCurveLimiteFrontParcel    | Géométrie des limites donnant sur la voirie        |
| getJtsCurveLimiteLatParcel      | Géométrie des limites latérales                    |
| getJtsCurveLimiteLatParcelLeft  | Géométrie des limites latérales du côté gauche     |
| getJtsCurveLimiteLatParcelRight | Géométrie des limites latérales du côté droit      |
| getJtsCurveOppositeLimit        | Géométrie des limites du côté opposé à la parcelle |
| getbPUGeom                      | Géométrie de l'unité foncière                      |


Si une géométrie n'existe pas du fait de la configuration spatiale, elle aura null comme valeur.


## Vérificateur portant sur l'ensemble des objects

Il s'agit de méthodes qui vérifient certaines règles portant sur l'ensemble des objets après modification. La méthode *getAllObjectsAfterModifcation(C c, M m)* permet d'obtenir cette liste et de faire porter le test sur celle-ci. Par exemple, dans la méthode *check* cela revient à ajouter, par exemple, le code suivant  :

```JAVA
//On récupère la liste des objets après modification
List<O> objects = this.getAllObjectsAfterModifcation(C c, M m);
//Si le test qui vérifie le nombre d'objets est faux, on considère que les règles ne sont pas respectées, on renvoie faux au niveau de la méthode check().
if(! checkNumberOfBuildings(objects, 8)){
  return false;
}
```

On retrouve les méthodes suivantes :

| Nom de la méthode                                                    | Commentaire                                                                                                       |
|:---------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------|
| checkNumberOfBuildings(allObjects, numberMaxOfObject)                | Vérifie que le nombre d'objets de la configuration est inférieure à numberMaxOfObject                             |
| checkBuiltRatio(allObjects,  maxValue)                               | Vérifie que le ratio de surface couverte de l'unité foncière est inférieure à maxValue                            |
| checkDistanceBetweenObjectandBuildings(allObjects, distMinInterBati) | Vérifie que la distance entre chaque objet simulé et les bâtiments existants est supérieure à    distMinInterBati |
| checkDistanceBetweenObjects(allObjects, distMinInterBati)            | Vérifie que la distance entre chaque paire d' objets simulés est supérieure à    distMinInterBati                 |


## Vérificateurs portant sur l'ensemble des nouveaux objets

Il s'agit de méthodes qui peuvent être vérifiés que sur les nouveaux objets créés par la modification appliquée (dans le sens où l'ajout de nouveaux objets ne change pas le respect de la règles pour les objets déjà existants de la configuration). La liste des objets créés par la modification peut être obtenue avec la méthode *m.getBirth()*. Ainsi, utiliser ces méthodes dans la méthode *check* revient par exemple à ajouter le code suivant :

```JAVA
//On récupère la liste des objets ajoutés par la modification
List<O> objects = m.getBirth();
//Si le test qui vérifie si les nouveaux objets sont à l'intérieur de la géométrie de l'unité foncière est faux
//On renvoie faux
if(! checkIfInsideBPU(objects)){
  return false;
}
```

On retrouve les méthodes suivantes :

| Nom de la méthode                                                | Commentaire                                                                                                      |
|:-----------------------------------------------------------------|:-----------------------------------------------------------------------------------------------------------------|
| checkDistanceToGeometry(objects, geom,  distMin)                 | Vérifie que la distance entre les objets et une géométrie est supérieure à distMin                               |
| checkDistanceToGeometry(objects, geom,  dist,  supOrInf)         | Vérifie que la distance entre les objets et une géométrie est supérieure ou inférieure à distMin                 |
| checkDistanceToLimitBySide(objects,  distanceMin,lBoundaryType)  | Vérifie que la distance entre les objets et les limites séparatives des types fournis est supérieure à distMin   |
| checkDistanceToLimitByType(objects,  distanceMin, lBoundaryType) | Vérifie que la distance entre les objets et les limites séparatives des côtés fournis est supérieure à distMin   |
| checkDistanceToOppositeLimit(objects,  distanceMin)              | Vérifie que la distance entre les objets les limites séparatives des parcelles opposées est supérieure à distMin |
| checkIfContainedInGeometry(objects,geometry)                     | Vérifie que la distance entre les objets sont contenues dans une géométrie                                       |
| checkIfInsideBPU(objects)                                        | Vérifie que la distance entre les objets sont inclus dans la géométrie de l'unité foncière                       |
| checkIfIntersectsGeometry(objects, geometry)                     | Vérifie que la distance entre les objets intersectent dans une géométrie                                         |

Les méthodes peuvent aussi être utilisées si on ne souhaite pas qu'une condition soit vérifiée. Par exemple, si on ne souhaite pas que les objets intersectent une géométrie, on peut utiliser la méthode *checkIfIntersectsGeometry* et renvoyer faux si la condition est vérifiée.

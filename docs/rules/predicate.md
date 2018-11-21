---
title: Vérificateurs de règles - Prédicats et vérification des règles
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

Pour vérifier les règles morphologiques, SimPLU3D utilise le concept de prédicat. Il s'agit d'un objet qui indique si une règle respecte ou non respecte une règle.

# Interface de predicat

 L'interface de la librjmcmc4j *fr.ign.rjmcmc.configuration. ConfigurationModificationPredicate<C extends Configuration<C, M>, M extends Modification<C, M>>* implémente cette notion de prédicat est permet la définition à elle seule du respect des règles.

 Cette interface ne définit que la méthode *boolean check(C c, M m);*. Avec :

- **c**, une instance de configuration, qui contient la liste des objets de la configuration courante  (c'est à dire avant application de la modification);
- **m**, la modification qui serait appliquée à la configuration **c** ;
- la méthode renvoie un boolean qui indique si les règles sont respectées suite à l'application de la modification m sur la méthode **c**.


# Implémentation à partir de l'interface de prédicat

Dans SimPLU3D, nous conseillons l'implémentation de cette interface à travers la définition suivante (et de regarder la-dite classe qui donne une idée de comment implémenter la méthode) :

```JAVA
 SamplePredicate<O extends ISimPLU3DPrimitive, C extends AbstractGraphConfiguration<O, C, M>, M extends AbstractBirthDeathModification<O, C, M>>
		implements ConfigurationModificationPredicate<C, M>
```

Ainsi, à travers la classe abstraite *AbstractGraphConfiguration*, il est possible d'accéder à la liste des objets de la configuration courante à travers un itérateur (méthode *iterator*) et à travers la classe *AbstractBirthDeathModification* d'accéder aux modifications à travers la création *m.getBirth()* et la destruction d'objets *m.getDeath()* de la classe utilisée pendant la génération.
Une modification est vue comme l'ajout et la suppression d'objet. Dans les modification usuellement utilisées dans SimPLU3D, ces listes contiennent soit 0 soit 1 objet.

Il suffit ensuite de coder la vérification des règles pour les différents objets de la configurations après modification en utilisant les informations disponibles dans l'[environnement géographique](../envgeo/intro.md)). Ainsi, pour avoir la liste des objets de la classe O après modification sur laquelle il faut porter les vérifications, on peut utiliser le code suivant :

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



> ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) **Attention**: comme l'évaluation de la méthode *check()* s'effectue à chaque itération et qu'une simulation peut compter des millions de simulation, optimiser le temps d'exécution de cette méthode permet de diminuer de beaucoup le temps de calcul, d'autant plus que cette méthode fait généralement à des opérateurs géométriques qui peuvent être coûteux en termes de temps. Voici quelques astuces pour diminuer ce temps de calcul :

> 1/ Essayer de renvoyer fois dès qu'une règle n'est pas respectée ;

> 2/ Privilégier les géométries JTS aux géométries GeOxygene (les opérateurs géométriques appliquées aux géométries GeOyegne nécessitent une conversion JTS qui augmente le temps de calcul) ;

> 3/ Conserver des objets en cache lorsqu'ils sont fixes pour éviter de les re-générer à chaque étape.

> 4/ Il n'est pas nécessaire de vérifier les règles pour tous les objets de la configuration après modification. En effet, les géométries dans la configuration avant modifications vérifient déjà ces règles. Cela est notamment vrai pour les règles de hauteur ou de distance 2D avec d'autres limites


# Implémentation à partir de la classe abstraite DefaultAbstractPredicate

La classe *fr.ign.cogit.simplu3d.util.regulation.DefaultAbstractPredicate* contient un certain nombre de fonctions géométriques de base qui sont issues de règles souvent utilisées dans SimPLU3D. Les méthodes de cette classe suivent les principes évoqués précédemment et permet de diminuer le temps de calcul.

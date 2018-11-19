---
title: Générateur de formes - Modifier la fonction d'optimisation
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

Dans cette partie, nous présenterons comment est configuré l'algorithme d'optimisation à travers les conditions intitiales et d'arrêt, ainsi que la définition de la fonction d'optimisation.


Les parties suivantes reprennent les principales étapes de création du sampler dont le code est repris dans [la dernière partie de la page sur l'implémentation](#implementation) (étape 2) , c'est à dire le code suivant :

```JAVA
//Step 2 : Preparation of the optimizer
//Étape 2 : Préparation de l'optimiseur

//Initializing the configuration (optimisation function + set of cuboid)
//Initizialization de la configuration (fonction d'optimisation + stock les cuboids de la configuration courante
GraphConfiguration<Cuboid> conf = null;
try {
  conf = create_configuration(p, AdapterFactory.toGeometry(new GeometryFactory(), bpu.getGeom()), bpu);
} catch (Exception e) {
  e.printStackTrace();
}

// Temperature initialization
//Initialization de la fonction de la température
Schedule<SimpleTemperature> sch = create_schedule(p);


//The end test condition
end = create_end_test(p);
```

# Définition de la fonction d'optimisation

## Principe général

La fonction à optimiser est contenue dans un objet de la classe générique *GraphConfiguration<?>* à typer en fonction de la classe paramétrique utilisée. La fonction d'optimisation est composée de 3 énergies :

- **une énergie unaire** (interface *UnaryEnergy<?>*), qui évalue indépendant une énergie pour chacun des objets  ;
- **une énergie binaire** (interface *BinaryEnergy<?>*), qui évalue une énergie pour chaque couple d'objet ;
- **une énergie de collection** facultative  (interface *CollectionEnergy<?>*), qui évalue la somme pour l'ensemble de la collection.
Les interfaces des classes ne définissent qu'une méthode *double getValue()* qui renvoie la valeur numériques associée

La valeur de la fonction d'optimisation est la somme des contributions de ces trois types d'énergie. Pour produire ces contributions, il est possible, pour chaque type d'énergie, de combiner différentes opérateurs (addition, soustraction, multiplication, etc.) pour produire des énergies composites. La définition de ces opérateurs se trouve dans le package *fr.ign.rjmcmc.energy* de la librjmcmc4j.

> ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) **Attention**: la librjmcmc4j est une bibliothèque minimisant par convention la fonction énergétique, il est nécessaire d'adapter l'instanciation de la fonction d'énergie en accord avec ce principe. Dans l'exemple au début, il s'agit de minimiser - volume(configuration)).


## Illustration avec l'exemple

La formule de l'énergie dans l'exemple est la suivante :
![Formule ](./img/energieformule.png)

L'énergie unaire contient différents termes :
- **Ecreation** a pour objectif de pénaliser les boîtes ne contribuant pas suffisamment à la configuration. La définition de ce paramètre est indispensable : en effet, rien n’empêche l’intersection de boîtes. Sans ce paramètre, que nous nommons énergie de création, le système pourrait très bien proposer des configurations contenant de très nombreuses boîte ;
- **volume(b)** qui définit le volume de chaque boîte indépendamment ;
- **volumeDifference(bPU,b)** qui définit une énergie en fonction du dépassement en termes de volume de l'unité foncière simulée (bPU). Comme les règles n'imposent pas dans l'exemple que les boîtes se trouvent strictement à l'intérieur de l'unité foncière. L'objectif de ce terme est d'autoriser le dépassement que si celui-ci contribue significativement à l'amélioration du volume.
Les deux derniers termes sont pondérés par **ponderationVolume** et **ponderationDifference**.

L'énergie binaire contient **volumeintersection(b1, b2)** la différence des volumes entre chaque couple de boîte qui est pondéré par **ponderation_volume_inter**.

Les différentes pondérations et la valeur **Ecreation** sont paramétrables, pour l'exemple, dans le fichier **params.json**.

![Graphe d'énergie ](./img/graph.png)

La figure ci-dessus représente le graphe d'énergie tel que modélisé en accord avec le formalisme de la librjmcmc4j. Les énergies dynamiques sont évaluées à chaque itération et implémentent  *UnaryEnergy<?>* et  *BinaryEnergy<?>* en fonction de l'énergie modélisée.

Il s'agit d'une représentation du code qui se situe ci-dessous et qui est utilisé dans le premier exemple de code.

```JAVA
/**
 * Creation of a cuboid configuration
 * @param p    parameters from the json file
 * @param bpu  the basic property unit on which the optimization will be
 *             proceeded
 * @param geom the geometry that contains the cuboids
 * @return a new configuration that embeds the calculation of the optimization
 *         function
 */

public GraphConfiguration<Cuboid> create_configuration(SimpluParameters p, Geometry geom, BasicPropertyUnit bpu) {
  // Énergie constante : à la création d'un nouvel objet
  ConstantEnergy<Cuboid, Cuboid> energyCreation = new ConstantEnergy<Cuboid, Cuboid>(p.getDouble("energy"));
  // Énergie constante : pondération de l'intersection
  ConstantEnergy<Cuboid, Cuboid> ponderationVolume = new ConstantEnergy<Cuboid, Cuboid>(
      p.getDouble("ponderation_volume"));
  // Énergie unaire : aire dans la parcelle
  UnaryEnergy<Cuboid> energyVolume = new VolumeUnaryEnergy<Cuboid>();
  // Multiplication de l'énergie d'intersection et de l'aire
  UnaryEnergy<Cuboid> energyVolumePondere = new MultipliesUnaryEnergy<Cuboid>(ponderationVolume, energyVolume);

  // On retire de l'énergie de création, l'énergie de l'aire
  UnaryEnergy<Cuboid> u3 = new MinusUnaryEnergy<Cuboid>(energyCreation, energyVolumePondere);

  double ponderationExt = p.getDouble("ponderation_difference_ext");

  UnaryEnergy<Cuboid> unaryEnergy;

  if (ponderationExt != 0) {
    // Énergie constante : pondération de la différence
    ConstantEnergy<Cuboid, Cuboid> ponderationDifference = new ConstantEnergy<Cuboid, Cuboid>(
        p.getDouble("ponderation_difference_ext"));
    // On ajoute l'énergie de différence : la zone en dehors de la parcelle
    UnaryEnergy<Cuboid> u4 = new DifferenceVolumeUnaryEnergy<Cuboid>(geom);
    UnaryEnergy<Cuboid> u5 = new MultipliesUnaryEnergy<Cuboid>(ponderationDifference, u4);
    unaryEnergy = new PlusUnaryEnergy<Cuboid>(u3, u5);
  } else {
    unaryEnergy = u3;
  }

  // Énergie binaire : intersection entre deux rectangles
  ConstantEnergy<Cuboid, Cuboid> c3 = new ConstantEnergy<Cuboid, Cuboid>(p.getDouble("ponderation_volume_inter"));
  BinaryEnergy<Cuboid, Cuboid> b1 = new IntersectionVolumeBinaryEnergy<Cuboid>();
  BinaryEnergy<Cuboid, Cuboid> binaryEnergy = new MultipliesBinaryEnergy<Cuboid, Cuboid>(c3, b1);
  // empty initial configuration*/
  GraphConfiguration<Cuboid> conf = new GraphConfiguration<>(unaryEnergy, binaryEnergy);
  return conf;
}
```

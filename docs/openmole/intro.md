---
title: Utilisation conjointe SimPLU3D - OpenMOLE
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

OpenMOLE [(https://openmole.org/)](https://openmole.org/) est une plate-forme dédiée à l'exploration de modèles de simulation. Cette plate-forme permet à travers la distribution de calculs de calibrer des modèles ou d'explorer de manière efficace l'espace de paramètres.

Ce couplage a été utilisé pour deux expérimentations : la production des simulations sur de grandes zones et pour la simulation de l'influence de paramètres de règles sur des formes bâties. Ces aspects sont présentés dans l'article suivant :

> Brasebin, M., P. Chapron, G. Chérel, M. Leclaire, I. Lokhat, J. Perret and R. Reuillon (2017) Apports des méthodes d’exploration et de distribution appliquées à la simulation des droits à bâtir, Actes du Colloque International de Géomatique et d'Analyse Spatiale (SAGEO 2017)



# Création d'un plug-in OpenMOLE.

Pour utiliser conjointement les deux projets, il est conseillé de créer un [plug-in OpenMOLE](https://openmole.org/Plugin+Development.html). Les différents couplages  déjà effectués se trouvent dans le projet [SimPLU3D-OpenMole](https://github.com/SimPLU3D/simplu3D-openmole).


##1 Création du projet Scala

Le plus simple pour créer le projet Scala est de repartir d'un exemple existant, en téléchargeant par exemple, le projet [SimPLU3D-OpenMole](https://github.com/SimPLU3D/simplu3D-openmole).

L'organisation du projet est classique, il y a un fichier build.sbt qui décrit la manière avec laquelle le projet est construit, dans celui-là, il est déjà spécifié que l'on cherche à compiler un projet OSGI (manière de compiler un plugin OpenMOLE).

Pour pouvoir utiliser SimPLU3D depuis ce projet, il faut le spécifier dans les dépendances, cela se fait en définissant les resolvers (c'est à dire le dépôt dans lequel on peut trouver le .jar contenant le code)   :

```scala
resolvers += Resolver.mavenLocal

resolvers += "IGN snapshots" at "https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/"

resolvers += "IGN releases" at "https://forge-cogit.ign.fr/nexus/content/repositories/releases/"
```


et en spécifiant les dépendances à utiliser :
```
libraryDependencies += "fr.ign.cogit" %% "simplu3d" % "1.2-SNAPSHOT"
```



## 2 Création du code Scala à exécuter

Il est tout d'abord nécessaire de préparer la tâche à exécuté en Java ou en Scala. Afin de pouvoir faciliter la distribution en ce qui concerne la gestion des fichiers envoyés et reçus pendant la distribution, nous conseillons au possible que le code ait la même signature que dans la méthode suivante (+ d'autres arguments)

```scala
package simplu3dopenmoleplugin

import java.io.File
import fr.ign.cogit.simplu3d.experiments.openmole._

object DistribTask {
  def apply(folderIn: File, folderOut: File, paramFile: File, seed: Long): (Boolean, File) = {
    val res = IAUIDFTask.run(folderIn, folderOut, paramFile, seed);
    (res, folderOut)
  }

}
```
Ce qui est utile pour la suite est :
* 1/ de bien se rappeler  du package scala (mot clef *package*) et de l'objet (mot clef *object*) ;
* 2/ il faut définir une méthode apply qui prend en entrée les paramètres nécessaires au fonctionnement de la tâche et qui indique les sorties (il est possible d'avoir plusieurs objets en sortie avec scala il suffit de définir les types en les séparant par des , comme ici  *(Boolean, File)* et la sortie est indiquée à la fin  (*res, folderOut)*'.

## 3 Production du bundle OSGI

A la racine, du projet, là où se trouve le *build.sbt*, il suffit d'exécuter :

```
 sbt osgiBundle -U
```


Un fichier .jar est alors produit dans "''target/scala''"



## 4 Définition du plan d'expérimentation et exécution dans OpenMole


Pour faire cela, je vous invite à regarder les tutoriels d'OpenMole pour apprendre à bien définir un script.

Une fois le plugin chargé, il est possible de l'utiliser dans la définition du workflow comme cela est expliqué en bas de [cette page]([http://www.openmole.org/current/Documentation_Development_Plugins.html).

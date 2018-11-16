---
title: Premiers pas - Installation
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Installer SimPLU3D

Les bibliothèques de SimPLU3D sont construites avec Maven et codées en Java (la version 8 de la JDK est requise) et sont régulièrement déployées sur le serveur Maven de l'IGN. Si vous voulez essayer les codes décrits dans cette documentation, vous pouvez installer le projet [SimPLU3D-tutorial](https://github.com/SimPLU3D/simplu3D-tutorial) ou sinon vous pouvez directement l'installer dans votre projet.

> ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) **Attention**: il se peut que la compilation du projet ne se fasse pas à cause de problèmes liées à la vérification du certificat ssh de la forge IGN. Pour cela deux solutions :

> - Installer localement le certificat comme décrit dans le lien suivant [http://ignf.github.io/geoxygene/documentation/developer/install.html](http://ignf.github.io/geoxygene/documentation/developer/install.html)

> - Faire une installation sécurisée lorsqu'il faudra construire le projet : **mvn clean install -Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true**

## Installer le code SimPLU3D-tutorial
Les étapes suivantes sont nécessaires :

1 - Installer un environnement de développement compatible avec Maven, nous recommandons de suivre ces étapes : [http://ignf.github.io/geoxygene/documentation/developer/install.html](http://ignf.github.io/geoxygene/documentation/developer/install.html)

2 - Cloner avec Git le projet https://github.com/SimPLU3D/simplu3D-tutorial

3 - Importer le projet dans l'environnement de développement. Dans Eclipse "Fichier > Importer un projet > Importer un projet Maven"

4 - Lancer une installation avec Maven :

- Click droit sur le projet et "**Run as Maven Install**"
- ou directement en ligne de commande : **mvn clean install**

5 - Lancer une construction du projet avec Eclipse : **Project > Build project**
Vous pouvez désormais exécuter la [première simulation](first_simulation.md).


Néanmoins, pour avoir accès au code source pendant le tutoriel, il est recommandé d'installer en local les codes issus de [SimPLU3D](https://github.com/SimPLU3D/simplu3D) et [SimPLU3D-rules](https://github.com/SimPLU3D/simplu3D-tutorial).

## Installer SimPLU3D dans un autre projet

L'installation s'effectue simplement en créant un projet qui pointe vers les dépendances de SimPLU3D. Les étapes sont les suivantes :

1 - Création d'un projet Maven

2 - Ajout de la dépendance à SimPLU3D dans le pom.xml dans les balises **dependencies**, en pensant à vérifier la version actuelle de SimPLU3D.
```XML
<dependency>
  <groupId>fr.ign.cogit</groupId>
  <artifactId>simplu3d</artifactId>
  <version>1.2-SNAPSHOT</version>
</dependency>
```
3 - Ajouter les dépôts sur lesquels est déployé SimPLU3D et les bibliothèques dans le pom.xml dans les balises **repositories**
```XML
<repository>
  <id>ign-snapshots</id>
  <name>Cogit Snapshots Repository</name>
  <url>https://forge-cogit.ign.fr/nexus/content/repositories/snapshots/</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
  <releases>
    <enabled>false</enabled>
  </releases>
</repository>
<repository>
  <id>ign-releases</id>
  <name>Cogit Releases Repository</name>
  <url>https://forge-cogit.ign.fr/nexus/content/repositories/releases/</url>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <releases>
    <enabled>true</enabled>
  </releases>
</repository>
```
4 - Faire un build du projet en utilisant par exemple : **maven install**.

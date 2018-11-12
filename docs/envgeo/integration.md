---
title: Environnement géographique - Processus d'intégration
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Introduction

Un processus d'intégration est déjà défini dans SimPLU3D. Il permet de créer un objet de la classe *Environnement* en renseignant un certain nombre d'attributs.

Le processus d'intégration peut prendre en entrée des données au format ShapeFile (avec la classe *fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP*)  ou des données dans une base de données PostGIS (avec la classe *fr.ign.cogit.simplu3d.io.nonStructDatabase.postgis.loadPostGIS*). Quelque soit la classe choisie, les deux classes ont une méthode *load*, qui va traduire les entités provenant de ces sources de données en collection de *IFeature* de GeOxygene et faire appelle à la méthode *load* de la classe *fr.ign.cogit.simplu3d.io.LoadFromCollection*.

Dans cette page, nous allons décrire tout d'abord les pré-requis en [fonction de la source de données utilisée](#sources-de-donnees-utilisees), puis décrire dans le détail [le processus d'intégration](#description-du-code-dintegration).

> ![#f03c15](https://placehold.it/15/f03c15/000000?text=+) **Attention**: actuellement la persistance n'est pas gérée, donc le processus d'intégration automatique est pour le moment le seul moyen d'utiliser le modèle géographique convenablement.


# Sources de données utilisées

La source de données peut intégrer des données [ShapeFile](#source-de-donnees-shapefile) ou [PostGIS](#source-de-donnees-postgis).

## Source de données ShapeFile

### Nom des fichiers

### Nom d'attribut pour les données de  : 



## Source de données PostGIS

### Nom des tables

# Description du code d'intégration

```JAVA
public static Environnement load(IFeature featPLU, IFeatureCollection<IFeature> zoneColl,
    IFeatureCollection<IFeature> parcelleColl, IFeatureCollection<IFeature> voirieColl,
    IFeatureCollection<IFeature> batiColl, IFeatureCollection<IFeature> prescriptions,
    AbstractDTM dtm, Environnement env) throws Exception {

  // Etape 0 : doit on translater tous les objets ?

  if (Environnement.TRANSLATE_TO_ZERO) {
    Environnement.dpTranslate = zoneColl.envelope().center();
    for (IFeature feat : zoneColl) {
      feat.setGeom(feat.getGeom().translate(-Environnement.dpTranslate.getX(),
          -Environnement.dpTranslate.getY(), 0));
    }
    for (IFeature feat : parcelleColl) {
      feat.setGeom(feat.getGeom().translate(-Environnement.dpTranslate.getX(),
          -Environnement.dpTranslate.getY(), 0));
    }
    for (IFeature feat : voirieColl) {
      feat.setGeom(feat.getGeom().translate(-Environnement.dpTranslate.getX(),
          -Environnement.dpTranslate.getY(), 0));
    }
    for (IFeature feat : batiColl) {
      feat.setGeom(feat.getGeom().translate(-Environnement.dpTranslate.getX(),
          -Environnement.dpTranslate.getY(), 0));
    }
    for (IFeature feat : prescriptions) {
      feat.setGeom(feat.getGeom().translate(-Environnement.dpTranslate.getX(),
          -Environnement.dpTranslate.getY(), 0));
    }
  }

  // Etape 1 : création de l'objet PLU
  logger.info("Read UrbaDocument...");
  UrbaDocument plu;
  if (featPLU == null) {
    plu = new UrbaDocument();
  } else {
    UrbaDocumentReader urbaDocumentReader = new UrbaDocumentReader();
    plu = urbaDocumentReader.read(featPLU);
  }
  env.setUrbaDocument(plu);

  // Etape 2 : création des zones et assignation des règles aux zones
  logger.info("Loading UrbaZone...");
  UrbaZoneReader urbaZoneReader = new UrbaZoneReader();
  IFeatureCollection<UrbaZone> zones = new FT_FeatureCollection<>();
  zones.addAll(urbaZoneReader.readAll(zoneColl));

  // Etape 3 : assignement des zonages au PLU
  env.setUrbaZones(zones);

  logger.info("Loading CadastralParcel and compute ParcelBoundary...");
  // Etape 4 : chargement des parcelles et créations des bordures
  IFeatureCollection<CadastralParcel> parcelles = CadastralParcelLoader
      .assignBordureToParcelleWithOrientation(parcelleColl);
  env.setCadastralParcels(parcelles);

  // Etape 5 : import des sous parcelles
  logger.info("Loading SubParcels...");
  {
    IFeatureCollection<SubParcel> sousParcelles = new FT_FeatureCollection<>();
    SubParcelGenerator subParcelGenerator = new SubParcelGenerator(zones);
    for (CadastralParcel cadastralParcel : parcelles) {
      sousParcelles.addAll(subParcelGenerator.createSubParcels(cadastralParcel));
    }
    env.setSubParcels(sousParcelles);
  }

  // Etape 6 : création des unités foncirèes
  logger.info("Loading BasicPropertyUnits...");
  BasicPropertyUnitGenerator bpuBuilder = new BasicPropertyUnitGenerator(parcelles);
  IFeatureCollection<BasicPropertyUnit> collBPU = bpuBuilder.createPropertyUnits();
  env.setBpU(collBPU);

  // Etape 7 : import des bâtiments
  logger.info("Loading Buildings...");
  BuildingReader buildingReader = new BuildingReader();
  Collection<Building> buildings = buildingReader.readAll(batiColl);
  env.getBuildings().addAll(buildings);

  // Etape 7.1 : assignation des batiments aux BpU
  logger.info("Assigning building to SubParcels...");
  AssignBuildingPartToSubParcel.assign(buildings, collBPU);

  // Etape 8 : chargement des voiries
  logger.info("Loading Roads...");
  RoadReader roadReader = new RoadReader();
  IFeatureCollection<Road> roads = new FT_FeatureCollection<>();
  roads.addAll(roadReader.readAll(voirieColl));
  env.setRoads(roads);

  // Etape 9 : on affecte les liens entres une bordure et ses objets
  // adjacents (bordure sur route => route + relation entre les limites de
  // parcelles)
  logger.info("Assigning Roads to ParcelBoundaries...");
  AssignRoadToParcelBoundary.process(parcelles, roads);

  // Etape 10 : on détecte les limites séparatives opposées
  logger.info("Assigning opposite boundaries to parcel boundaries...");
  AssignOppositeToBoundary.process(parcelles);

  // Etape 11 : on importe les alignements
  logger.info("Loading Prescriptions...");
  {
    PrescriptionReader prescriptionReader = new PrescriptionReader();
    Collection<Prescription> prescriptionsRead = prescriptionReader.readAll(prescriptions);
    env.getPrescriptions().addAll(prescriptionsRead);
  }

  logger.info("Assign Z to features...");
  // Etape 12 : on affecte des z à tout ce bon monde // - parcelles,
  // sous-parcelles route sans z, zonage, les bordures etc...
  env.setTerrain(dtm);
  try {
    AssignZ.toParcelle(env.getCadastralParcels(), dtm, SURSAMPLED);
    AssignZ.toSousParcelle(env.getSubParcels(), dtm, SURSAMPLED);
    AssignZ.toVoirie(env.getRoads(), dtm, SURSAMPLED);
    AssignZ.toPrescriptions(env.getPrescriptions(), dtm, SURSAMPLED);
    AssignZ.toZone(env.getUrbaZones(), dtm, false);
  } catch (Exception e) {
    e.printStackTrace();
  }

  logger.info("Loading complete");

  return env;
}
```

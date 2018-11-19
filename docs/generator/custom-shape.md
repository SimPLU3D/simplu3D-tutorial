---
title: Générateur de formes - Générer d'autres types de formes
authors:
    - Mickaël Brasebin
date: 2018-10-26

---


Main de fr.ign.simplu3d.shapeGenerator.OptimisedLShapeDirectRejection


```JAVA
// Step 0 ; Defining an output existing folder
		String outputFolder = "/tmp/";

		// Step 1 : Creating the geographic environment using the repository that
		// contains the data

		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(new File(
				BasicParametricShapeSimulator.class.getClassLoader().getResource("firstSimulation/data/").getPath()));

		// Select a parcel on which generation is proceeded
		BasicPropertyUnit bPU = env.getBpU().get(2);

		// Step 2 : Defining the regulation that will be applied during the simulation

		// Rules parameters.8
		// Distance to road
		double distReculVoirie = 0;
		// Distance to bottom of the parcel
		double distReculFond = 0;
		// Distance to lateral parcel limits
		double distReculLat = 0;
		// Distance between two buildings of a parcel
		double distanceInterBati = 3;
		// Maximal ratio built area
		double maximalCES = 1;

		// Instanciation of a predicate class
		// Same as in the first sample fr.ign.simplu3d.firstSimulation.BasicSimulator
		// As LBuildingWithRoof and Cuboid extends ISimPLU3DPrimitive
		SamplePredicate<LBuildingWithRoof, GraphConfiguration<LBuildingWithRoof>, BirthDeathModification<LBuildingWithRoof>> pred = new SamplePredicate<>(
				bPU, distReculVoirie, distReculFond, distReculLat, distanceInterBati, maximalCES);

		// Step 3 : Defining the sampler that will be applied during the simulation
		// Instantiation of the sampler
		OptimisedLShapeDirectRejection optimisedLShapedSampler = new OptimisedLShapeDirectRejection();

		// Loading the parameters for the building shape generation
		String folderName = BasicParametricShapeSimulator.class.getClassLoader()
				.getResource("firstSimulation/scenario/").getPath();
		// We use a specific scenario dedicated to LShape
		String fileName = "paramsLShape.json";
		SimpluParameters p = new SimpluParametersJSON(new File(folderName + fileName));

		// Run of the optimisation on a parcel with the predicate
		GraphConfiguration<? extends ISimPLU3DPrimitive> cc = optimisedLShapedSampler.process(bPU, p, env, bPU.getId(),
				pred, bPU.getGeom());

		// 4 - Writing the output
		IFeatureCollection<IFeature> iFeatC = new FT_FeatureCollection<>();
		for (GraphVertex<? extends ISimPLU3DPrimitive> v : cc.getGraph().vertexSet()) {

			IFeature feat = new DefaultFeature(v.getValue().generated3DGeom());
			// On ajoute des attributs aux entités (dimension des objets)
			AttributeManager.addAttribute(feat, "Info", v.getValue().toString(), "Double");

			iFeatC.add(feat);

		}

		// Writng the shapefile from the collection
ShapefileWriter.write(iFeatC, outputFolder + "out.shp");
```

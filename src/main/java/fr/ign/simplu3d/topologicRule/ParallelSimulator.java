package fr.ign.simplu3d.topologicRule;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.io.shapefile.SaveGeneratedObjects;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.model.ParcelBoundaryType;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.geometry.impl.Cuboid;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.optimizer.paralellcuboid.ParallelCuboidOptimizer;
import fr.ign.cogit.simplu3d.rjmcmc.generic.predicate.SamplePredicate;
import fr.ign.cogit.simplu3d.util.SimpluParameters;
import fr.ign.cogit.simplu3d.util.SimpluParametersJSON;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.random.Random;
import fr.ign.simplu3d.firstSimulation.BasicSimulator;

public class ParallelSimulator {

	public static void main(String[] args) throws Exception {

		// Step 0 ; Defining an output existing folder
		String outputFolder = "/tmp/";

		// Step 1 : Creating the geographic environnement using the repository that
		// contains the data

		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(
				new File(BasicSimulator.class.getClassLoader().getResource("firstSimulation/data/").getPath()));

		// Select a parcel on which generation is proceeded
		BasicPropertyUnit bPU = env.getBpU().get(2);

		// Step 2 : Defining the regulation that will be applied during the simulation

		// Distance to road

		// THE VALUE IS ZERO AS WE WANT CUBOID ALIGNED TO
		// LIMITS THAT TOUCH THE rOAD
		double distReculVoirie = 0;

		// Distance to bottom of the parcel
		double distReculFond = 3;
		// Distance to lateral parcel limits
		double distReculLat = 4;
		// Distance between two buildings of a parcel
		double distanceInterBati = 0;
		// Maximal ratio built area
		double maximalCES = 0.5;

		// Instanciation of a predicate class
		SamplePredicate<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> pred = new SamplePredicate<>(
				bPU, distReculVoirie, distReculFond, distReculLat, distanceInterBati, maximalCES);

		// Step 3 : Defining the sampler that will be applied during the simulation

		// Instantiation of the sampler

		// Loading the parameters for the building shape generation
		String folderName = BasicSimulator.class.getClassLoader().getResource("firstSimulation/scenario/").getPath();
		String fileName = "params.json";
		SimpluParameters p = new SimpluParametersJSON(new File(folderName + fileName));

		// NEW HERE THE PARALELLE OPTIMOIZER IS USED
		ParallelCuboidOptimizer oCB = new ParallelCuboidOptimizer();

		// IT REQUIRES AS INPUT THE GEOMETRY WHERE THE CUBOIDS HAVE TO BE ALIGNED
		IGeometry[] limits = createRoadLimits(bPU);

		// Run of the optimisation on a parcel with the predicate
		GraphConfiguration<Cuboid> cc = oCB.process(Random.random(), bPU, p, env, 0, pred, limits, bPU.getGeom());

		// 4 - Writing the output
		SaveGeneratedObjects.saveShapefile(outputFolder + "out.shp", cc, bPU.getId(), 0);

	}

	/**
	 * 
	 * @param bPU a basic property unit
	 * @return retrieve the geometry of roads limit
	 */
	private static IGeometry[] createRoadLimits(BasicPropertyUnit bPU) {
		// We know that there is only one cadastral aprcel for the BPU
		List<IGeometry> lBoundary = bPU.getCadastralParcels().get(0).getBoundariesByType(ParcelBoundaryType.ROAD)
				.stream().map(x -> x.getGeom()).collect(Collectors.toList());

		// Converting to Array
		IGeometry[] geometryArray = new IGeometry[lBoundary.size()];
		for (int i = 0; i < lBoundary.size(); i++) {
			geometryArray[i] = lBoundary.get(i);
		}

		return geometryArray;

	}

}

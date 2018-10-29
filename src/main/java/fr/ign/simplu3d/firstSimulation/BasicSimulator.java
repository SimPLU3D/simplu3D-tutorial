package fr.ign.simplu3d.firstSimulation;

import java.io.File;

import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.io.shapefile.SaveGeneratedObjects;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.geometry.impl.Cuboid;
import fr.ign.cogit.simplu3d.rjmcmc.cuboid.optimizer.cuboid.OptimisedBuildingsCuboidFinalDirectRejection;
import fr.ign.cogit.simplu3d.rjmcmc.generic.predicate.SamplePredicate;
import fr.ign.cogit.simplu3d.util.SimpluParameters;
import fr.ign.cogit.simplu3d.util.SimpluParametersJSON;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.0
 * 
 *          Very simple and first simulator using SimPLU3D
 * 
 * 
 */
public class BasicSimulator {

	public static void main(String[] args) throws Exception {
		
		//Step 0 ; Defining an output existing folder
		String outputFolder = "/tmp/";

		// Step 1 : Creating the geographic environnement using the repository that contains the data
		
		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(
				new File(BasicSimulator.class.getClassLoader().getResource("firstSimulation/data/").getPath()));
		
		// Select a parcel on which generation is proceeded
		BasicPropertyUnit bPU = env.getBpU().get(2);

		
		
		//Step 2 : Defining the regulation that will be applied during the simulation
		
		// Rules parameters.8
		// Distance to road
		double distReculVoirie = 2;
		// Distance to bottom of the parcel
		double distReculFond = 3;
		// Distance to lateral parcel limits
		double distReculLat = 4;
		// Distance between two buildings of a parcel
		double distanceInterBati = 0;
		// Maximal ratio built area
		double maximalCES = 0.5;

		//Instanciation of a predicate class
		SamplePredicate<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> pred = new SamplePredicate<>(
				bPU, distReculVoirie, distReculFond, distReculLat, distanceInterBati, maximalCES);
		
		
		
		//Step 3 : Defining the regulation that will be applied during the simulation


		// Instantiation of the sampler
		OptimisedBuildingsCuboidFinalDirectRejection oCB = new OptimisedBuildingsCuboidFinalDirectRejection();


		// Loading the parameters for the building shape generation
		String folderName = BasicSimulator.class.getClassLoader().getResource("firstSimulation/scenario/").getPath();
		String fileName = "params.json";
		SimpluParameters p = new SimpluParametersJSON(new File(folderName + fileName));
	

		// Run of the optimisation on a parcel with the predicate
		GraphConfiguration<Cuboid> cc = oCB.process(bPU, p, env, 1, pred);
		
		//4 - Writting the output
		SaveGeneratedObjects.saveShapefile(outputFolder + "out.shp", cc, bPU.getId(), 0);

	}

}

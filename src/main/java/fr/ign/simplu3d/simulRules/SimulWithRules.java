package fr.ign.simplu3d.simulRules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.io.shapefile.SaveGeneratedObjects;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.CadastralParcel;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.model.IZoneRegulation;
import fr.ign.cogit.simplu3d.model.UrbaZone;
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
 * see <http://www.cecill.info/
 * 
 * 
 * 
 * copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.2
 * 
 *          Very simple and first simulator using SimPLU3D
 * 
 * 
 */
public class SimulWithRules {

	public static void main(String[] args) throws Exception {

		// Step 0 ; Defining an output existing folder
		String outputFolder = "/tmp/";

		// Step 1 : Creating the geographic environnement using the repository that
		// contains the data
		String dataFolder = SimulWithRules.class.getClassLoader().getResource("simulationWithRules/data/").getPath();
		// Load default environment (data are in resource directory)
		Environnement env = LoaderSHP.loadNoDTM(new File(dataFolder));

		// Step 2 : loading the regulation and creating the link betweent urbazone and
		// regulation
		readAndAssociateRules(dataFolder + "rules.csv", env);

		// We apply the simulation on the bPU that are simulatble
		for (BasicPropertyUnit bPU : env.getBpU()) {

			// One parcel per bPU
			CadastralParcel currentCadastralParcel = bPU.getCadastralParcels().get(0);

			if (!currentCadastralParcel.hasToBeSimulated()) {
				continue;
			}

			// We get the regulation from the first SubParcel
			IZoneRegulation regulation = currentCadastralParcel.getSubParcels().get(0).getUrbaZone()
					.getZoneRegulation();

			if (regulation == null) {
				continue;
			}

			// We make a cast as we know that the object is from this class
			RegulationTuto regulationTuto = (RegulationTuto) regulation;

			// We do the usual job of simulating with the SamplePredicate

			// Step 3 : Defining the regulation that will be applied during the simulation

			// Rules parameters.8
			// Distance to road
			double distReculVoirie = regulationTuto.getDistReculVoirie();
			// Distance to bottom of the parcel
			double distReculFond = regulationTuto.getDistReculFond();
			// Distance to lateral parcel limits
			double distReculLat = regulationTuto.getDistReculLat();
			// Distance between two buildings of a parcel
			double distanceInterBati = regulationTuto.getDistanceInterBati();
			// Maximal ratio built area
			double maximalCES = regulationTuto.getMaximalCES();

			// Instanciation of a predicate class
			SamplePredicate<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> pred = new SamplePredicate<>(
					bPU, distReculVoirie, distReculFond, distReculLat, distanceInterBati, maximalCES);

			// Step 4 : Defining the sampler that will be applied during the simulation

			// Instantiation of the sampler
			OptimisedBuildingsCuboidFinalDirectRejection oCB = new OptimisedBuildingsCuboidFinalDirectRejection();

			// Loading the parameters for the building shape generation
			String folderName = SimulWithRules.class.getClassLoader().getResource("firstSimulation/scenario/")
					.getPath();
			String fileName = "params.json";
			SimpluParameters p = new SimpluParametersJSON(new File(folderName + fileName));

			// Run of the optimisation on a parcel with the predicate
			GraphConfiguration<Cuboid> cc = oCB.process(bPU, p, env, 1, pred);

			// Step 5 - Writing the output
			SaveGeneratedObjects.saveShapefile(outputFolder + "out_" + currentCadastralParcel.getCode() + ".shp", cc,
					bPU.getId(), 0);

		}

	}

	public static void readAndAssociateRules(String ruleFile, Environnement env) throws Exception {
		// Creation of a map to create a link between id regulation and regulation
		Map<String, RegulationTuto> mapLibelleRegulation = new HashMap<>();

		////////////////////
		//// READING THE CSV FILE to complete the map
		////////////////////

		BufferedReader in = new BufferedReader(new FileReader(new File(ruleFile)));

		// The first line is the head
		in.readLine();
		String line = "";
		// For each line
		while ((line = in.readLine()) != null) {

			// We create a new regulation
			RegulationTuto r = new RegulationTuto(line);
			// We add it to the map with it is name
			if (r != null) {
				mapLibelleRegulation.put(r.getLibelle(), r);
			}

		}

		in.close();

		////////////////////
		//// ASSOCIATING UrbaZone and regulation
		////////////////////

		for (UrbaZone zone : env.getUrbaZones()) {
			// We use the libelle to make a join between regulation and zone
			RegulationTuto regulation = mapLibelleRegulation.get(zone.getLibelle());

			// We create the link in the two directions for convenience
			zone.setZoneRegulation(regulation);
			if (regulation != null) {
				regulation.setUrbaZone(zone);

			}

		}

	}

}

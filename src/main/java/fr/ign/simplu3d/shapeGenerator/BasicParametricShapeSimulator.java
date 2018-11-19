package fr.ign.simplu3d.shapeGenerator;

import java.io.File;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.BasicPropertyUnit;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.rjmcmc.generic.object.ISimPLU3DPrimitive;
import fr.ign.cogit.simplu3d.rjmcmc.generic.predicate.SamplePredicate;
import fr.ign.cogit.simplu3d.rjmcmc.paramshp.geometry.impl.LBuildingWithRoof;
import fr.ign.cogit.simplu3d.rjmcmc.paramshp.optimizer.OptimisedLShapeDirectRejection;
import fr.ign.cogit.simplu3d.util.SimpluParameters;
import fr.ign.cogit.simplu3d.util.SimpluParametersJSON;
import fr.ign.mpp.configuration.BirthDeathModification;
import fr.ign.mpp.configuration.GraphConfiguration;
import fr.ign.mpp.configuration.GraphVertex;

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
 *          Simulateur standard
 * 
 * 
 */
public class BasicParametricShapeSimulator {

	public static void main(String[] args) throws Exception {

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

	}

}

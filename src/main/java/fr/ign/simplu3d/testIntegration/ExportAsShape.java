package fr.ign.simplu3d.testIntegration;

import java.io.File;

import fr.ign.cogit.simplu3d.io.export.ExportInstance;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.simplu3d.firstSimulation.BasicSimulator;

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
 * @author mbrasebin
 * 
 *         This class aims at exporting the instances from the SimPLU3D model in
 *         order to check how it has beeen instanciated (as there is currently
 *         no persistance mechanism)
 *
 */
public class ExportAsShape {

	public static void main(String[] args) throws Exception {

		// Step 0 ; Defining an output existing folder
		String outputFolder = "/tmp/tmp/";

		// Load THE COMPLETE environment (data are in resource directory).
		// One difference with the firstSimulation is that there is a DTM in the folder
		// You can use the load (instead of loadNoDTM method) but you need to pass the
		// following
		// option to the VM argument as Java3D is currently needed to read DTM) :
		//
		// -Djava.library.path=./lib/native_libraries/linux-amd64 (adjust it according
		// to the system)
		//
		// or replace the code by the following line :
		//
		// Environnement env = LoaderSHP.loadNoDTM(new
		// File(BasicSimulator.class.getClassLoader().getResource("completeDataSet/data/").getPath()));
		Environnement env = LoaderSHP
				.load(new File(BasicSimulator.class.getClassLoader().getResource("completeDataSet/data/").getPath()));

		// Export the environnement into a set of shapefile in the folder outputFolder
		ExportInstance.export(env, outputFolder);

	}

}

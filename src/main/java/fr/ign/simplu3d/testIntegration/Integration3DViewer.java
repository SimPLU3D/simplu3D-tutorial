package fr.ign.simplu3d.testIntegration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object1d;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.simplu3d.io.nonStructDatabase.shp.LoaderSHP;
import fr.ign.cogit.simplu3d.model.Environnement;
import fr.ign.cogit.simplu3d.representation.RepEnvironnement;
import fr.ign.cogit.simplu3d.representation.RepEnvironnement.Theme;
import fr.ign.simplu3d.firstSimulation.BasicSimulator;

/**
 * 
 * Class to visualize a 3D Environnement
 * 
 * @author mbrasebin
 *
 */
public class Integration3DViewer {
	
	
	/**
	 * Permet de représenter les données par rapport à des thèmes sélectionnés. Ces thèmes sont disponibles dans l'énumérateur Theme de la classe RepEnvironnement
	 * 
	 * Il est indispensable d'ajouter le paramètre suivant dans la VM (afin de premetter d'utiliser le viewer 3D) : -Djava.library.path=./lib/native_libraries/linux-amd64 
	 * Le nom du dossier est à moduler en fonction de l'OS utilisé.
	 * 
	 * @param args non utilisé
	 * @throws Exception exceptions possibles sur le chargement des données
	 */
	public static void main(String[] args) throws Exception {

		// Size of sphere that represents ponctual object
		Object1d.width = 4.0f;

		// The name where data is stored
		String folderName = BasicSimulator.class.getClassLoader().getResource("completeDataSet/data/").getPath();

		// Loading the environement with DTM
		Environnement env = LoaderSHP.load(new File(folderName));

		// Adding the theme to represent
		List<Theme> lTheme = new ArrayList<RepEnvironnement.Theme>();
		lTheme.add(Theme.TOIT_BATIMENT);
		lTheme.add(Theme.FACADE_BATIMENT);
		lTheme.add(Theme.FAITAGE);
		lTheme.add(Theme.PIGNON);
		lTheme.add(Theme.GOUTTIERE);
		lTheme.add(Theme.VOIRIE);
		lTheme.add(Theme.PARCELLE);
		lTheme.add(Theme.SOUS_PARCELLE);
		lTheme.add(Theme.PAN);
		lTheme.add(Theme.PAN_MUR);
		lTheme.add(Theme.BORDURE);

		// Turning data into a list of representable vector layers for GeOxygene 3D
		List<VectorLayer> vl = RepEnvironnement.represent(env, lTheme);

		// Creating a GeOxygene3D window
		MainWindow mW = new MainWindow();

		// Adding each theme to the window
		for (VectorLayer l : vl) {

			mW.getInterfaceMap3D().getCurrent3DMap().addLayer(l);
		}

	}

}

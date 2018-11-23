package fr.ign.simplu3d.simulRules;

import fr.ign.cogit.simplu3d.model.IZoneRegulation;
import fr.ign.cogit.simplu3d.model.UrbaZone;

public class RegulationTuto implements IZoneRegulation {

	
	//Name of the zone that corresponds to the regulation
	String libelle;
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

	public static String SEPARATIVE_CHARACTER = ";";

	private UrbaZone urbaZone = null;

	public RegulationTuto(String libelle, double distReculVoirie, double distReculFond, double distReculLat, double distanceInterBati,
			double maximalCES) {
		super();
	
		this.libelle = libelle;
		this.distReculVoirie = distReculVoirie;
		this.distReculFond = distReculFond;
		this.distReculLat = distReculLat;
		this.distanceInterBati = distanceInterBati;
		this.maximalCES = maximalCES;
	}



	// Parse the string
	public RegulationTuto(String s) {

		String[] splittedLine = s.split(SEPARATIVE_CHARACTER);
		this.libelle =splittedLine[0];
		this.distReculVoirie = Double.parseDouble(splittedLine[1]);
		this.distReculFond = Double.parseDouble(splittedLine[2]);
		this.distReculLat = Double.parseDouble(splittedLine[3]);
		this.distanceInterBati = Double.parseDouble(splittedLine[4]);
		this.maximalCES = Double.parseDouble(splittedLine[5]);

	}

	public String getLibelle() {
		return libelle;
	}
	
	
	public double getDistReculVoirie() {
		return distReculVoirie;
	}

	public double getDistReculFond() {
		return distReculFond;
	}

	public double getDistReculLat() {
		return distReculLat;
	}

	public double getDistanceInterBati() {
		return distanceInterBati;
	}

	public double getMaximalCES() {
		return maximalCES;
	}

	@Override
	public UrbaZone getUrbaZone() {
		return urbaZone;
	}

	public void setUrbaZone(UrbaZone urbaZone) {
		this.urbaZone = urbaZone;
	}

	@Override
	public String toString() {
		return "RegulationTuto [distReculVoirie=" + distReculVoirie + ", distReculFond=" + distReculFond
				+ ", distReculLat=" + distReculLat + ", distanceInterBati=" + distanceInterBati + ", maximalCES="
				+ maximalCES + ", urbaZone=" + urbaZone + "]";
	}

	@Override
	public String toText() {
		return this.toString();
	}

}

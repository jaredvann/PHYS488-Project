//This file has several checks of individual methods to see if they return sensible results.
import java.io.*;
import java.text.DecimalFormat;

class TestAttenuator {
	static PrintWriter screen = new PrintWriter(System.out, true);
	static DecimalFormat df = new DecimalFormat("0.000");
	static DecimalFormat sf = new DecimalFormat("0.00E00");

	static int ironAtomicNumber = 26;
	static double ironMassNumber = 55.85;
	static double ironDensity = 7.87;

	public static void main(String[] args) {

		Attenuator iron = new Attenuator(ironAtomicNumber, ironMassNumber, ironDensity, 1);
		double energyLoss, theta0;

		screen.println("Muon energy loss desk check using given values:");
		screen.println("Muon Momentum (MeV/c) \tActual E Loss (MeV) \tCalculated E Loss (MeV)");

		energyLoss = iron.getEnergyLoss(30.0, Helpers.MASS_MUON);
		screen.println(30 + "\t\t\t" + 81.565 + "\t\t\t" + df.format(energyLoss));

		energyLoss = iron.getEnergyLoss(300.0, Helpers.MASS_MUON);
		screen.println(300 + "\t\t\t" + 11.599 + "\t\t\t" + df.format(energyLoss));

		energyLoss = iron.getEnergyLoss(3000.0, Helpers.MASS_MUON);
		screen.println(3000 + "\t\t\t" + 15.388 + "\t\t\t" + df.format(energyLoss));

		energyLoss = iron.getEnergyLoss(10000.0, Helpers.MASS_MUON);
		screen.println(10000 + "\t\t\t" + 18.078 + "\t\t\t" + df.format(energyLoss));

		energyLoss = iron.getEnergyLoss(30000.0, Helpers.MASS_MUON);
		screen.println(30000 + "\t\t\t" + 20.547 + "\t\t\t" + df.format(energyLoss));

		energyLoss = iron.getEnergyLoss(100000.0, Helpers.MASS_MUON);
		screen.println(100000 + "\t\t\t" + 23.255 + "\t\t\t" + df.format(energyLoss));

		screen.println("");

		screen.println("Muon theta0 desk check using given values:");
		screen.println("Muon Momentum (MeV/c) \tActual theta0 (rads) \tCalculated theta0 (rads)");

		theta0 = iron.getMCSTheta0(500.0, Helpers.MASS_MUON);
		screen.println(500 + "\t\t\t0.020\t\t\t" + df.format(theta0));

		theta0 = iron.getMCSTheta0(1000.0, Helpers.MASS_MUON);
		screen.println(1000 + "\t\t\t9.98E-03\t\t" + sf.format(theta0));

		theta0 = iron.getMCSTheta0(3000.0, Helpers.MASS_MUON);
		screen.println(3000 + "\t\t\t3.31E-03\t\t" + sf.format(theta0));

	}
}

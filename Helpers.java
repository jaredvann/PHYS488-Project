import java.io.*;
import java.util.ArrayList;
import java.util.Random;


class Helpers {
    private static Random rng = new Random();

    public static final double MASS_MUON = 106; // MeV
    public static final double MASS_ELECTRON = 0.511; // MeV

    public static final int CHARGE_ELECTRON = 1; // 1.60E-19 C

    public static double beta(double mass, double momentum) {
        return momentum / energy(mass, momentum);
    }

    public static double gamma(double mass, double momentum) {
        double b = beta(mass, momentum);
        return 1 / Math.sqrt(1 - b*b);
    }

    public static double energy(double mass, double momentum) {
        return Math.sqrt(mass*mass + momentum*momentum);
    }

    public static double momentum(double mass, double energy) {
        return Math.sqrt(energy*energy - mass*mass);
    }

    public static double gauss(double xmean, double sigma){
        // Return a random number with a gaussian distribution
        double sum = 0;

        for (int n=0; n<=11; n++)
            sum += rng.nextDouble(); // use the class Random to make a number

        return xmean + sigma*(sum-6);
    }

    public static boolean write_to_disk(String filepath, double[][] data) throws IOException {
        return write_to_disk(filepath, data, "");
    }

    public static boolean write_to_disk(String filepath, double[][] data, String header) throws IOException {
        File dir;
        FileWriter file;
        PrintWriter toFile = null;

        try {
            dir = new File("./data");
            if (!dir.exists())
                dir.mkdir();

            file = new FileWriter(filepath); // File stream
            toFile = new PrintWriter(file); // File writer

            if (header != "")
                toFile.print(header + "\n,\n");

            for (double[] line : data) {
                for (double item : line)
                    toFile.print(item + ",");

                if (line.length > 0) toFile.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            if (toFile != null)
                toFile.close();
        }

        return true;
    }

    public static ArrayList<double[]> read_CSV(String filepath) {
        ArrayList<double[]> data = new ArrayList<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            String line;

            String[] cols;
            double[] dCols;

            while ((line = br.readLine()) != null) {
                cols = line.split(",");
                dCols = new double[cols.length];

                for (int i = 0; i < cols.length; i++)
                    dCols[i] = Double.parseDouble(cols[i]);

                data.add(dCols);
            }
        } catch (IOException e) { e.printStackTrace();
        } finally {
            if (br != null)
                try { br.close(); }
                catch (IOException e) { e.printStackTrace(); }
        }

        return data;
    }
}

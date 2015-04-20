// Import statements
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

class Analyse {
    private static Config config;

    public static void main(String[] args) throws IOException {
        Simulation.main(null);

        analyse();
    }

    public static void analyse() throws IOException {
        config = new Config("config.properties");

        ArrayList<double[]> data = readCsv("./data.csv");

        int count = 0;
        int estCount = 0;
        double mom = config.getDouble("momentum");

        for (double[] row : data) {
            if (row[2] >= mom)
                count += 1;

            if (row[4] >= mom)
                estCount += 1;
        }

        System.out.println("[*] Minimum Momentum: " + mom + "\n");
        System.out.println("[!] Count: " + count);
        System.out.println("[!] Estimated Count: " + estCount + "\n");
        System.out.println("[X] Efficiency: " + (estCount*100 / count) + "%");
    }

    private static ArrayList<double[]> readCsv(String filepath) {
        ArrayList<double[]> data = new ArrayList<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            String line = null;

            String[] cols;
            double[] dCols;

            while ((line = br.readLine()) != null) {
                cols = line.split(",");
                dCols = new double[cols.length];

                for (int i = 0; i < cols.length; i++)
                    dCols[i] = Double.parseDouble(cols[i]);

                data.add(dCols);
            }
        } catch (FileNotFoundException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace();
        } finally {
            if (br != null)
                try { br.close(); }
                catch (IOException e) { e.printStackTrace(); }
        }

        return data;
    }
}

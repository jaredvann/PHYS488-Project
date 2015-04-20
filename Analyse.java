// Import statements
import java.util.ArrayList;
import java.io.IOException;

class Analyse {
    private static Config config;

    public static void main(String[] args) throws IOException {
        config = new Config("config.properties");

        ArrayList<double[]> data = Helpers.read_CSV("./data.csv");

        int count = 0;
        int estCount = 0;
        double mom = config.getDouble("high_momentum_limit");

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
}

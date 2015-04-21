// Import statements
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class Config {
    private static Properties config = new Properties();

    public int numParticles;
    public double[] masses;

    public double magField;

    public double momentum;
    public double momentumSmear;
    public double momentumLimit;

    public double triggerRadiusA;
    public double triggerRadiusB;
    public double triggerThickness;
    public double triggerResolution;

    public Config(String fp) throws IOException {
        try {
            FileInputStream file = new FileInputStream(new File("./"+fp));
            config.load(file);

            init();

            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handlers

    public void init() {
        numParticles = (hasKey("num_particles")) ?
            getInt("num_particles") : 10000;

        masses = (hasKey("masses")) ?
            getDoubles("masses") : new double[106];

        magField = (hasKey("mag_field")) ?
            getDouble("mag_field") : 4;

        momentum = (hasKey("momentum")) ?
            getDouble("momentum") : 50000;
        momentumSmear = (hasKey("momentum_smear")) ?
            getDouble("momentum_smear") : 0.5;
        momentumLimit = (hasKey("momentum_limit")) ?
            getDouble("momentum_limit") : 50000;

        triggerRadiusA = (hasKey("cd_radius_a")) ?
            getDouble("cd_radius_a") : 90;
        triggerRadiusB = (hasKey("cd_radius_b")) ?
            getDouble("cd_radius_b") : 91;
        triggerThickness = (hasKey("cd_thickness")) ?
            getDouble("cd_thickness") : 0.05;
        triggerResolution = (hasKey("cd_resolution")) ?
            getDouble("cd_resolution") : 0.00005;
    }

    // Helpers

    public boolean hasKey(String key) {
        return config.stringPropertyNames().contains(key);
    }

    public String get(String key) {
        return config.getProperty(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(get(key));
    }

    public double[] getDoubles(String key) {
        String s = get(key);
        String[] ls = s.split(",");
        double[] doubles = new double[ls.length];

        for (int i = 0; i < doubles.length; i++)
            doubles[i] = Double.parseDouble(ls[i]);

        return doubles;
    }
}

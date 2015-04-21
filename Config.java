// Import statements
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class Config {
    private static Properties config = new Properties();

    public Config(String fp) throws IOException {
        try {
            FileInputStream file = new FileInputStream(new File("./"+fp));
            config.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

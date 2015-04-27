// Import statements
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties config = new Properties();

    public Config(String filepath) throws IOException {
        try {
            FileInputStream file = new FileInputStream(new File("./"+filepath));
            config.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) { return config.getProperty(key); }
    public int getInt(String key) { return Integer.parseInt(get(key)); }
    public double getDouble(String key) { return Double.parseDouble(get(key)); }
}

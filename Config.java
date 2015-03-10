import java.util.Properties;
import java.io.*;

/** Class that is used to import configuration files
  * See TestConfig.java for example on how to use */
class Config {

	String filepath;

	static Properties config = new Properties();

	public Config(String fp) throws IOException {
		filepath = "config/" + fp + ".properties";
		FileInputStream file = new FileInputStream(new File(filepath));
		config.load(file);
		file.close();
	}

	public String get(String key) {
		return config.getProperty(key);
	}

	public int getInt(String key) {
		return Integer.parseInt(config.getProperty(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(config.getProperty(key));
	}
}

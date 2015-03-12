import java.util.Properties;
import java.io.*;

class Config {

	static Properties config = new Properties();

	public Config(String fp) throws IOException {
		fp = "config/" + fp + ".properties";
		FileInputStream file = new FileInputStream(new File(fp));
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

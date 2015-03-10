import java.io.*;

class TestConfig {

	public static void main(String[] args) throws IOException {
		// Create config instance where 'test' is the name of the config file
		Config config = new Config("test");

		// Get either strings, ints or doubles from the config file,
		// where 'testString'... are the variable names
		System.out.println(config.get("testString"));
		System.out.println(config.getInt("testInt"));
		System.out.println(config.getDouble("testDouble"));
	}
}

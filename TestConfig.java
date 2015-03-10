import java.io.*;

class TestConfig {

	public static void main(String[] args) throws IOException {
		Config config = new Config("test");

		System.out.println(config.get("testString"));
		System.out.println(config.get("testInt"));
		System.out.println(config.get("testDouble"));
	}
}

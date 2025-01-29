package Utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src/test/resources/config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file.", e);
        }
    }

    public static String get(String key) {
        String envValue = System.getenv(key);
        if (envValue != null) {
            return envValue;
        }

        return properties.getProperty(key);
    }
}

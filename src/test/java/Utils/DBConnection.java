package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;
    static {
        try {
            checkRequiredVars("DB_URL", "DB_USERNAME", "DB_PASSWORD", "DB_DRIVER");
            Class.forName(ConfigReader.get("DB_DRIVER"));
            connection = DriverManager.getConnection(
                    ConfigReader.get("DB_URL"),
                    ConfigReader.get("DB_USERNAME"),
                    ConfigReader.get("DB_PASSWORD")
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to connect to the database.", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
    public static void checkRequiredVars(String... keys) {
        for (String key : keys) {
            if (ConfigReader.get(key) == null) {
                throw new RuntimeException("Missing required database variable: " + key);
            }
        }
    }

}
package util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DBUtil V2 - Optimized with HikariCP Connection Pooling
 */
public class DBUtil {

    private static final Logger logger = Logger.getLogger(DBUtil.class.getName());
    private static HikariDataSource dataSource;

    static {
        try {
            Properties properties = new Properties();
            InputStream input = null;

            // 1. Thử đọc từ classpath
            input = DBUtil.class.getClassLoader().getResourceAsStream("config.properties");

            // 2. Nếu không tìm thấy, thử đọc trực tiếp từ src/resources
            if (input == null) {
                String path = "src/resources/config.properties";
                try {
                    input = new FileInputStream(path);
                    logger.info("Loaded config.properties from " + path);
                } catch (IOException e) {
                    logger.warning("config.properties not found in src/resources, will use environment variables");
                }
            }

            // 3. Load properties nếu tìm thấy file
            if (input != null) {
                properties.load(input);
            } else {
                // fallback: environment variables
                properties.setProperty("db.url", System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/pos_db"));
                properties.setProperty("db.user", System.getenv().getOrDefault("DB_USER", "root"));
                properties.setProperty("db.password", System.getenv().getOrDefault("DB_PASS", ""));
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.user"));
            config.setPassword(properties.getProperty("db.password"));

            // Cấu hình HikariCP
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(10000);
            config.setLeakDetectionThreshold(60000);

            dataSource = new HikariDataSource(config);
            logger.info("✅ HikariCP Connection Pool initialized successfully");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load configuration", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize HikariCP pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to close resource", e);
                }
            }
        }
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            logger.info("Connection pool has been shut down.");
        }
    }
}

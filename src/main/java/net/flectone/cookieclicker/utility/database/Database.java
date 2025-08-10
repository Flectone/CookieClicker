package net.flectone.cookieclicker.utility.database;

import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
public class Database {

    private HikariDataSource dataSource;

    private void init() {
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS 'users' (uuid TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, itemframe_clicks INTEGER NOT NULL DEFAULT 0, lvl INTEGER NOT NULL DEFAULT 0, remaining_xp INTEGER NOT NULL DEFAULT 1500)");
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect(Path projectPath) throws SQLException {
        HikariConfig hikariConfig = createHikaryConfig(projectPath);

        try {
            dataSource = new HikariDataSource(hikariConfig);
        } catch (HikariPool.PoolInitializationException e) {
            throw new RuntimeException(e);
        }

        try (Connection ignored = getConnection()){
            init();
        }
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("Not initialized");

        return dataSource.getConnection();
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.getHikariPoolMXBean().softEvictConnections();
            dataSource.close();
        }
    }

    private HikariConfig createHikaryConfig(Path projectPath) {
        HikariConfig hikariConfig = new HikariConfig();

        String connectionURL = "jdbc:sqlite:";

        connectionURL = connectionURL +
                projectPath.toString() +
                File.separator +
                "database.db";

        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.addDataSourceProperty("journal_mode", "WAL");
        hikariConfig.addDataSourceProperty("synchronous", "NORMAL");
        hikariConfig.addDataSourceProperty("journal_size_limit", "6144000");

        hikariConfig.setJdbcUrl(connectionURL);
        hikariConfig.setPoolName("CookieClickerDataBase");

        return hikariConfig;
    }
}

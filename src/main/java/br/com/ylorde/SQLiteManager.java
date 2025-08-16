package br.com.ylorde;

import br.com.ylorde.utils.Console;

import java.io.File;
import java.sql.*;

public class SQLiteManager {
    private final File databaseFile;
    public Connection connection;
    private Console console = new Console();

    public SQLiteManager(File dataFolder) {
        this.databaseFile = new File(dataFolder, "players.db");
        connect();
    }

    private void connect() {
        try {
            if (!databaseFile.exists()) {
                databaseFile.getParentFile().mkdir();
                databaseFile.createNewFile();
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/discordsync/players.db");

            console.sendColoredMessage("&a[DiscordSync::SQLITE] -> Conectado ao banco de dados.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null) connection.close();
            console.sendColoredMessage("&c[DiscordSunc::SQLITE] -> Conexão encerrada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setupTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "nickname TEXT,"
                    + "discord_id TEXT,"
                    + "random_code TEXT"
                    + ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

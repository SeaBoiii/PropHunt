package me.tomski.currency;

import me.tomski.enums.EconomyType;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;

import java.sql.*;

public class SqlConnect {

    private boolean enabled;
    private PropHunt plugin;
    private SqlSettings settings;

    public SqlConnect(PropHunt plugin) {
        this.plugin = plugin;
        this.settings = new SqlSettings(plugin);
        try {
            testConnection();
            enabled = true;
            ShopSettings.economyType = EconomyType.PROPHUNT;
        } catch (SQLException e) {
            plugin.getLogger().info("Sql not able to connect! Disabling Sql currency! STACK BELOW ;)");
            e.printStackTrace();

            enabled = false;
        }
    }


    private void testConnection() throws SQLException {
        System.out.println("CONNECTING TO " + settings.getConnector() + settings.getHost() + ":" + settings.getPort() + "/" + "   " + settings.getUsername() + " " + settings.getPass());
        Connection conn = DriverManager.getConnection(settings.getConnector() + settings.getHost() + ":" + settings.getPort() + "/", settings.getUsername(), settings.getPass()); //Creates the connection
        PreparedStatement sampleQueryStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + settings.getDatabase()); //gen new Database if required
        sampleQueryStatement.execute();
        sampleQueryStatement.executeUpdate("USE " + settings.getDatabase());
        sampleQueryStatement.executeUpdate("CREATE TABLE IF NOT EXISTS PropHuntCurrency (playerName VARCHAR(255) PRIMARY KEY," + "credits INT)");
        sampleQueryStatement.executeUpdate();
        sampleQueryStatement.close();
        conn.close();
    }

    public int getCredits(String playerName) {
        try {
            Connection conn = DriverManager.getConnection(settings.getUrl(), settings.getUsername(), settings.getPass()); //Creates the connection
            PreparedStatement findStatement;
            findStatement = conn.prepareStatement("SELECT * from PropHuntCurrency WHERE playerName=?");
            findStatement.setString(1, playerName);
            ResultSet rs = findStatement.executeQuery();
            int counter = 0;
            if (rs != null) {
                while (rs.next()) {
                    counter++;
                }
            }
            if (rs == null || counter == 0) {
                plugin.getLogger().info("Creating new Player file for: " + playerName);
                conn.close();
                setCredits(playerName, 0);
                return 0;
            } else if (counter > 1) {
                plugin.getLogger().info("Error with database! Multiple files with the same name");
            } else {
                rs.first();
                return rs.getInt(2);
            }
            conn.close();
        } catch (SQLException ex) {
            plugin.getLogger().info("" + ex);
        }
        return 0;
    }

    public void setCredits(String playerName, int amount) {
        try {
            Connection conn = DriverManager.getConnection(settings.getUrl(), settings.getUsername(), settings.getPass()); //Creates the connection
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO PropHuntCurrency (`playerName`, credits) " +
                    "VALUES ('" + playerName + "', " + amount + ")" +
                    " ON DUPLICATE KEY UPDATE playerName='" + playerName + "', credits=" + amount + "");
            conn.close();
        } catch (SQLException ex) {
            plugin.getLogger().info("" + ex);
        }

    }

}

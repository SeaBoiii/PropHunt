package me.tomski.currency;


import me.tomski.prophunt.PropHunt;

public class SqlSettings {

    private String username;
    private String host;
    private String pass;
    private String port;
    private String database;
    private String connector;

    private String url;

    private PropHunt plugin;

    public SqlSettings(PropHunt plugin) {
        this.plugin = plugin;
        connector = "jdbc:mysql://";
        loadSettings();
    }

    private void loadSettings() {
        username = plugin.getConfig().getString("DatabaseSettings.username");
        host = plugin.getConfig().getString("DatabaseSettings.host");
        port = plugin.getConfig().getString("DatabaseSettings.port");
        pass = plugin.getConfig().getString("DatabaseSettings.password");
        database = plugin.getConfig().getString("DatabaseSettings.database");
        url = connector + host + ":" + port + "/" + database;
    }


    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getConnector() {
        return connector;
    }

    public String getUrl() {
        return url;
    }
}

package me.tomski.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import me.tomski.prophunt.PropHunt;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ScoreboardTranslate {

    static FileConfiguration currentLanguageFile;
    private PropHunt plugin;

    private FileConfiguration translateConfig;
    private File customLanguageFile;

    public ScoreboardTranslate(PropHunt ph) throws IOException {
        this.plugin = ph;
        getTranslateFile().options().copyDefaults(true);
        saveTranslateFile();
        initTranslates();
    }


    public boolean usingTranslations = false;
    public String player_Translate = "Players";
    public String seeker_Translate = "Seekers";
    public String hider_Translate = "Hiders";
    public String spectator_Translate = "spectators";
    public String time_Left_Translate = "TimeLeft";
    public String starting_In_Translate = "Starting in";
    public String solid_Time_Translate = "SolidTime";
    public String solid_Translate = "SOLID";

    public Map<String, String> disguise_Translations = new HashMap<String, String>();

    public void initTranslates() {
        usingTranslations = getTranslateFile().getBoolean("use-translate");
        if (!usingTranslations) {
            plugin.getLogger().info("Using default scoreboard messages");
            return;
        }
        plugin.getLogger().info("Loading custom scoreboard messages");

        String key = "Translate-Words.";
        if (getTranslateFile().contains(key + "Players")) {
            player_Translate = getTranslateFile().getString(key + "Players");
        }
        if (getTranslateFile().contains(key + "Seekers")) {
            seeker_Translate = getTranslateFile().getString(key + "Seekers");
        }
        if (getTranslateFile().contains(key + "Hiders")) {
            hider_Translate = getTranslateFile().getString(key + "Hiders");
        }
        if (getTranslateFile().contains(key + "Spectators")) {
            spectator_Translate = getTranslateFile().getString(key + "Spectators");
        }
        if (getTranslateFile().contains(key + "Time-Left")) {
            time_Left_Translate = getTranslateFile().getString(key + "Time-Left");
        }
        if (getTranslateFile().contains(key + "Starting-In")) {
            starting_In_Translate = getTranslateFile().getString(key + "Starting-In");
        }
        if (getTranslateFile().contains(key + "Solid-Time")) {
            solid_Time_Translate = getTranslateFile().getString(key + "Solid-Time");
        }
        if (getTranslateFile().contains(key + "Solid")) {
            solid_Translate = getTranslateFile().getString(key + "Solid");
        }
        for (String keyy : getTranslateFile().getConfigurationSection("Disguise-Translations").getKeys(false)) {
            disguise_Translations.put(keyy, getTranslateFile().getString("Disguise-Translations."+keyy));
        }

    }


    public String getDisguiseTranslate(String matName) {
        if (!usingTranslations) {
            return matName;
        }
        if (disguise_Translations.containsKey(matName)) {
            return disguise_Translations.get(matName);
        } else {
            return matName;
        }
    }

    public void reloadTranslate() {
        if (customLanguageFile == null) {
            customLanguageFile = new File(plugin.getDataFolder(), "ScoreboardTranslate.yml");
        }
        translateConfig = YamlConfiguration.loadConfiguration(customLanguageFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource("ScoreboardTranslate.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(defConfigStream);
            translateConfig.setDefaults(defConfig);
        }
    }

    public FileConfiguration getTranslateFile() {
        if (translateConfig == null) {
            this.reloadTranslate();
        }
        return translateConfig;
    }

    public void saveTranslateFile() {
        if (translateConfig == null || customLanguageFile == null) {
            return;
        }
        try {
            getTranslateFile().save(customLanguageFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not save config to " + customLanguageFile, ex);
        }
    }

}

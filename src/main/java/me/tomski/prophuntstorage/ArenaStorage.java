package me.tomski.prophuntstorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import me.tomski.arenas.Arena;
import me.tomski.objects.ArenaFileStructureWrapper;
import me.tomski.objects.LocationBox;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.arenas.ArenaConfig;
import me.tomski.arenas.ArenaManager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class ArenaStorage {


    public FileConfiguration StorageFilef = null;
    private File customConfigFile = null;
    private PropHunt plugin;
    private GameManager GM;

    public ArenaStorage(PropHunt plugin, GameManager GM) {
        this.plugin = plugin;
        this.GM = GM;
    }


    public void loadData() {
        if (!getStorageFile().contains("Arenas")) {
            plugin.getLogger().log(Level.WARNING, "No arenas have been setup!");
            return;
        }
        for (String key : getStorageFile().getConfigurationSection("Arenas").getKeys(false)) {
            String path = "Arenas." + key + ".";
            Arena a;
            if (ArenaFileStructureWrapper.usingOldFormat(getStorageFile(), key)) {
                World world = plugin.getServer().getWorld(getStorageFile().getString(path + "worldname"));
                Location lobby = getStorageFile().getVector(path + "lobbyVec").toLocation(world);
                Location exit = getStorageFile().getVector(path + "exitVec").toLocation(world);
                Location seeker = getStorageFile().getVector(path + "seekerVec").toLocation(world);
                Location hider = getStorageFile().getVector(path + "hiderVec").toLocation(world);
                Location spec = getStorageFile().getVector(path + "spectatorVec").toLocation(world);
                a = new Arena(key, lobby, exit, seeker, hider, spec);
                ArenaManager.playableArenas.put(key, a);
                ArenaFileStructureWrapper.translateToNewStorageFormat(plugin, getStorageFile(), a);
                plugin.getLogger().log(Level.INFO, key + " arena loaded and translated to the new file format");
            } else {
                Location lobby = new LocationBox(getStorageFile().getString(path + "lobbySpawn")).unBox();
                Location seeker = new LocationBox(getStorageFile().getString(path + "seekerSpawn")).unBox();
                Location exit = new LocationBox(getStorageFile().getString(path + "exitSpawn")).unBox();
                Location hider = new LocationBox(getStorageFile().getString(path + "hiderSpawn")).unBox();
                Location spec = new LocationBox(getStorageFile().getString(path + "spectatorSpawn")).unBox();
                a = new Arena(key, lobby, exit, seeker, hider, spec);
                ArenaManager.playableArenas.put(key, a);
                plugin.getLogger().log(Level.INFO, key + " arena loaded");
            }
            if (!plugin.getConfig().contains("CustomArenaConfigs." + key)) {
                a.saveArenaToFile(plugin);
            }
            for (Arena ar : ArenaManager.playableArenas.values()) {
                ArenaManager.arenasInRotation.add(ar);
            }
            loadCustomSettings(a);
        }
    }

    private void loadCustomSettings(Arena a) {
        if (plugin.getConfig().getBoolean("CustomArenaConfigs." + a.getArenaName() + ".usingDefault")) {
            ArenaConfig AC = new ArenaConfig(DisguiseManager.blockDisguises, GameManager.hiderCLASS, GameManager.seekerCLASS, true);
            ArenaManager.arenaConfigs.put(a, AC);
            plugin.getLogger().log(Level.INFO, a.getArenaName() + " is using default arena Config");
        } else {
            plugin.getLogger().log(Level.INFO, a.getArenaName() + ": attempting to load custom config");
            ArenaConfig AC = new ArenaConfig(plugin.getCustomDisguises(a.getArenaName()), plugin.getCustomHiderClass(a.getArenaName()), plugin.getCustomSeekerClass(a.getArenaName()), false);
            ArenaManager.arenaConfigs.put(a, AC);
            plugin.getLogger().log(Level.INFO, a.getArenaName() + " is using a custom arena Config");

        }
    }


    public void saveData() {
        for (Arena a : ArenaManager.playableArenas.values()) {
            a.saveArenaToFile(plugin);
        }
    }

    public void reloadStorageFile() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), "StorageFile.yml");
        }
        StorageFilef = YamlConfiguration.loadConfiguration(customConfigFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource("StorageFile.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(defConfigStream);
            StorageFilef.setDefaults(defConfig);
        }
    }

    public FileConfiguration getStorageFile() {
        if (StorageFilef == null) {
            this.reloadStorageFile();
        }
        return StorageFilef;
    }

    public void saveStorageFile() {
        if (StorageFilef == null || customConfigFile == null) {
            return;
        }
        try {
            getStorageFile().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not save config to " + customConfigFile, ex);
        }
    }

}

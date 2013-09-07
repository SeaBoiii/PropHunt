package me.tomski.prophuntstorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.tomski.arenas.Arena;
import me.tomski.objects.ArenaFileStructureWrapper;
import me.tomski.objects.LocationBox;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.arenas.ArenaConfig;
import me.tomski.arenas.ArenaManager;

import me.tomski.shop.ShopItem;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class ShopConfig {


    public FileConfiguration StorageFilef = null;
    private File customConfigFile = null;
    private PropHunt plugin;

    public List<ShopItem> shopItems = new ArrayList<ShopItem>();

    public ShopConfig(PropHunt plugin) {
        this.plugin = plugin;
        getShopConfig().options().copyDefaults(true);
        saveShopConfig();
    }


    public void loadData() {

    }

    public void reloadShopConfig() {
        if (customConfigFile == null) {
            customConfigFile = new File(plugin.getDataFolder(), "Shop.yml");
        }
        StorageFilef = YamlConfiguration.loadConfiguration(customConfigFile);
        InputStream defConfigStream = plugin.getResource("Shop.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration
                    .loadConfiguration(defConfigStream);
            StorageFilef.setDefaults(defConfig);
        }
    }

    public FileConfiguration getShopConfig() {
        if (StorageFilef == null) {
            this.reloadShopConfig();
        }
        return StorageFilef;
    }

    public void saveShopConfig() {
        if (StorageFilef == null || customConfigFile == null) {
            return;
        }
        try {
            getShopConfig().save(customConfigFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Could not save config to " + customConfigFile, ex);
        }
    }

}

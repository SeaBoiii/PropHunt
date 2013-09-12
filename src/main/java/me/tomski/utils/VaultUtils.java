package me.tomski.utils;

import me.tomski.enums.EconomyType;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtils {

    public Permission permission = null;
    public Economy economy = null;
    private PropHunt plugin;

    public VaultUtils(PropHunt plugin) {
        this.plugin = plugin;
        if (setupPermissions()) {
            ShopSettings.enabled = true;
            plugin.getLogger().info("Vault permissions found!");
        } else {
            ShopSettings.enabled = false;
            plugin.getLogger().info("Vault permissions not found! Shop disabling!");
        }
        if (setupEconomy()) {
            ShopSettings.enabled = true;
            ShopSettings.currencyName = economy.currencyNamePlural();
            plugin.getLogger().info("Vault Economy found!");
            ShopSettings.economyType = EconomyType.VAULT;
        } else {
            ShopSettings.enabled = false;
            plugin.getLogger().info("Vault Economy not found! Shop disabling!");
        }
    }

    public VaultUtils(PropHunt plugin, boolean usingPropHunt) {
      if (usingPropHunt) {
          this.plugin = plugin;
          if (setupPermissions()) {
              ShopSettings.enabled = true;
              plugin.getLogger().info("Vault permissions found!");
          } else {
              ShopSettings.enabled = false;
              plugin.getLogger().info("Vault permissions not found! Shop disabling!");
          }
      }
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

}

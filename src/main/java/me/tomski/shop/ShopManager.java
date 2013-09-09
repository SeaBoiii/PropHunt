package me.tomski.shop;

import me.tomski.prophunt.PropHunt;

public class ShopManager {


    PropHunt plugin;

    MainShop mainShop;
    DisguiseShop disguiseShop;
    ItemShop itemShop;
    LoadoutChooser loadoutChooser;
    BlockChooser blockChooser;

    public ShopManager(PropHunt plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        mainShop = new MainShop(plugin);
        disguiseShop = new DisguiseShop(plugin);
        itemShop = new ItemShop(plugin);
        loadoutChooser = new LoadoutChooser(plugin);
        blockChooser = new BlockChooser(plugin);
        plugin.getServer().getPluginManager().registerEvents(mainShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(disguiseShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(itemShop, plugin);
        plugin.getServer().getPluginManager().registerEvents(loadoutChooser, plugin);
        plugin.getServer().getPluginManager().registerEvents(blockChooser, plugin);
    }


    public MainShop getMainShop() {
        return mainShop;
    }

    public BlockChooser getBlockChooser() {
        return blockChooser;
    }

    public DisguiseShop getDisguiseShop() {
        return disguiseShop;
    }

    public ItemShop getItemShop() {
        return itemShop;
    }

    public LoadoutChooser getLoadoutChooser() {
        return loadoutChooser;
    }
}

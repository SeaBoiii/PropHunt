package me.tomski.shop;


import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainShop implements Listener {

    private PropHunt plugin;
    public List<Player> inMenu = new ArrayList<Player>();


    public MainShop(PropHunt plugin) {
        this.plugin = plugin;
    }


    public void openMainShop(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.BLUE + "PropHunt Shop!");
        ItemStack customItems = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta itemMeta = customItems.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_AQUA + "PropHunt Items");
        List<String> itemLore = new ArrayList<String>();
        itemLore.add(ChatColor.GOLD + "Buy your PropHunt items here!");
        itemMeta.setLore(itemLore);
        customItems.setItemMeta(itemMeta);


        ItemStack customDisguises = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta disguiseMeta = customItems.getItemMeta();
        disguiseMeta.setDisplayName(ChatColor.DARK_GREEN + "PropHunt Disguise Shop");
        List<String> disLore = new ArrayList<String>();
        disLore.add(ChatColor.GOLD + "Buy your PropHunt disguises here!");
        disguiseMeta.setLore(disLore);
        customDisguises.setItemMeta(disguiseMeta);


        ItemStack placeHolder = new ItemStack(Material.ENDER_CHEST);
        ItemMeta placeMeta = customItems.getItemMeta();
        placeMeta.setDisplayName(ChatColor.DARK_RED + "Disguise Chooser");
        List<String> placeLore = new ArrayList<String>();
        placeLore.add(ChatColor.GOLD + "Use to select your disguises!");
        placeLore.add(ChatColor.GOLD + "You need to be in the lobby!");
        placeMeta.setLore(placeLore);
        placeHolder.setItemMeta(placeMeta);

        ItemStack currency = new ItemStack(Material.EMERALD);
        ItemMeta currencyMeta = customItems.getItemMeta();
        currencyMeta.setDisplayName(ChatColor.GOLD + "Your " + ShopSettings.currencyName);
        List<String> currencyLore = new ArrayList<String>();
        currencyLore.add(ChatColor.GREEN + "509");
        currencyMeta.setLore(currencyLore);
        currency.setItemMeta(currencyMeta);

        inv.setItem(3, customItems);
        inv.setItem(4, customDisguises);
        inv.setItem(5, placeHolder);
        inv.setItem(8, currency);
        inMenu.add(p);
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (inMenu.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {
                if (!e.getCurrentItem().getType().equals(Material.AIR)) {
                    if (e.getCurrentItem().getType().equals(Material.ENDER_CHEST)) {
                        //block chooser
                        e.getView().close();
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                plugin.blockChooser.openBlockShop((Player) e.getWhoClicked());
                            }
                        }, 2L);
                    } else if (e.getCurrentItem().getType().equals(Material.GOLD_BLOCK)) {
                        //disguise purchase
                        e.getView().close();
                    } else if (e.getCurrentItem().getType().equals(Material.DIAMOND_SWORD)) {
                        //item shop
                        e.getView().close();
                    } else {
                        e.getView().close();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (inMenu.contains(e.getPlayer())) {
            inMenu.remove(e.getPlayer());
        }
    }

}

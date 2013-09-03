package me.tomski.shop;


import me.tomski.prophunt.ShopSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MainShop {


    public void openMainShop(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.DARK_RED + "PropHunt Shop!");
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


        ItemStack placeHolder = new ItemStack(Material.COMPASS);
        ItemMeta placeMeta = customItems.getItemMeta();
        placeMeta.setDisplayName(ChatColor.DARK_GREEN + "Placeholder :)");
        List<String> placeLore = new ArrayList<String>();
        placeLore.add(ChatColor.GOLD + "Coming soon....");
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

        p.openInventory(inv);
    }
}

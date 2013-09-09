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

public class DisguiseShop implements Listener {

    private PropHunt plugin;
    public List<Player> inMenu = new ArrayList<Player>();


    public DisguiseShop(PropHunt plugin) {
        this.plugin = plugin;
    }


    public void openDisguiseShop(Player p) {
        Inventory i = Bukkit.createInventory(p, getShopSize(ShopSettings.blockChoices.size()), ChatColor.DARK_AQUA + "Disguise Shop");
        for (ShopItem item : ShopSettings.blockChoices) {
          item.addToInventory(i, p);
        }
        p.openInventory(i);
        inMenu.add(p);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (inMenu.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {
                for (ShopItem item : ShopSettings.blockChoices) {
                    if (item.itemStack.getType().equals(e.getCurrentItem().getType())) {
                        if (item.itemStack.getData().getData() == e.getCurrentItem().getData().getData()) {
                            item.buyItem((Player)e.getWhoClicked());
                            e.getView().close();
                        }
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

    private int getShopSize(int n) {
        return (int) Math.ceil(n / 9.0) * 9;
    }

}

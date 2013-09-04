package me.tomski.shop;


import me.tomski.language.MessageBank;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import me.tomski.utils.PropHuntMessaging;
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
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LoadoutChooser implements Listener {

    private PropHunt plugin;
    private List<Player> inInventory = new ArrayList<Player>();

    public LoadoutChooser(PropHunt plugin) {
        this.plugin = plugin;
    }

    public void openBlockShop(Player p) {
        if (!GameManager.playersWaiting.contains(p.getName())) {
            PropHuntMessaging.sendMessage(p, MessageBank.NOT_IN_LOBBY.getMsg());
            return;
        }
        Inventory inv = Bukkit.createInventory(p, getShopSize(ShopSettings.blockChoices.size()), ChatColor.DARK_AQUA + "Loadout Selector");

    }

    @EventHandler
    public void onInventClick(InventoryClickEvent e) {
        if (inInventory.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {

            }
        }
    }

    private boolean hasPermsForItem(Player player, ItemStack currentItem) {
        if (currentItem.getData().getData() == 0) {
            return player.hasPermission("prophunt.loadout." + currentItem.getTypeId());
        } else {
            return player.hasPermission("prophunt.loadout." + currentItem.getTypeId() + "-" + currentItem.getData().getData());
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (inInventory.contains(e.getPlayer())) {
            inInventory.remove(e.getPlayer());
        }
    }

    private int getShopSize(int n) {
        return (int) Math.ceil(n / 9.0) * 9;
    }
}

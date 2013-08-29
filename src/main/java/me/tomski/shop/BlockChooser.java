package me.tomski.shop;


import me.tomski.language.MessageBank;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import me.tomski.utils.PropHuntMessaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class BlockChooser implements Listener {

    private PropHunt plugin;
    private List<Player> inInventory = new ArrayList<Player>();

    public BlockChooser(PropHunt plugin) {
        this.plugin = plugin;
    }

    public void openBlockShop(Player p) {
        Inventory inv = Bukkit.createInventory(p, getShopSize(ShopSettings.blockChoices.size()), ChatColor.AQUA + "Disguise Selector");
        for (ItemStack stack : ShopSettings.blockChoices) {
            inv.addItem(stack);
        }
        p.openInventory(inv);
        inInventory.add(p);
    }

    @EventHandler
    public void onInventClick(InventoryClickEvent e) {
        if (inInventory.contains((Player) e.getWhoClicked())) {
            if (e.getCurrentItem() != null) {
                if (!hasPermsForBlock((Player) e.getWhoClicked(), e.getCurrentItem())) {
                    PropHuntMessaging.sendMessage((Player) e.getWhoClicked(), MessageBank.NO_BLOCK_CHOICE_PERMISSION.getMsg());
                    return;
                }
                DisguiseManager.preChosenDisguise.put((Player) e.getWhoClicked(), parseItemToDisguise(e.getCurrentItem()));
                e.getView().close();
                PropHuntMessaging.sendMessage((Player) e.getWhoClicked(), MessageBank.SHOP_CHOSEN_DISGUISE + e.getCurrentItem().getItemMeta().getDisplayName());
            }
        }
    }

    private boolean hasPermsForBlock(Player player, ItemStack currentItem) {
        if (currentItem.getData().getData() == 0) {
            return player.hasPermission("prophunt.blockchooser." + currentItem.getTypeId());
        } else {
            return player.hasPermission("prophunt.blockchooser." + currentItem.getTypeId() + "-" + currentItem.getData().getData());
        }
    }

    private Disguise parseItemToDisguise(ItemStack itemStack) {
        LinkedList<String> data = new LinkedList<String>();
        data.add(("blockID:" + itemStack.getTypeId()));
        data.add(("blockData" + itemStack.getData().getData()));
        return new Disguise(PropHunt.dc.newEntityID(), data, DisguiseType.FallingBlock);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (inInventory.contains(e.getPlayer())) {
            inInventory.remove(e.getPlayer());
        }
    }

    private int getShopSize(int n) {
        return (int) Math.ceil(n / 9) * 9;
    }
}

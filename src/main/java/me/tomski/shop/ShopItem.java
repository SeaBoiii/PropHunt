package me.tomski.shop;

import me.tomski.language.MessageBank;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ShopSettings;
import me.tomski.utils.PropHuntMessaging;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

    ItemStack itemStack;
    String itemName;
    int itemCost;
    String itemPermission;

    private PropHunt plugin;


    public ShopItem(PropHunt plugin, ItemStack stack, String name, int cost, String permission) {
        this.plugin = plugin;
        this.itemStack = stack;
        this.itemName = name;
        this.itemCost = cost;
        this.itemPermission = permission;
    }

    public boolean buyItem(Player p) {
        if (p.hasPermission(itemPermission)) {
            PropHuntMessaging.sendMessage(p, MessageBank.ALREADY_PURCHASED_ITEM.getMsg());
            return false;
        }
        return attemptPurchase(p);
    }

    private boolean attemptPurchase(Player p) {
        switch (ShopSettings.economyType) {
            case PROPHUNT:
                if (plugin.SQL.getCredits(p.getName()) >= itemCost) {
                    int credits = plugin.SQL.getCredits(p.getName());
                    credits = credits - itemCost;
                    plugin.SQL.setCredits(p.getName(), credits);
                    plugin.vaultUtils.permission.playerAdd(p, itemPermission);
                    PropHuntMessaging.sendMessage(p, MessageBank.PURCHASE_COMPLETE.getMsg() + itemName);
                    return true;
                } else {
                    PropHuntMessaging.sendMessage(p, MessageBank.NOT_ENOUGH_CURRENCY.getMsg());
                    return false;
                }
            case VAULT:
                if (plugin.vaultUtils.economy.getBalance(p.getName()) >= itemCost) {
                    plugin.vaultUtils.economy.withdrawPlayer(p.getName(), itemCost);
                    plugin.vaultUtils.permission.playerAdd(p, itemPermission);
                    PropHuntMessaging.sendMessage(p, MessageBank.PURCHASE_COMPLETE.getMsg() + itemName);
                    return true;
                } else {
                    PropHuntMessaging.sendMessage(p, MessageBank.NOT_ENOUGH_CURRENCY.getMsg());
                    return false;
                }
        }
        return false;
    }
}

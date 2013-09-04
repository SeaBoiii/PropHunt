package me.tomski.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

    ItemStack itemStack;
    String itemName;
    int itemCost;
    String itemPermission;


    public ShopItem(ItemStack stack, String name, int cost, String permission) {
        this.itemStack = stack;
        this.itemName = name;
        this.itemCost = cost;
        this.itemPermission = permission;
    }
}

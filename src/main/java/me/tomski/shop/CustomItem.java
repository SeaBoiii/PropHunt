package me.tomski.shop;


import me.tomski.prophunt.ShopSettings;
import org.bukkit.inventory.ItemStack;

public enum CustomItem {


    FIRST_SEEKER("First Seeker", getCfgItem("FirstSeeker")),
    FORCE_HIDER("Force Hider", getCfgItem("ForceHider"));


    private String itemName;
    private ItemStack item;


    CustomItem(String itemName, ItemStack item) {
        this.itemName = itemName;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getItemName() {
        return itemName;
    }

    public CustomItem getFromItemStack(ItemStack testStack) {
        for (CustomItem item : CustomItem.values()) {
            if (testStack.getType().equals(item.getItem().getType())) {
                if (testStack.getData().getData() == item.getItem().getData().getData()) {
                    return item;
                }
            }
        }
        return null;
    }


    private static ItemStack getCfgItem(String s) {
        return ShopSettings.getCustomItem(s);
    }

}

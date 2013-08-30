package me.tomski.prophunt;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopSettings {

    public static List<ItemStack> blockChoices = new ArrayList<ItemStack>();
    public static String currencyName;

    public static List<ItemStack> generateBlockChoices(List<String> stringList) {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (String itemString : stringList) {
            if (itemString.split(":").length == 2) {
                stacks.add(new ItemStack(Integer.valueOf(itemString.split(":")[0]), 1, Byte.valueOf(itemString.split(":")[1])));
            } else if (itemString.split(":").length == 1) {
                stacks.add(new ItemStack(Material.getMaterial(Integer.valueOf(itemString)), 1));
            }
        }
        return stacks;
    }

    public static List<ItemStack> cleanStacks() {
        List<ItemStack> newStacks = new ArrayList<ItemStack>();
        for (ItemStack s : blockChoices) {
            ItemMeta im = s.getItemMeta();
            String name = s.getType().toString().toLowerCase().replaceAll("_", " ");
            String finalName = name.substring(0, 1).toUpperCase() + name.substring(1);
            im.setDisplayName(ChatColor.BOLD + finalName);
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GOLD + "Click to choose the " + ChatColor.RESET + im.getDisplayName() + ChatColor.RESET + ChatColor.GOLD + " disguise!");
            im.setLore(lore);
            s.setItemMeta(im);
            newStacks.add(s);
        }
        return newStacks;
    }
}

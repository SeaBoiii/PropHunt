package me.tomski.prophunt;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerManagement {

    private static Map<String, ItemStack[]> playerInvents = new HashMap<String, ItemStack[]>();
    private static Map<String, Integer> playerXP = new HashMap<String, Integer>();
    private static Map<String, ItemStack> playerHelmet = new HashMap<String, ItemStack>();
    private static Map<String, ItemStack> playerBody = new HashMap<String, ItemStack>();
    private static Map<String, ItemStack> playerLegs = new HashMap<String, ItemStack>();
    private static Map<String, ItemStack> playerBoots = new HashMap<String, ItemStack>();


    public static void gameStartPlayer(Player p) {
        saveArmour(p);
        clearEffects(p);
        healPlayer(p);
        saveInvent(p);
        saveXp(p);

    }

    private static void removeFromMaps(Player p) {
        if (playerInvents.containsKey(p.getName())) {
            playerInvents.remove(p.getName());
        }
        if (playerXP.containsKey(p.getName())) {
            playerXP.remove(p.getName());
        }
        if (playerHelmet.containsKey(p.getName())) {
            playerHelmet.remove(p.getName());
        }
        if (playerBody.containsKey(p.getName())) {
            playerBody.remove(p.getName());
        }
        if (playerLegs.containsKey(p.getName())) {
            playerLegs.remove(p.getName());
        }
        if (playerBoots.containsKey(p.getName())) {
            playerBoots.remove(p.getName());
        }
    }

    @SuppressWarnings("deprecation")
    private static void saveArmour(Player p) {
        ItemStack helm = p.getInventory().getHelmet();
        ItemStack body = p.getInventory().getChestplate();
        ItemStack legs = p.getInventory().getLeggings();
        ItemStack boots = p.getInventory().getBoots();
        if (helm != null) {
            playerHelmet.put(p.getName(), helm);
        }
        if (body != null) {
            playerBody.put(p.getName(), body);
        }
        if (legs != null) {
            playerLegs.put(p.getName(), legs);
        }
        if (boots != null) {
            playerBoots.put(p.getName(), boots);
        }
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void saveXp(Player p) {
        playerXP.put(p.getName(), p.getLevel());
        p.setLevel(0);
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void saveInvent(Player p) {
        Inventory inv = p.getInventory();
        ItemStack[] itemarray = new ItemStack[inv.getSize()];
        int iter = 0;
        for (ItemStack item : inv.getContents()) {
            itemarray[iter] = item;
            iter++;
        }
        playerInvents.put(p.getName(), itemarray);
        p.getInventory().clear();
        p.updateInventory();
    }

    @SuppressWarnings("deprecation")
    private static void healPlayer(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.updateInventory();
    }

    private static void clearEffects(Player p) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
    }

    public static void gameRestorePlayer(Player p) {
        restoreXp(p);
        restoreInvent(p);
        removeFromMaps(p);
        restoreArmour(p);

    }

    @SuppressWarnings("deprecation")
    private static void restoreInvent(Player p) {
        if (playerInvents.containsKey(p.getName())) {
            p.getInventory().clear();
            ItemStack[] oldinv = playerInvents.get(p.getName());
            for (ItemStack i : oldinv) {
                if (i == null) {
                    continue;
                }
                p.getInventory().addItem(i);
            }
            p.updateInventory();
        }

    }

    @SuppressWarnings("deprecation")
    private static void restoreXp(Player p) {
        if (playerXP.containsKey(p.getName())) {
            if (playerXP.get(p.getName()) == null) {
                return;
            }
            p.setLevel(playerXP.get(p.getName()));
            p.updateInventory();
        }
    }

    @SuppressWarnings("deprecation")
    private static void restoreArmour(Player p) {
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
        if (playerHelmet.containsKey(p.getName())) {
            p.getInventory().setHelmet(playerHelmet.get(p.getName()));
        }
        if (playerBody.containsKey(p.getName())) {
            p.getInventory().setChestplate(playerBody.get(p.getName()));
        }
        if (playerLegs.containsKey(p.getName())) {
            p.getInventory().setLeggings(playerLegs.get(p.getName()));
        }
        if (playerBoots.containsKey(p.getName())) {
            p.getInventory().setBoots(playerBoots.get(p.getName()));
        }
        p.updateInventory();
    }

}

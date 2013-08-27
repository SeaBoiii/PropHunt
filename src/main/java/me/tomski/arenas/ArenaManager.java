package me.tomski.arenas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.tomski.prophunt.PropHunt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class ArenaManager {

    PropHunt plugin;
    public static Map<Arena, ArenaConfig> arenaConfigs = new HashMap<Arena, ArenaConfig>();

    public ArenaManager(PropHunt ph) {
        plugin = ph;


    }


    public static Map<String, String> setupMap = new HashMap<String, String>();
    public static Arena currentArena = null;

    public static Map<String, Arena> playableArenas = new HashMap<String, Arena>();
    public static Arena[] arenasInRotation = new Arena[playableArenas.size()];
    public static int rotationCounter = 0;

    public void addSettingUp(Player sender, String name) {
        if (setupMap.containsKey(sender.getName())) {
            return;
        } else {
            setupMap.put(sender.getName(), name);
            giveSetupTools(sender);
            currentArena = new Arena(name, null, null, null, null, null);
        }

    }


    @SuppressWarnings("deprecation")
    public void giveSetupTools(Player p) {
        ItemStack tool1 = new ItemStack(Material.WOOL, 1, (byte) 1);

        ItemMeta t1meta = tool1.getItemMeta();
        t1meta.setDisplayName(ChatColor.DARK_RED + "Hiders Spawn Tool");
        t1meta.setLore(Arrays.asList(ChatColor.RED + "Place this to set Hider Spawn!"));
        tool1.setItemMeta(t1meta);

        ItemStack tool2 = new ItemStack(Material.WOOL, 1, (byte) 2);
        ItemMeta t2meta = tool2.getItemMeta();
        t2meta.setDisplayName(ChatColor.DARK_BLUE + "Seekers Spawn Tool");
        t2meta.setLore(Arrays.asList(ChatColor.BLUE + "Place this to set the Seeker Spawn!"));
        tool2.setItemMeta(t2meta);


        ItemStack tool3 = new ItemStack(Material.WOOL, 1, (byte) 3);
        ItemMeta t3meta = tool3.getItemMeta();
        t3meta.setDisplayName(ChatColor.DARK_GREEN + "Lobby Spawn Tool");
        t3meta.setLore(Arrays.asList(ChatColor.GREEN + "Click this at the Lobby spawn location", ChatColor.GREEN + "corner of your arena!"));
        tool3.setItemMeta(t3meta);


        ItemStack tool4 = new ItemStack(Material.WOOL, 1, (byte) 4);
        ItemMeta t4meta = tool4.getItemMeta();
        t4meta.setDisplayName(ChatColor.DARK_GREEN + "Spectator Spawn Tool");
        t4meta.setLore(Arrays.asList(ChatColor.GREEN + "Click this at the Spectator spawn location", ChatColor.GREEN + "corner of your arena!"));
        tool4.setItemMeta(t4meta);


        ItemStack tool5 = new ItemStack(Material.WOOL, 1, (byte) 5);
        ItemMeta t4meta1 = tool5.getItemMeta();
        t4meta1.setDisplayName(ChatColor.DARK_GREEN + "Exit Spawn Tool");
        t4meta1.setLore(Arrays.asList(ChatColor.GREEN + "Click this at the Exit spawn location", ChatColor.GREEN + "corner of your arena!"));
        tool5.setItemMeta(t4meta1);

        p.getInventory().addItem(tool1, tool2, tool3, tool4, tool5);
        p.updateInventory();
    }

    public boolean checkComplete() {
        if (currentArena != null) {
            if (currentArena.isComplete()) {
                return true;
            }
        }
        return false;
    }


    public boolean deleteArena(String string) {
        String remove = null;
        for (String a : playableArenas.keySet()) {
            System.out.print(a);
            if (a.equalsIgnoreCase(string)) {
                remove = a;
                resetCounterAndArray();
                plugin.AS.getStorageFile().set("Arenas." + a, null);
                plugin.AS.saveStorageFile();
                plugin.getConfig().set("CustomArenaConfigs." + a, null);
                plugin.saveConfig();
                break;
            } else {
                continue;

            }
        }
        if (remove != null) {
            playableArenas.remove(remove);
            return true;
        }
        return false;
    }


    public void resetCounterAndArray() {
        arenasInRotation = new Arena[playableArenas.size()];
        int count = 0;
        for (Arena a : playableArenas.values()) {
            arenasInRotation[count] = a;
            count++;
        }
        rotationCounter = arenasInRotation.length - 1;
    }


    public static Arena getNextInRotation() {
        rotationCounter++;
        if (rotationCounter >= arenasInRotation.length) {
            rotationCounter = 0;
            return arenasInRotation[rotationCounter];
        } else {
            return arenasInRotation[rotationCounter];
        }
    }


    public boolean hasInvetorySpace(Player p) {
        int counter = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) {
                counter++;
            }

            if (counter >= 5) {
                return true;
            }
        }
        if (counter >= 5) {
            return true;
        } else {
            return false;
        }
    }


}

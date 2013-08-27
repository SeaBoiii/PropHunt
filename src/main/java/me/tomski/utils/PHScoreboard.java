package me.tomski.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;

public class PHScoreboard {


    private PropHunt plugin;

    public static boolean disguisesBlown = false;

    public PHScoreboard(PropHunt plugin) {
        this.plugin = plugin;
    }


    public void updateTab(Player p) {
        TabAPI.setPriority(plugin, p, 2);
        TabAPI.updatePlayer(p);

        TabAPI.setTabString(plugin, p, 1, 0, ChatColor.GOLD + "" + ChatColor.BOLD + "Prop");
        TabAPI.setTabString(plugin, p, 1, 1, ChatColor.GOLD + "" + ChatColor.BOLD + "Hunt");
        TabAPI.setTabString(plugin, p, 1, 2, ChatColor.GOLD + "" + ChatColor.BOLD + "Status");

        TabAPI.setTabString(plugin, p, 0, 0, ChatColor.GREEN + "----------" + TabAPI.nextNull());
        TabAPI.setTabString(plugin, p, 0, 1, ChatColor.GREEN + "----------" + TabAPI.nextNull());
        TabAPI.setTabString(plugin, p, 0, 2, ChatColor.GREEN + "----------" + TabAPI.nextNull());
        TabAPI.setTabString(plugin, p, 2, 0, ChatColor.GREEN + "----------" + TabAPI.nextNull());
        TabAPI.setTabString(plugin, p, 2, 1, ChatColor.GREEN + "----------" + TabAPI.nextNull());
        TabAPI.setTabString(plugin, p, 2, 2, ChatColor.GREEN + "----------" + TabAPI.nextNull());

        TabAPI.setTabString(plugin, p, 4, 0, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Seekers!");
        TabAPI.setTabString(plugin, p, 4, 1, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Hiders!");
        String team = "";
        if (GameManager.seekers.contains(p.getName())) {
            team = "Seeker";
        }
        if (GameManager.hiders.contains(p.getName())) {
            team = "Hider";
        }
        TabAPI.setTabString(plugin, p, 4, 2, ChatColor.GOLD + "" + ChatColor.BOLD + "Your team:");
        TabAPI.setTabString(plugin, p, 5, 2, ChatColor.YELLOW + team);
        if (PropHunt.dc.isDisguised(p) && PropHunt.dc.getDisguise(p).type.equals(DisguiseType.FallingBlock)) {
            TabAPI.setTabString(plugin, p, 6, 2, ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Disguise:");
            if (PropHunt.dc.getDisguise(p) != null) {
                if (PropHunt.dc.getDisguise(p).getBlockID() != null) {
                    TabAPI.setTabString(plugin, p, 7, 2, ChatColor.LIGHT_PURPLE + DisguiseManager.parseDisguiseToName(PropHunt.dc.getDisguise(p)));
                }
            }
        }
        if (disguisesBlown) {
            int y = 5;
            for (String name : GameManager.seekers) {
                if (y == 20) {
                    continue;
                }
                TabAPI.setTabString(plugin, p, y, 0, ChatColor.RED + name);
                y++;
            }
            while (y < 20) {

                TabAPI.setTabString(plugin, p, y, 0, " " + TabAPI.nextNull());
                y++;
            }
            y = 5;
            for (String name : GameManager.hiders) {
                if (y == 20) {
                    continue;
                }
                if (plugin.getServer().getPlayer(name) != null && plugin.getServer().getPlayer(name).isOnline()) {
                    if (PropHunt.dc.isDisguised(plugin.getServer().getPlayer(name))) {
                        TabAPI.setTabString(plugin, p, y, 1, ChatColor.GREEN + DisguiseManager.parseDisguiseToName(PropHunt.dc.getDisguise(plugin.getServer().getPlayer(name))) + TabAPI.nextNull());
                        y++;
                    }
                }
                if (SolidBlockTracker.solidBlocks.containsKey(name)) {
                    TabAPI.setTabString(plugin, p, y, 1, ChatColor.GREEN + Material.getMaterial(SolidBlockTracker.solidBlocks.get(name).id).name() + TabAPI.nextNull());
                    y++;
                }
            }
            while (y < 20) {
                TabAPI.setTabString(plugin, p, y, 1, " " + TabAPI.nextNull());
                y++;
            }
        } else {
            int y = 5;
            for (String name : GameManager.seekers) {
                if (y == 20) {
                    continue;
                }
                TabAPI.setTabString(plugin, p, y, 0, ChatColor.RED + name);
                y++;
            }
            while (y < 20) {
                TabAPI.setTabString(plugin, p, y, 0, " " + TabAPI.nextNull());
                y++;
            }
            y = 5;
            for (String name : GameManager.hiders) {
                if (y == 20) {
                    continue;
                }
                TabAPI.setTabString(plugin, p, y, 1, ChatColor.GREEN + name);
                y++;
            }
            while (y < 20) {
                TabAPI.setTabString(plugin, p, y, 1, " " + TabAPI.nextNull());
                y++;
            }
        }


        TabAPI.setTabString(plugin, p, 9, 2, ChatColor.BLUE + "" + ChatColor.BOLD + "Time left");
        TabAPI.setTabString(plugin, p, 10, 2, ChatColor.BLUE + "" + GameManager.timeleft);


        TabAPI.updatePlayer(p);
    }

    public void removeTab(Player p) {
        if (p.isOnline()) {
            TabAPI.setPriority(plugin, p, -2);
            TabAPI.updatePlayer(p);
        }
    }


}

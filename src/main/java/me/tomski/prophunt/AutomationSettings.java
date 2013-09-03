package me.tomski.prophunt;

import java.io.IOException;
import java.util.List;

import me.tomski.bungee.Pinger;

import org.bukkit.entity.Player;

public class AutomationSettings {

    public static boolean dispatchCommands;
    public static int gamesTillReset;
    public static int gamesPlayed = 0;
    public static List<String> commandsToRun;

    public static void initSettings(PropHunt plugin) {
        dispatchCommands = plugin.getConfig().getBoolean("AutomationSettings.dispatch-commands-after-x-games");
        gamesTillReset = plugin.getConfig().getInt("AutomationSettings.number-of-games");
        commandsToRun = plugin.getConfig().getStringList("AutomationSettings.commands");

    }

    public static boolean runChecks(final PropHunt plugin) {
        if (dispatchCommands) {
            gamesPlayed++;
            if (gamesPlayed == gamesTillReset) {
                for (String command : commandsToRun) {
                    if (command.equalsIgnoreCase("kickalltohub")) {
                        Pinger ping = new Pinger(plugin);
                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            try {
                                ping.connectToServer(p, BungeeSettings.hubname);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (command.equalsIgnoreCase("stop")) {
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
                            @Override
                            public void run() {
                                plugin.getServer().shutdown();
                            }
                        }, 100L);
                        return true;
                    } else {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                    }
                }
                gamesPlayed = 0;
                return false;
            }
        } else {
            return false;
        }
        return false;
    }
}

package me.tomski.utils;

import java.util.List;
import java.util.Map;

import me.tomski.arenas.Arena;
import me.tomski.prophunt.GameManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PropHuntMessaging {

    private static String prefix = "&0[&6Prop&4Hunt&0]: &a";
    private static String banner = "&60o0&0_______ &bPropHunt &0_______&60o0";

    public static void sendMessage(Player p, String msg) {
        String finalmsg = parseChatColors(prefix + msg);
        p.sendMessage(finalmsg);
    }

    public static void broadcastMessage(String msg) {
        String finalmsg = parseChatColors(prefix + msg);
        Bukkit.broadcastMessage(finalmsg);
    }

    public static void broadcastLobby(String msg) {
        String finalmsg = parseChatColors(prefix + msg);
        for (String s : GameManager.playersWaiting) {
            if (Bukkit.getServer().getPlayer(s) != null) {
                Bukkit.getServer().getPlayer(s).sendMessage(finalmsg);
            }

        }
    }


    public static void sendGameStatus(Player p) {
        if (GameManager.gameStatus == false && GameManager.isHosting) {
            p.sendMessage(parseChatColors(banner));
            p.sendMessage(parseChatColors("&4[&fGameStatus&4]: &6Pre-Game"));
            p.sendMessage(parseChatColors("&c[&bArena&c]: &b" + GameManager.currentGameArena.getArenaName()));
            p.sendMessage(parseChatColors("&c[&bPlayers&c]: &b" + GameManager.playersWaiting.size()));
            p.sendMessage(parseChatColors("&c[&bPlayers&c]: &f" + GameManager.playersWaiting));
            return;
        }
        if (GameManager.gameStatus == false) {
            p.sendMessage(parseChatColors(banner));
            p.sendMessage(parseChatColors("&4[&fGameStatus&4]: &6Not-Live"));
        } else {
            p.sendMessage(parseChatColors(banner));
            p.sendMessage(parseChatColors("&4[&fGameStatus&4]: &6Live"));
            p.sendMessage(parseChatColors("&c[&bArena&c]: &b" + GameManager.currentGameArena.getArenaName()));
            p.sendMessage(parseChatColors("&c[&bSeekers&c]: &b" + "(&4" + GameManager.seekers.size() + "&b) &a" + GameManager.seekers));
            p.sendMessage(parseChatColors("&c[&bHiders&c]: &b" + "(&4" + GameManager.hiders.size() + "&b) &a" + GameManager.hiders));
            p.sendMessage(parseChatColors("&c[&bSpectators&c]: &b" + "(&4" + GameManager.spectators.size() + "&b) &a" + GameManager.spectators));
            p.sendMessage(parseChatColors("&d[&fTimeLeft&d]: &f" + GameManager.timeleft));
        }
    }

    public static void sendPlayerHelp(Player p) {
        p.sendMessage(parseChatColors(banner));
        p.sendMessage(parseChatColors("&b/ph join &0- &6Join the current PropHunt game"));
        p.sendMessage(parseChatColors("&b/ph leave &0- &6Leave the current PropHunt game"));
        p.sendMessage(parseChatColors("&b/ph spectate &0- &6Spectate the current PropHunt game"));
        p.sendMessage(parseChatColors("&b/ph status &0- &6Check the status of PropHunt"));
    }

    public static void sendHostHelp(Player p) {
        sendPlayerHelp(p);
        p.sendMessage(parseChatColors("&b/ph host <arena> &0- &6Host a game of PropHunt"));
        p.sendMessage(parseChatColors("&b/ph start &0- &6Start the current PropHunt game"));
        p.sendMessage(parseChatColors("&b/ph stop &0- &6Stop the current PropHunt game"));
        p.sendMessage(parseChatColors("&b/ph kick <player> &0- &6Kick a player from the game"));
        p.sendMessage(parseChatColors("&b/ph setup <ArenaName>&0- &4Admin Only command- setup!"));
        p.sendMessage(parseChatColors("&b/ph delete <arena> &0- &4Admin Only command- delete!!"));

    }


    public static void broadcastMessageToPlayers(List<String> hiders, List<String> seekers, String msg) {
        for (String hider : hiders) {
            if (Bukkit.getServer().getPlayer(hider) != null) {
                sendMessage(Bukkit.getServer().getPlayer(hider), msg);
            }
        }
        for (String seeker : seekers) {
            if (Bukkit.getServer().getPlayer(seeker) != null) {
                sendMessage(Bukkit.getServer().getPlayer(seeker), msg);
            }
        }
    }


    private static String parseChatColors(String m) {
        return m.replaceAll("&", "\u00A7");
    }

    public static void sendAvailableArenas(Player p,
                                           Map<String, Arena> playableArenas) {
        p.sendMessage(parseChatColors(banner));
        p.sendMessage(parseChatColors("&fPlayable arenas:"));
        if (playableArenas == null || playableArenas.size() == 0) {
            p.sendMessage(parseChatColors("No arenas setup"));
            return;
        }
        for (String arenaName : playableArenas.keySet()) {
            p.sendMessage(parseChatColors("&6" + arenaName));
        }

    }
}

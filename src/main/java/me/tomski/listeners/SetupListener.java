package me.tomski.listeners;

import me.tomski.utils.PropHuntMessaging;
import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.arenas.ArenaConfig;
import me.tomski.arenas.ArenaManager;
import me.tomski.language.MessageBank;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetupListener implements Listener {


    private PropHunt plugin;

    public SetupListener(PropHunt ph) {
        this.plugin = ph;
    }


    @EventHandler
    public void onBlockPlace(PlayerInteractEvent e) {
        if (ArenaManager.setupMap.containsKey(e.getPlayer().getName())) {
            if (e.getItem()== null) {
                return;
            }
            if (e.getItem().getTypeId() == 35) {
                if (e.getItem().getData().getData() == 1) {
                    ArenaManager.currentArena.setHiderSpawn(e.getPlayer().getLocation());
                    PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.HIDER_SPAWN_SET.getMsg());
                    ifCompleteFinish(e);
                    e.setCancelled(true);
                    return;
                }
                if (e.getItem().getData().getData() == 2) {
                    ArenaManager.currentArena.setSeekerSpawn(e.getPlayer().getLocation());
                    PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.SEEKER_SPAWN_SET.getMsg());
                    ifCompleteFinish(e);
                    e.setCancelled(true);
                    return;
                }
                if (e.getItem().getData().getData() == 3) {
                    ArenaManager.currentArena.setLobbySpawn(e.getPlayer().getLocation());
                    PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.LOBBY_SPAWN_SET.getMsg());
                    ifCompleteFinish(e);
                    e.setCancelled(true);
                    return;
                }
                if (e.getItem().getData().getData() == 4) {
                    ArenaManager.currentArena.setSpectatorSpawn(e.getPlayer().getLocation());
                    PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.SPECTATOR_SPAWN_SET.getMsg());
                    ifCompleteFinish(e);
                    e.setCancelled(true);
                    return;
                }
                if (e.getItem().getData().getData() == 5) {
                    ArenaManager.currentArena.setExitSpawn(e.getPlayer().getLocation());
                    PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.EXIT_SPAWN_SET.getMsg());
                    ifCompleteFinish(e);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }


    private void ifCompleteFinish(PlayerInteractEvent e) {
        if (plugin.AM.checkComplete()) {
            ArenaManager.currentArena.saveArenaToFile(plugin);
            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.ARENA_COMPLETE.getMsg());
            ArenaManager.playableArenas.put(ArenaManager.currentArena.getArenaName(), ArenaManager.currentArena);
            ArenaConfig AC = new ArenaConfig(DisguiseManager.blockDisguises, GameManager.hiderCLASS, GameManager.seekerCLASS, true);
            ArenaManager.arenaConfigs.put(ArenaManager.currentArena, AC);
            ArenaManager.currentArena = null;
            ArenaManager.setupMap.remove(e.getPlayer().getName());
        }
    }


}

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
import org.bukkit.event.block.BlockPlaceEvent;

public class SetupListener implements Listener{

	
	private PropHunt plugin;

	public SetupListener(PropHunt ph){
		this.plugin = ph;
	}
	

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(ArenaManager.setupMap.containsKey(e.getPlayer().getName())){
			if(e.getBlock().getTypeId()==35){
				if(e.getBlock().getData()==1){
					ArenaManager.currentArena.setHiderSpawn(e.getBlock().getLocation());
					PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.HIDER_SPAWN_SET.getMsg());
					ifCompleteFinish(e);
					e.setCancelled(true);
					return;
				}
				if(e.getBlock().getData()==2){
					ArenaManager.currentArena.setSeekerSpawn(e.getBlock().getLocation());
					PropHuntMessaging.sendMessage(e.getPlayer(),MessageBank.SEEKER_SPAWN_SET.getMsg());
					ifCompleteFinish(e);
					e.setCancelled(true);
					return;
				}
				if(e.getBlock().getData()==3){
					ArenaManager.currentArena.setLobbySpawn(e.getBlock().getLocation());
					PropHuntMessaging.sendMessage(e.getPlayer(),MessageBank.LOBBY_SPAWN_SET.getMsg());
					ifCompleteFinish(e);
					e.setCancelled(true);
					return;
				}
				if(e.getBlock().getData()==4){
					ArenaManager.currentArena.setSpectatorSpawn(e.getBlock().getLocation());
					PropHuntMessaging.sendMessage(e.getPlayer(),MessageBank.SPECTATOR_SPAWN_SET.getMsg());
					ifCompleteFinish(e);
					e.setCancelled(true);
					return;
				}
				if(e.getBlock().getData()==5){
					ArenaManager.currentArena.setExitSpawn(e.getBlock().getLocation());
					PropHuntMessaging.sendMessage(e.getPlayer(),MessageBank.EXIT_SPAWN_SET.getMsg());
					ifCompleteFinish(e);
					e.setCancelled(true);
					return;
				}
			}
		}
	}


	private void ifCompleteFinish(BlockPlaceEvent e) {
		if(plugin.AM.checkComplete()){
			ArenaManager.currentArena.saveArenaToFile(plugin);
			PropHuntMessaging.sendMessage(e.getPlayer(),MessageBank.ARENA_COMPLETE.getMsg());
			ArenaManager.playableArenas.put(ArenaManager.currentArena.getArenaName(),ArenaManager.currentArena);
			ArenaConfig AC = new ArenaConfig(DisguiseManager.blockDisguises, GameManager.hiderCLASS, GameManager.seekerCLASS, true);
			ArenaManager.arenaConfigs.put(ArenaManager.currentArena,AC);
			ArenaManager.currentArena=null;
			ArenaManager.setupMap.remove(e.getPlayer().getName());
		}
	}

	
	
}

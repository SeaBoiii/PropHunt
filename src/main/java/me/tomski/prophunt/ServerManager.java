package me.tomski.prophunt;

import java.io.IOException;

import me.tomski.bungee.Pinger;
import me.tomski.language.LanguageManager;
import me.tomski.language.MessageBank;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerManager implements Listener{

	public static boolean forceMOTD;
	public static boolean forceMaxPlayers;
	public static int forceMaxPlayersSize;
	public static boolean blockAccessWhilstInGame;
	private PropHunt plugin;
	
	public ServerManager(PropHunt plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void playerKick(final PlayerLoginEvent e) throws IOException{
		if(GameManager.gameStatus && blockAccessWhilstInGame){
			if(e.getPlayer().isOp() || e.getPlayer().hasPermission("prophunt.joinoverride")){
				return;
			}else{
				if(BungeeSettings.usingPropHuntSigns && BungeeSettings.kickToHub){

					final Pinger ping = new Pinger(plugin);
					plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

						@Override
						public void run() {
							try {
								ping.connectToServer(e.getPlayer(), BungeeSettings.hubname);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}}, 5L);
					e.disallow(Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', MessageBank.BLOCK_ACCESS_IN_GAME.getMsg()));

					return;
				}
				e.disallow(Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', MessageBank.BLOCK_ACCESS_IN_GAME.getMsg()));
			}

		}
		if(forceMaxPlayers){
			if(!e.getPlayer().isOp()||e.getPlayer().hasPermission("prophunt.joinoverride")){
				if(plugin.getServer().getOnlinePlayers().length >= forceMaxPlayersSize){
					e.disallow(Result.KICK_FULL, ChatColor.translateAlternateColorCodes('&',MessageBank.SERVER_FULL_MESSAGE.getMsg()));
				}
			}
		}
	}

	
	@EventHandler
	public void playerPing(ServerListPingEvent e){
		if(forceMOTD){
			e.setMotd(ChatColor.translateAlternateColorCodes('&',getMOTD()));
		}
		if(forceMaxPlayers){
			e.setMaxPlayers(forceMaxPlayersSize);
		}
	}
	
	private String getMOTD(){
		String MOTD;
		boolean status = GameManager.gameStatus;
		int time = GameManager.timeleft;
		int hiders = GameManager.hiders.size();
		int seekers = GameManager.seekers.size();				
		if(status){
			MOTD = MessageBank.SERVER_STATUS_IN_GAME_MESSAGE.getMsg();
			MOTD = LanguageManager.regex(MOTD, "\\{seekers\\}", String.valueOf(seekers));
			MOTD = LanguageManager.regex(MOTD,"\\{hiders\\}", String.valueOf(hiders));
			MOTD = LanguageManager.regex(MOTD,"\\{time\\}", String.valueOf(time));
		}else{
			MOTD = MessageBank.SERVER_STATUS_IN_LOBBY_MESSAGE.getMsg();
			MOTD = LanguageManager.regex(MOTD,"\\{lobbyplayers\\}", String.valueOf(GameManager.playersWaiting.size()));

		}
		return MOTD;
	}
}

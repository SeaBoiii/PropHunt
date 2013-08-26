package me.tomski.utils;

import me.tomski.prophunt.GameManager;
import me.tomski.language.LanguageManager;
import me.tomski.language.MessageBank;
import me.tomski.prophunt.PropHunt;

public class LobbyThread implements Runnable{

	
	int time;
	int id;
	private PropHunt plugin;
	public boolean isRunning = false;
	
	public LobbyThread(PropHunt plugin,int startingTime){
		this.plugin = plugin;
		time = new Integer(startingTime);
	}

	@Override
	public void run() {
		if(!isRunning){
			return;
		}
		time--;
		GameManager.currentLobbyTime = time;
		if(time<=0){
			GameManager.currentLobbyTime = 0;
			if(GameManager.playersToStartGame <= GameManager.playersWaiting.size()){
				plugin.GM.startGame(null);
				isRunning=false;
				plugin.getServer().getScheduler().cancelTask(id);
				return;
			}else{
				String regex = MessageBank.NOT_ENOUGH_PLAYERS.getMsg();
				regex  = LanguageManager.regex(regex, "\\{playeramount\\}", String.valueOf(GameManager.playersToStartGame));
				PropHuntMessaging.broadcastLobby(regex);
				time = GameManager.lobbyTime;
				return;
			}
		}
	}
	
	public void setId(int ID){
		this.id = ID;

	}
	
}

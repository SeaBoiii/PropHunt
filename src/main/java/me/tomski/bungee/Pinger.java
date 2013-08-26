package me.tomski.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.tomski.prophunt.GameManager;
import org.bukkit.entity.Player;

import me.tomski.prophunt.BungeeSettings;
import me.tomski.prophunt.PropHunt;
import me.tomski.prophunt.ServerManager;

public class Pinger {
	
	
	private PropHunt plugin;
	public boolean sentData = false;

	public Pinger(PropHunt plugin){
		this.plugin = plugin;
	}
	
	public void connectToServer(Player p, String hub) throws IOException{
    	ByteArrayOutputStream b = new ByteArrayOutputStream();
    	DataOutputStream out = new DataOutputStream(b);
		out.writeUTF("Connect");
		out.writeUTF(hub);
		p.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	}
	
	
	public void sendServerDataEmpty() throws IOException{
		if(sentData){
			if(plugin.getServer().getOnlinePlayers().length>0){
		    	ByteArrayOutputStream b = new ByteArrayOutputStream();
		    	DataOutputStream out = new DataOutputStream(b);
		    	out.writeUTF("Forward");
		    	out.writeUTF(BungeeSettings.hubname);
		    	out.writeUTF("PropHunt");
		    	 
		    	ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		    	DataOutputStream msgout = new DataOutputStream(msgbytes);
		    	msgout.writeUTF(BungeeSettings.bungeeName);
		    	msgout.writeInt(plugin.getServer().getMaxPlayers()); // max players
		    	msgout.writeInt(0); // online players
		    	msgout.writeBoolean(GameManager.gameStatus);
		    	if(ServerManager.blockAccessWhilstInGame && GameManager.gameStatus){
		    		msgout.writeBoolean(false);
		    	}else{
		    		msgout.writeBoolean(true);
		    	}
		    	if(GameManager.gameStatus){
		    		msgout.writeUTF(GameManager.currentGameArena.getArenaName());
		    		msgout.writeInt(GameManager.timeleft);
		    		msgout.writeInt(GameManager.seekers.size());
		    		msgout.writeInt(GameManager.hiders.size());
		    		msgout.writeInt(GameManager.spectators.size());
		    	}else{
		    		msgout.writeInt(0);
		    	}
		    	
		    	out.writeShort(msgbytes.toByteArray().length);
		    	out.write(msgbytes.toByteArray());
		    	plugin.getServer().getOnlinePlayers()[0].sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
	
			}
		}
		
	}
	
	public void sendServerData() throws IOException{
		if(plugin.getServer().getOnlinePlayers().length>0){
	    	ByteArrayOutputStream b = new ByteArrayOutputStream();
	    	DataOutputStream out = new DataOutputStream(b);
	    	out.writeUTF("Forward");
	    	out.writeUTF(BungeeSettings.hubname);
	    	out.writeUTF("PropHunt");
	    	 
	    	ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
	    	DataOutputStream msgout = new DataOutputStream(msgbytes);
	    	msgout.writeUTF(BungeeSettings.bungeeName);
	    	if(ServerManager.forceMaxPlayers){
	    		msgout.writeInt(ServerManager.forceMaxPlayersSize);
	    	}else{
	    		msgout.writeInt(plugin.getServer().getMaxPlayers()); // max 
	    	}
	    	msgout.writeInt(plugin.getServer().getOnlinePlayers().length); // on
	    	msgout.writeBoolean(GameManager.gameStatus);
	    	if(ServerManager.blockAccessWhilstInGame && GameManager.gameStatus){
	    		msgout.writeBoolean(false);
	    	}else{
	    		msgout.writeBoolean(true);
	    	}
	    	if(GameManager.gameStatus){
	    		msgout.writeUTF(GameManager.currentGameArena.getArenaName());
	    		msgout.writeInt(GameManager.timeleft);
	    		msgout.writeInt(GameManager.seekers.size());
	    		msgout.writeInt(GameManager.hiders.size());
	    		msgout.writeInt(GameManager.spectators.size());
	    	}else{
	    		msgout.writeInt(GameManager.playersWaiting.size());
	    	}
	    	
	    	out.writeShort(msgbytes.toByteArray().length);
	    	out.write(msgbytes.toByteArray());
	    	
	    	plugin.getServer().getOnlinePlayers()[0].sendPluginMessage(plugin, "BungeeCord", b.toByteArray());

		}
		
	}
}

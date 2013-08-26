package me.tomski.utils;

import java.util.ArrayList;
import java.util.List;

import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.language.MessageBank;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SeekerDelay implements Runnable {

	private int ID;
	private List<Player> PLAYERS;
	int COUNTER;
	private PropHunt PLUGIN;
	private Location LOCATION;
	public boolean isDelaying;

	public SeekerDelay(Player firstSeeker,int time,PropHunt plugin) {
		PLAYERS = new ArrayList<Player>();
		PLAYERS.add(firstSeeker);
		this.COUNTER = time;
		this.PLUGIN = plugin;
		this.LOCATION = firstSeeker.getLocation().clone();
		isDelaying = true;
	}

	public void setID(int delayID) {
		this.ID = delayID;
	}

	public void addPlayer(Player seeker){
		PLAYERS.add(seeker);
	}
	
	@Override
	public void run() {
		
		for(Player p :PLAYERS){
			if(!GameManager.seekers.contains(p.getName())){
				continue;
			}
			
			if(COUNTER==GameManager.seekerDelayTime){
				if(GameManager.blindSeeker){
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,20*GameManager.seekerDelayTime,1));
				}
				PropHuntMessaging.sendMessage(p, MessageBank.SEEKER_DELAY.getMsg());
			}
			p.teleport(LOCATION);
			if(COUNTER<=0){
				PropHuntMessaging.sendMessage(p, MessageBank.SEEKER_DELAY_END.getMsg());
				PLUGIN.SBS.addPlayerToGame(PLUGIN, p);
			}

		}
		if(COUNTER<=0){
			isDelaying = false;
			PLUGIN.getServer().getScheduler().cancelTask(ID);
		}
		COUNTER--;


	}

}

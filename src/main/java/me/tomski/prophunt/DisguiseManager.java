package me.tomski.prophunt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import me.tomski.utils.PropHuntMessaging;
import me.tomski.arenas.ArenaConfig;
import me.tomski.language.MessageBank;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DisguiseManager {
	
	private static PropHunt plugin;
	public static Map<Integer,String> blockDisguises = new HashMap<Integer,String>();

	
	public DisguiseManager(PropHunt plugin){
		DisguiseManager.plugin = plugin;
		int i = DisguiseManager.plugin.loadBlockDisguises();
		DisguiseManager.plugin.getLogger().log(Level.INFO,"PropHunt: " + i + " disgiuses loaded");
	}
	

	public static void randomDisguise(Player p, ArenaConfig ac){
		int i = PropHunt.dc.newEntityID();
		String id = getRandomBlockID(ac.getArenaDisguises());
		
		String type = getDisguiseType(id);
		Disguise ds = null;
		boolean found = false;
		while(!found){
			if(type.equalsIgnoreCase("single")){
				ds =  new Disguise(i,"blockID:"+id,DisguiseType.FallingBlock);
				found = true;
				break;
			}else if(type.equalsIgnoreCase("entity")){
				ds = new Disguise(i,"",DisguiseType.fromString(id.split(":")[1]));
				found = true;
				break;
			}else if(type.equalsIgnoreCase("damage")){
				LinkedList<String> data= new LinkedList<String>();
				data.add("blockID:"+id.split(":")[0]);
				data.add("blockData:"+id.split(":")[1]);
				ds = new Disguise(i,data,DisguiseType.FallingBlock);
				
				found = true;
				break;
			}
		}
		if(ds==null){
			PropHuntMessaging.sendMessage(p, MessageBank.DISGUISE_ERROR.getMsg());
			return;
		}

		
		if(PropHunt.dc.isDisguised(p)){
			PropHunt.dc.changePlayerDisguise(p, ds);
			PropHuntMessaging.sendMessage(p,MessageBank.DISGUISE_MESSAGE.getMsg() +parseDisguiseToName(ds));
		}else{
			PropHunt.dc.disguisePlayer(p,ds);
			PropHuntMessaging.sendMessage(p,MessageBank.DISGUISE_MESSAGE.getMsg() + parseDisguiseToName(ds));

		}
	}
	
	public static String parseDisguiseToName(Disguise ds){
		if(ds.type.equals(DisguiseType.FallingBlock)){
			if(ds.getBlockData()!=null){
				byte colour = ds.getBlockData();
				if(ds.getBlockID().equals(35)){
					String Colour = DyeColor.getByWoolData(colour).name();
					return Colour + " Wool";
				}
			}
			return Material.getMaterial(ds.getBlockID()).name();
		}else{
			return ds.type.name();
		}
	}


	private static String getRandomBlockID(Map<Integer,String> disguises) {
		int size = disguises.size();
		Random rnd = new Random();
		int random = rnd.nextInt(size);
		String disguise = disguises.get(random+1);
		return disguise;
	}
	
	

	private static String getDisguiseType(String dis){
		if(dis.startsWith("e:")){
			return "entity";
		}
		if(dis.split(":").length==2){
			return "damage";
		}
		else{
			return "single";
		}
	}
	
	
}

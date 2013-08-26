package me.tomski.blocks;

import java.lang.reflect.InvocationTargetException;

import me.tomski.prophunt.PropHunt;
import me.tomski.listeners.PropHuntListener;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;


public class SolidBlock {

	
	public Player owner;
	public Location loc;
	public Integer id;
	byte damage;
	ProtocolManager pm;
	PacketContainer blockChange;
	private Disguise d;
	
	public SolidBlock(Location loc, Integer integer, byte dmg,Player p,ProtocolManager pm,PropHunt plugin) throws InvocationTargetException{
		this.loc = loc.clone();
		this.damage = dmg;
		this.pm = pm;
		this.id = integer;
		d = PropHunt.dc.getDisguise(p);
		if(d.type.equals(DisguiseType.FallingBlock)){
			d.addSingleData("blocklock");	
			d.addSingleData("nomove");
		}
		blockChange = getBlockPacket();
		this.owner = p;
		PropHuntListener.tempIgnoreUndisguise.add(owner);
		d.data.remove("nomove");

		plugin.hidePlayer(owner,owner.getInventory().getArmorContents());

		
		
	}
	
	public boolean hasMoved(PropHunt plugin) {
		if(owner.getLocation().getBlockX()!=loc.getBlockX()){
			return true;
		}
		if(owner.getLocation().getBlockZ()!=loc.getBlockZ()){
			return true;
		}
		if(owner.getLocation().getBlockY()!=loc.getBlockY()){
			return true;
		}
		try {
            sendPacket(plugin.getServer().getOnlinePlayers());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
		return false;
	}
	
	private PacketContainer getBlockPacket() {
		blockChange = pm.createPacket(Packets.Server.BLOCK_CHANGE);
		blockChange.getIntegers().
		write(0,loc.getBlockX()).
		write(1,loc.getBlockY()).
		write(2,loc.getBlockZ()).
		write(3,id.intValue()).
		write(4,(int) damage);
		return blockChange;
	}

	public void unSetBlock(PropHunt plugin) throws InvocationTargetException{
		blockChange = pm.createPacket(Packets.Server.BLOCK_CHANGE);
		blockChange.getIntegers().
		write(0,loc.getBlockX()).
		write(1,loc.getBlockY()).
		write(2,loc.getBlockZ()).
		write(3,0).
		write(4,(int) damage);
		
		PropHunt.dc.disguisePlayer(owner, d);
		if(PropHunt.dc.isDisguised(owner)){
			Disguise d = PropHunt.dc.getDisguise(owner);
			if(d.type.equals(DisguiseType.FallingBlock)){
				d.data.remove("blocklock");	
				d.data.remove("nomove");

			}
		}		
	}
	
	public void sendPacket(Player[] players) throws InvocationTargetException{
		for(Player p : players){
			if(p.equals(owner)){
				continue;
			}
			pm.sendServerPacket(p, blockChange);
		}
	}
}

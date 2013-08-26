package me.tomski.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import me.tomski.blocks.SolidBlock;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.language.MessageBank;
import me.tomski.listeners.PropHuntListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeSolidifyThread implements Runnable{
    
    private PropHunt plugin;
    List<String> removeList = new ArrayList<String>();
    List<Player> playerRemoveList = new ArrayList<Player>();


    public DeSolidifyThread(PropHunt plugin){
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Entry<String, SolidBlock> sb : SolidBlockTracker.solidBlocks.entrySet()){
            if(sb.getValue().hasMoved(plugin)  || GameManager.seekers.contains(SolidBlockTracker.solidBlocks.get(sb.getKey()).owner.getName())){
                try {
                    PropHuntMessaging.sendMessage(sb.getValue().owner, MessageBank.BROKEN_SOLID_BLOCK.getMsg());
                    sb.getValue().unSetBlock(plugin);
                    sb.getValue().sendPacket(Bukkit.getOnlinePlayers());
                    removeList.add(sb.getValue().owner.getName());
                    SolidBlockTracker.currentLocation.put(sb.getValue().owner.getName(), sb.getValue().owner.getLocation());
                    SolidBlockTracker.movementTracker.put(sb.getValue().owner.getName(), 0);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            }
        }
        for(String s : removeList){
            SolidBlockTracker.solidBlocks.remove(s);
        }
        removeList.clear();
        for(Player  p : PropHuntListener.playerOnBlocks.keySet()){
            PropHuntListener.playerOnBlocks.put(p, PropHuntListener.playerOnBlocks.get(p) -1);
            if(PropHuntListener.playerOnBlocks.get(p) <= 0) {
                playerRemoveList.add(p);
            }
        }
        for(Player s : playerRemoveList){
            PropHuntListener.playerOnBlocks.remove(s);
        }
        playerRemoveList.clear();
    }

}

package me.tomski.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tomski.prophunt.GameManager;
import me.tomski.blocks.SolidBlock;
import me.tomski.language.MessageBank;
import me.tomski.prophunt.PropHunt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class SolidBlockTracker implements Runnable {

    public static Map<String, Integer> movementTracker = new HashMap<String, Integer>();
    public static Map<String, Location> currentLocation = new HashMap<String, Location>();

    public static Map<String, SolidBlock> solidBlocks = new HashMap<String, SolidBlock>();

    List<String> removeList = new ArrayList<String>();
    private PropHunt plugin;

    public SolidBlockTracker(PropHunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        for (String s : movementTracker.keySet()) {
            if (Bukkit.getPlayer(s) == null) {
                removeList.add(s);
                continue;
            }
            if (!PropHunt.dc.isDisguised(Bukkit.getPlayer(s))) {
                removeList.add(s);
                continue;
            }
            if (solidBlocks.containsKey(s)) {
                if (!solidBlocks.get(s).dead) {
                    continue;
                }
            }
            if (shouldBeSolid(movementTracker.get(s))) {
                SolidBlock sb = null;
                try {
                    Byte data = PropHunt.dc.getDisguise(Bukkit.getPlayer(s)).getBlockData();
                    if (data == null) {
                        data = 0;
                    }
                    if (PropHunt.dc.getDisguise(Bukkit.getPlayer(s)).getBlockID() != null) {
                        sb = new SolidBlock(currentLocation.get(s), PropHunt.dc.getDisguise(Bukkit.getPlayer(s)).getBlockID(), data, Bukkit.getPlayer(s), PropHunt.protocolManager, plugin);
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (sb != null) {
                    solidBlocks.put(s, sb);
                    try {
                        PropHuntMessaging.sendMessage(sb.owner, MessageBank.SOLID_BLOCK.getMsg());
                        sb.sendPacket(Bukkit.getOnlinePlayers());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        for (String s : removeList) {
            movementTracker.remove(s);
        }
        removeList.clear();


        for (String p : GameManager.hiders) {
            if (GameManager.seekers.contains(p)) {
                continue;
            }
            if (Bukkit.getPlayer(p) == null) {
                continue;
            }
            if (solidBlocks.containsKey(p)) {
                continue;
            }
            if (currentLocation.containsKey(p) && movementTracker.containsKey(p)) {

                if (hasMoved(currentLocation.get(p), Bukkit.getPlayer(p).getLocation())) {
                    currentLocation.put(p, Bukkit.getPlayer(p).getLocation().clone());
                    movementTracker.put(p, 0);
                    continue;
                } else {
                    movementTracker.put(p, movementTracker.get(p) + 1);
                    continue;
                }

            } else {
                currentLocation.put(p, Bukkit.getPlayer(p).getLocation());
                movementTracker.put(p, 0);
                continue;
            }
        }

    }

    public boolean hasMoved(Location loc, Location test) {
        if (test.getBlockX() != loc.getBlockX()) {
            return true;
        }
        if (test.getBlockZ() != loc.getBlockZ()) {
            return true;
        }
        if (test.getBlockY() != loc.getBlockY()) {
            return true;
        }
        return false;
    }


    private boolean shouldBeSolid(int i) {
        return i == GameManager.solidBlockTime;
    }
}

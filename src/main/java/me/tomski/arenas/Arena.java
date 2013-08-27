package me.tomski.arenas;

import me.tomski.prophunt.PropHunt;

import org.bukkit.Location;

public class Arena {

    private String arenaName = null;
    private Location seekerSpawn = null;
    private Location hiderSpawn = null;
    private Location lobbySpawn = null;
    private Location spectatorSpawn = null;
    private Location exitSpawn = null;

    public Arena(String name, Location lobby, Location exit, Location seeker, Location hider, Location spec) {
        this.arenaName = name;
        this.lobbySpawn = lobby;
        this.exitSpawn = exit;
        this.seekerSpawn = seeker;
        this.hiderSpawn = hider;
        this.spectatorSpawn = spec;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public Location getSeekerSpawn() {
        return seekerSpawn;
    }

    public void setSeekerSpawn(Location seekerSpawn) {
        this.seekerSpawn = seekerSpawn;
    }

    public Location getHiderSpawn() {
        return hiderSpawn;
    }


    public void setHiderSpawn(Location hiderSpawn) {
        this.hiderSpawn = hiderSpawn;
    }


    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public void setLobbySpawn(Location lobbySpawn) {
        this.lobbySpawn = lobbySpawn;
    }


    public Location getSpectatorSpawn() {
        return spectatorSpawn;
    }


    public void setSpectatorSpawn(Location spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
    }


    public void saveArenaToFile(PropHunt plugin) {
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".worldname", lobbySpawn.getWorld().getName());
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".lobbyVec", lobbySpawn.toVector());
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".seekerVec", seekerSpawn.toVector());
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".hiderVec", hiderSpawn.toVector());
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".spectatorVec", spectatorSpawn.toVector());
        plugin.AS.getStorageFile().set("Arenas." + arenaName + ".exitVec", exitSpawn.toVector());
        plugin.AS.saveStorageFile();
        if (!plugin.getConfig().contains("CustomArenaConfigs." + arenaName + ".usingDefault")) {
            plugin.getConfig().set("CustomArenaConfigs." + arenaName + ".usingDefault", true);
            plugin.saveConfig();
        }
    }


    public boolean isComplete() {
        if (seekerSpawn != null && hiderSpawn != null && lobbySpawn != null && spectatorSpawn != null && exitSpawn != null) {
            return true;
        } else {
            return false;
        }
    }


    public Location getExitSpawn() {
        return exitSpawn;
    }


    public void setExitSpawn(Location exitSpawn) {
        this.exitSpawn = exitSpawn;
    }

}

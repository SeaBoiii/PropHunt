package me.tomski.objects;

import me.tomski.arenas.Arena;
import me.tomski.prophunt.PropHunt;
import org.bukkit.configuration.file.FileConfiguration;

public class ArenaFileStructureWrapper {


    public static boolean usingOldFormat(FileConfiguration storage, String arenaName) {
        return storage.isVector("Arenas." + arenaName + ".lobbyVec");
    }

    public static void translateToNewStorageFormat(PropHunt plugin, FileConfiguration storage, Arena a) {
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName(), null);
        plugin.AS.saveStorageFile();
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName() + ".lobbySpawn", new LocationBox(a.getLobbySpawn()).box());
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName() + ".seekerSpawn", new LocationBox(a.getSeekerSpawn()).box());
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName() + ".hiderSpawn", new LocationBox(a.getHiderSpawn()).box());
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName() + ".exitSpawn", new LocationBox(a.getExitSpawn()).box());
        plugin.AS.getStorageFile().set("Arenas." + a.getArenaName() + ".spectatorSpawn", new LocationBox(a.getSpectatorSpawn()).box());
        plugin.AS.saveStorageFile();
    }
}

package me.tomski.listeners;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import me.tomski.prophunt.BungeeSettings;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PlayerManagement;
import me.tomski.prophunt.PropHunt;
import me.tomski.arenas.ArenaManager;
import me.tomski.blocks.SolidBlock;
import me.tomski.bungee.Pinger;
import me.tomski.language.LanguageManager;
import me.tomski.language.MessageBank;
import me.tomski.utils.PropHuntMessaging;
import me.tomski.utils.Reason;
import me.tomski.utils.SolidBlockTracker;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import pgDev.bukkit.DisguiseCraft.api.PlayerUndisguiseEvent;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class PropHuntListener implements Listener {

    public static List<Player> tempIgnoreUndisguise = new ArrayList<Player>();
    private GameManager GM = null;
    private PropHunt PH = null;
    private List<String> allowedcmds = new ArrayList<String>();
    public static Map<Player, Integer> playerOnBlocks = new HashMap<Player, Integer>();

    public PropHuntListener(PropHunt plugin, GameManager Gamem) {
        this.PH = plugin;
        this.GM = Gamem;
        allowedcmds.add("/ph leave");
        allowedcmds.add("/ph status");
        allowedcmds.add("/prophunt leave");
        allowedcmds.add("/prophunt leave");
    }

    @EventHandler
    public void playerKickEvent(PlayerKickEvent e) {
        if (e.getReason().contains("Flying")) {
            if (GameManager.hiders.contains(e.getPlayer().getName())) {
                if (playerOnBlocks.containsKey(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
                int x = e.getPlayer().getLocation().getBlockX();
                int y = e.getPlayer().getLocation().getBlockY() - 1;
                int z = e.getPlayer().getLocation().getBlockZ();
                for (SolidBlock s : SolidBlockTracker.solidBlocks.values()) {
                    if (s.loc.getBlockX() < x + 2 && s.loc.getBlockX() > x - 2) {
                        if (s.loc.getBlockY() < y + 2 && s.loc.getBlockY() > y - 2) {
                            if (s.loc.getBlockZ() < z + 2 && s.loc.getBlockZ() > z - 2) {
                                if (!playerOnBlocks.containsKey(e.getPlayer())) {
                                    playerOnBlocks.put(e.getPlayer(), 20);
                                }
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
            if (GameManager.seekers.contains(e.getPlayer().getName())) {
                if (playerOnBlocks.containsKey(e.getPlayer())) {
                    e.setCancelled(true);
                    return;
                }
                int x = e.getPlayer().getLocation().getBlockX();
                int y = e.getPlayer().getLocation().getBlockY() - 1;
                int z = e.getPlayer().getLocation().getBlockZ();
                for (SolidBlock s : SolidBlockTracker.solidBlocks.values()) {
                    if (s.loc.getBlockX() < x + 2 && s.loc.getBlockX() > x - 2) {
                        if (s.loc.getBlockY() < y + 2 && s.loc.getBlockY() > y - 2) {
                            if (s.loc.getBlockZ() < z + 2 && s.loc.getBlockZ() > z - 2) {
                                if (!playerOnBlocks.containsKey(e.getPlayer())) {
                                    playerOnBlocks.put(e.getPlayer(), 20);
                                }
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCrouchEvent(PlayerToggleSneakEvent e) {
        if (GameManager.crouchBlockLock) {
            if (GameManager.hiders.contains(e.getPlayer().getName())) {
                if (SolidBlockTracker.solidBlocks.containsKey(e.getPlayer().getName())) {
                    return;
                }
                if (PropHunt.dc.isDisguised(e.getPlayer())) {
                    Disguise d = PropHunt.dc.getDisguise(e.getPlayer());
                    if (d.type.equals(DisguiseType.FallingBlock)) {
                        if (e.isSneaking()) {
                            d.addSingleData("blocklock");
                            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.TOGGLE_BLOCK_LOCK_ON.getMsg());
                        } else {
                            d.data.remove("blocklock");
                            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.TOGGLE_BLOCK_LOCK_OFF.getMsg());
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPLayerDrop(PlayerDropItemEvent e) {
        if (GameManager.hiders.contains(e.getPlayer().getName()) || GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_ITEM_SHARING.getMsg());
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if (e.getPlayer().hasPermission("prophunt.admin.commandoverride")) {
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            if (!allowedcmds.contains(e.getMessage().toLowerCase())) {
                PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.NO_GAME_COMMANDS.getMsg());
                e.setCancelled(true);
            }
        }
    }

    private void refreshDisguises() {
        PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

            @Override
            public void run() {
                for (Player p : PropHunt.dc.getOnlineDisguisedPlayers()) {
                    if (p.isOnline() && PropHunt.dc.isDisguised(p)) {
                        PropHunt.dc.disguisePlayer(p, PropHunt.dc.getDisguise(p));
                    }
                }
            }
        }, 20L);

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent e) {

        if (GameManager.playersQuit.contains(e.getPlayer().getName())) {
            e.setRespawnLocation(GameManager.currentGameArena.getExitSpawn());
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PlayerManagement.gameRestorePlayer(e.getPlayer());
                    if (PropHunt.usingTABAPI) {
                        GameManager.SB.removeTab(e.getPlayer());
                    }
                    if (GameManager.useSideStats) {
                        PH.SBS.removeScoreboard(PH, e.getPlayer());
                    }

                }
            }, 20L);
            GameManager.playersQuit.remove(e.getPlayer().getName());
            refreshDisguises();
            if (PropHunt.dc.isDisguised(e.getPlayer())) {
                PropHunt.dc.undisguisePlayer(e.getPlayer());
                return;
            }
        }
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            PH.SBS.addPlayerToGame(PH, e.getPlayer());
            e.setRespawnLocation(GameManager.currentGameArena.getSpectatorSpawn());
            if (PropHunt.dc.isDisguised(e.getPlayer())) {
                PropHunt.dc.undisguisePlayer(e.getPlayer());
            }
            refreshDisguises();
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setRespawnLocation(GameManager.currentGameArena.getSeekerSpawn());
            if (GameManager.seekerDelayTime != 0) {
                if (GameManager.sd.isDelaying) {
                    GameManager.sd.addPlayer(e.getPlayer());
                }
            }
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PH.showPlayer(e.getPlayer());

                    if (PropHunt.dc.isDisguised(e.getPlayer())) {
                        PropHunt.dc.undisguisePlayer(e.getPlayer());
                    }
                    ArenaManager.arenaConfigs.get(GameManager.currentGameArena).getArenaSeekerClass().givePlayer(e.getPlayer());
                    PH.SBS.addPlayerToGame(PH, e.getPlayer());

                }
            }, 20L);
            refreshDisguises();
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            PH.SBS.addPlayerToGame(PH, e.getPlayer());
            e.setRespawnLocation(GameManager.currentGameArena.getSeekerSpawn());
            ArenaManager.arenaConfigs.get(GameManager.currentGameArena).getArenaHiderClass().givePlayer(e.getPlayer());
            refreshDisguises();
            return;
        }
        if (PropHunt.dc.isDisguised(e.getPlayer())) {
            PropHunt.dc.undisguisePlayer(e.getPlayer());
        }
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws IllegalAccessException, InvocationTargetException, IOException {
        if (GameManager.hiders.contains(e.getEntity().getName())) {
            e.getDrops().clear();
            if (isLastHider()) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                GameManager.playersQuit.add(e.getEntity().getName());
                GameManager.hiders.remove(e.getEntity().getName());
                respawnQuick(e.getEntity());
                GM.endGame(Reason.SEEKERWON, false);
                return;
            }
            GameManager.hiders.remove(e.getEntity().getName());
            GameManager.seekers.add(e.getEntity().getName());
            GameManager.seekerLives.put(e.getEntity(), GameManager.seekerLivesAmount);
            if (SolidBlockTracker.solidBlocks.containsKey(e.getEntity().getName())) {
                SolidBlockTracker.solidBlocks.get(e.getEntity().getName()).unSetBlock(PH);
            }
            respawnQuick(e.getEntity());
            PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, e.getEntity().getName() + MessageBank.HIDER_DEATH_MESSAGE.getMsg());
            GameManager.GT.timeleft = GameManager.GT.timeleft + GameManager.time_reward;
            if (GameManager.time_reward != 0) {
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, MessageBank.TIME_INCREASE_MESSAGE.getMsg() + GameManager.time_reward);
            }
            return;
        } else if (GameManager.seekers.contains(e.getEntity().getName())) {
            e.getDrops().clear();
            if (isLastSeeker()) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                if (GameManager.chooseNewSeeker && GameManager.firstSeeker.equalsIgnoreCase(e.getEntity().getName())) {
                    GameManager.playersQuit.add(e.getEntity().getName());
                    GameManager.seekers.remove(e.getEntity().getName());
                    respawnQuick(e.getEntity());
                    this.GM.chooseNewSeekerMeth(GameManager.hiders);
                    return;
                }
                if (noLivesLeft(e.getEntity())) {
                    GameManager.playersQuit.add(e.getEntity().getName());
                    GameManager.seekers.remove(e.getEntity().getName());
                    respawnQuick(e.getEntity());
                    GM.endGame(Reason.HIDERSWON, false);
                    return;
                } else {
                    String msg = MessageBank.SEEKER_LIVES_MESSAGE.getMsg();
                    msg = LanguageManager.regex(msg, "\\{seeker\\}", e.getEntity().getName());
                    msg = LanguageManager.regex(msg, "\\{lives\\}", GameManager.seekerLives.get(e.getEntity()).toString());
                    PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, msg);
                    respawnQuick(e.getEntity());
                    return;
                }
            }
            if (noLivesLeft(e.getEntity())) {
                if (GameManager.useSideStats) {
                    PH.SBS.removeScoreboard(PH, e.getEntity());
                }
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, MessageBank.SEEKER_DEATH_MESSAGE.getMsg());
                GameManager.spectators.add(e.getEntity().getName());
                GameManager.seekers.remove(e.getEntity().getName());
                respawnQuick(e.getEntity());

            } else {
                String msg = MessageBank.SEEKER_LIVES_MESSAGE.getMsg();
                msg = LanguageManager.regex(msg, "\\{seeker\\}", e.getEntity().getName());
                msg = LanguageManager.regex(msg, "\\{lives\\}", GameManager.seekerLives.get(e.getEntity()).toString());
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, msg);
                respawnQuick(e.getEntity());
            }
        }
    }

    private boolean noLivesLeft(Player p) {
        if (GameManager.seekerLives.get(p) <= 1) {
            return true;
        } else {
            GameManager.seekerLives.put(p, GameManager.seekerLives.get(p) - 1);
            return false;
        }
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e) {
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent e) {
        if (GameManager.spectators.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            e.setCancelled(true);
            return;
        }
    }

    private boolean isLastHider() {
        int hidersleft = 0;
        for (String s : GameManager.hiders) {
            hidersleft++;
        }
        if (hidersleft == 1) {
            return true;
        }
        return false;
    }

    private boolean isLastSeeker() {
        int seekersleft = 0;
        for (String s : GameManager.seekers) {
            seekersleft++;
        }
        if (seekersleft == 1) {
            return true;
        }
        return false;
    }

    private void respawnQuick(final Player player) {
        PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

            @Override
            public void run() {
                PacketContainer packet = new PacketContainer(Packets.Client.CLIENT_COMMAND);
                packet.getIntegers().write(0, 1);

                try {
                    ProtocolLibrary.getProtocolManager().recieveClientPacket(player, packet);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot recieve packet.", e);
                }
            }
        }, 5L);

    }

    private void playHitMarkerEffect(Location loc) {
        if (GameManager.usingHitmarkers) {
            loc.setY(loc.getY() + 1);
            loc.getWorld().playEffect(loc, Effect.POTION_BREAK, 19);
        }
    }

    private void playerHitSoundEffect(Location loc) {
        if (GameManager.usingHitsounds) {
            loc.getWorld().playSound(loc, Sound.ORB_PICKUP, 1, 1);
        }
    }

    @EventHandler
    public void playerDamange(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player defend = (Player) e.getEntity();
            Player attacker = (Player) e.getDamager();
            if (GameManager.hiders.contains(defend.getName()) && GameManager.hiders.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.seekers.contains(defend.getName()) && GameManager.seekers.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.spectators.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.playersWaiting.contains(attacker.getName())) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.hiders.contains(defend.getName())) {
                playHitMarkerEffect(e.getEntity().getLocation());
                playerHitSoundEffect(e.getEntity().getLocation());
            }
        }
        if (e.getDamager() instanceof Projectile) {
            if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                Player attacker = (Player) ((Projectile) e.getDamager()).getShooter();
                if (e.getEntity() instanceof Player) {
                    Player defend = (Player) e.getEntity();
                    if (GameManager.hiders.contains(defend.getName()) && GameManager.hiders.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.seekers.contains(defend.getName()) && GameManager.seekers.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.spectators.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.playersWaiting.contains(attacker.getName())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (GameManager.hiders.contains(defend.getName())) {
                        playHitMarkerEffect(e.getEntity().getLocation());
                        playerHitSoundEffect(e.getEntity().getLocation());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerundis(PlayerUndisguiseEvent e) {
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            if (tempIgnoreUndisguise.contains(e.getPlayer())) {
                tempIgnoreUndisguise.remove(e.getPlayer());
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onlogin(final PlayerJoinEvent e) {
        if (GameManager.dedicated) {
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    GM.addPlayerToGame(e.getPlayer().getName());
                }
            }, 10L);
        }
        if (GameManager.playersQuit.contains(e.getPlayer().getName())) {
            GM.teleportToExit(e.getPlayer(), false);
            PropHuntMessaging.sendMessage(e.getPlayer(), MessageBank.QUIT_GAME_MESSAGE.getMsg());
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    PlayerManagement.gameRestorePlayer(e.getPlayer());

                }
            }, 20L);
            GameManager.playersQuit.remove(e.getPlayer().getName());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) throws IOException {
        if (GameManager.useSideStats) {
            PH.SBS.removeScoreboard(PH, e.getPlayer());
        }
        if (BungeeSettings.usingBungee && PH.getServer().getOnlinePlayers().length == 1) {
            final Pinger p = new Pinger(PH);
            p.sentData = true;
            p.sendServerDataEmpty();
            PH.getServer().getScheduler().scheduleSyncDelayedTask(PH, new Runnable() {

                @Override
                public void run() {
                    p.sentData = false;
                }
            }, 20L);
        }
        if (GameManager.dedicated) {
            if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
                GameManager.playersWaiting.remove(e.getPlayer().getName());
            }
        }
        if (GameManager.playersWaiting.contains(e.getPlayer().getName())) {
            GameManager.playersWaiting.remove(e.getPlayer().getName());
            GameManager.playersQuit.add(e.getPlayer().getName());
        }
        if (GameManager.hiders.contains(e.getPlayer().getName())) {
            GM.kickPlayer(e.getPlayer().getName());
            GameManager.playersQuit.add(e.getPlayer().getName());
        }
        if (GameManager.seekers.contains(e.getPlayer().getName())) {
            GM.kickPlayer(e.getPlayer().getName());
            GameManager.playersQuit.add(e.getPlayer().getName());
        }

    }

}

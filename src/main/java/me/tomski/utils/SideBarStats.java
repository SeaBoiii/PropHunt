package me.tomski.utils;

import java.util.HashMap;
import java.util.Map;

import me.tomski.prophunt.DisguiseManager;
import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class SideBarStats {

    Scoreboard board;
    PropHunt plugin;
    public static Map<Player, Scoreboard> playerBoards = new HashMap<Player, Scoreboard>();

    public SideBarStats(PropHunt plugin) {
        this.plugin = plugin;
    }

    public void updateBoard() {
        for (Player p : playerBoards.keySet()) {
            if (p == null || !p.isOnline()) {
                return;
            }
            if (GameManager.seekers.contains(p.getName())) {
                Objective ob = playerBoards.get(p).getObjective("seekerboard");
                if (ob == null) {
                    ob = playerBoards.get(p).registerNewObjective("seekerboard", "dummy");
                }
                Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
                score.setScore(GameManager.seekers.size());
                Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
                score1.setScore(GameManager.hiders.size());
                Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
                score2.setScore(GameManager.timeleft);

            }
            if (GameManager.hiders.contains(p.getName())) {
                Objective ob = playerBoards.get(p).getObjective("hiderboard");
                if (ob == null) {
                    ob = playerBoards.get(p).registerNewObjective("hiderboard", "dummy");
                }
                if (PropHunt.dc.isDisguised(p)) {
                    ob.setDisplayName(ChatColor.AQUA + DisguiseManager.parseDisguiseToName(PropHunt.dc.getDisguise(p)));
                }
                Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
                score.setScore(GameManager.seekers.size());

                Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
                score1.setScore(GameManager.hiders.size());

                Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
                score2.setScore(GameManager.timeleft);
                if (GameManager.usingSolidBlock) {
                    Score score3 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.ITALIC + plugin.ST.solid_Time_Translate + ":"));
                    if (SolidBlockTracker.solidBlocks.containsKey(p.getName())) {
                        p.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.ITALIC + plugin.ST.solid_Time_Translate + ":"));
                        Score newscore = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.ST.solid_Translate + ""));
                        newscore.setScore(1);
                    } else {
                        p.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.GOLD + "" + ChatColor.BOLD + plugin.ST.solid_Translate + ""));
                        score3.setScore(GameManager.solidBlockTime - SolidBlockTracker.movementTracker.get(p.getName()));
                    }
                }
            }
            if (GameManager.playersWaiting.contains(p.getName())) {
                Objective ob = playerBoards.get(p).getObjective("lobbyboard");
                if (ob == null) {
                    ob = playerBoards.get(p).registerNewObjective("lobbyboard", "dummy");
                }
                if (!GameManager.gameStatus) {
                    Score scorey = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + plugin.ST.starting_In_Translate + ""));
                    scorey.setScore(GameManager.currentLobbyTime);
                } else {
                    p.getScoreboard().resetScores(Bukkit.getOfflinePlayer(ChatColor.GREEN + plugin.ST.starting_In_Translate + ""));

                }
                Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
                score.setScore(GameManager.seekers.size());

                Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
                score1.setScore(GameManager.hiders.size());

                Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
                score2.setScore(GameManager.timeleft);


                Score score3 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + plugin.ST.player_Translate + ":"));
                score3.setScore(GameManager.playersWaiting.size());
                Score score4 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + plugin.ST.spectator_Translate + ":"));
                score4.setScore(GameManager.spectators.size());
            }
            if (GameManager.spectators.contains(p.getName())) {
                Objective ob = playerBoards.get(p).getObjective("lobbyboard");
                if (ob == null) {
                    ob = playerBoards.get(p).registerNewObjective("lobbyboard", "dummy");
                }
                Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
                score.setScore(GameManager.seekers.size());

                Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
                score1.setScore(GameManager.hiders.size());

                Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
                score2.setScore(GameManager.timeleft);


                Score score3 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + plugin.ST.player_Translate + ":"));
                score3.setScore(GameManager.playersWaiting.size());
                Score score4 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + plugin.ST.spectator_Translate + ":"));
                score4.setScore(GameManager.spectators.size());
            }

        }
    }


    public void removeScoreboard(PropHunt plugin, Player p) {
        if (p == null) {
            return;
        }
        if (!p.isOnline()) {
            return;
        }
        if (playerBoards.containsKey(p)) {
            playerBoards.remove(p);
            p.setScoreboard(plugin.getServer().getScoreboardManager().getNewScoreboard());
        }
    }


    public void addPlayerToGame(PropHunt plugin, Player p) {
        if (p == null || !p.isOnline()) {
            return;
        }
        if (GameManager.seekers.contains(p.getName())) {
            Scoreboard sb = plugin.getServer().getScoreboardManager().getNewScoreboard();
            Objective ob = sb.registerNewObjective("seekerboard", "dummy");
            ob.setDisplaySlot(DisplaySlot.SIDEBAR);
            ob.setDisplayName(ChatColor.GOLD + "PropHunt Stats");
            Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
            score.setScore(GameManager.seekers.size());

            Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
            score1.setScore(GameManager.hiders.size());

            Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
            score2.setScore(GameManager.timeleft);

            p.setScoreboard(sb);
            playerBoards.put(p, sb);
        }
        if (GameManager.hiders.contains(p.getName())) {
            Scoreboard sb = plugin.getServer().getScoreboardManager().getNewScoreboard();
            Objective ob = sb.registerNewObjective("hiderboard", "dummy");
            ob.setDisplaySlot(DisplaySlot.SIDEBAR);
            ob.setDisplayName(ChatColor.AQUA + DisguiseManager.parseDisguiseToName(PropHunt.dc.getDisguise(p)));
            Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
            score.setScore(GameManager.seekers.size());

            Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
            score1.setScore(GameManager.hiders.size());

            Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
            score2.setScore(GameManager.timeleft);

            p.setScoreboard(sb);
            playerBoards.put(p, sb);
        }

    }

    public void addPlayerToLobby(final PropHunt plugin, final Player p) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (p == null || !p.isOnline()) {
                    return;
                }
                Scoreboard sb = plugin.getServer().getScoreboardManager().getNewScoreboard();
                Objective ob = sb.registerNewObjective("lobbyboard", "dummy");
                ob.setDisplaySlot(DisplaySlot.SIDEBAR);
                ob.setDisplayName(ChatColor.GOLD + "PropHunt Stats");
                if (GameManager.gameStatus) {

                    Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + plugin.ST.seeker_Translate + ":"));
                    score.setScore(GameManager.seekers.size());

                    Score score1 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA + plugin.ST.hider_Translate + ":"));
                    score1.setScore(GameManager.hiders.size());

                    Score score2 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.WHITE + plugin.ST.time_Left_Translate + ":"));
                    score2.setScore(GameManager.timeleft);


                    Score score3 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + plugin.ST.player_Translate + ":"));
                    score3.setScore(GameManager.playersWaiting.size());
                    Score score4 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GRAY + plugin.ST.spectator_Translate + ":"));
                    score4.setScore(GameManager.spectators.size());


                    p.setScoreboard(sb);
                    playerBoards.put(p, sb);
                } else {
                    ob.setDisplayName(ChatColor.GOLD + "Arena: " + ChatColor.AQUA + GameManager.currentGameArena.getArenaName());

                    Score score = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + plugin.ST.starting_In_Translate + ""));
                    score.setScore(GameManager.currentLobbyTime);

                    Score score3 = ob.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW + plugin.ST.player_Translate + ":"));
                    score3.setScore(GameManager.playersWaiting.size());

                    p.setScoreboard(sb);
                    playerBoards.put(p, sb);
                }
            }
        }, 40L);

    }
}

package me.tomski.utils;

import java.io.IOException;

import me.tomski.prophunt.GameManager;
import me.tomski.prophunt.PropHunt;
import me.tomski.language.MessageBank;

public class GameTimer implements Runnable {

    public int ID;
    public PropHunt plugin;
    public double damage;
    public int startingtime;
    public int timeleft;
    public int interval;
    public GameManager GM;
    private int intervalcounter = 0;
    private SideBarStats sbs;

    public GameTimer(GameManager gm, PropHunt plugin, double seeker_damage, int interval, int startingtime, SideBarStats stats) {
        this.plugin = plugin;
        this.damage = seeker_damage;
        this.startingtime = startingtime;
        this.timeleft = startingtime;
        this.interval = interval;
        this.GM = gm;
        this.sbs = stats;
    }

    @Override
    public void run() {
        timeleft = timeleft - 1;
        if (timeleft == 30) {
            if (GameManager.blowDisguises && !PHScoreboard.disguisesBlown) {
                PHScoreboard.disguisesBlown = true;
                PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, MessageBank.DISGUISES_BLOWN.getMsg());
            }
            return;
        }
        intervalcounter = intervalcounter + 1;
        GameManager.timeleft = timeleft;


        if (timeleft == 0 || timeleft < 0) {
            try {
                GM.endGame(Reason.TIME, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        intervalPlayers();


        if (interval != 0 && intervalcounter % interval == 0) {
            intervalSeekers();
        }

    }

    private void intervalSeekers() {
        PropHuntMessaging.broadcastMessageToPlayers(GameManager.hiders, GameManager.seekers, timeleft + MessageBank.GAME_TIME_LEFT.getMsg());
        for (String seekers : GameManager.seekers) {
            if (plugin.getServer().getPlayer(seekers) != null && damage != 0) {
                plugin.getServer().getPlayer(seekers).damage(damage);
            }
        }
    }

    private void intervalPlayers() {
        for (String hiders : GameManager.hiders) {
            if (plugin.getServer().getPlayer(hiders) != null) {
                plugin.getServer().getPlayer(hiders).setLevel(timeleft);
                plugin.getServer().getPlayer(hiders).setExp((float) ((timeleft / GameManager.starting_time) > 1 ? (float) 1 : (float) timeleft / GameManager.starting_time));
            }
        }
        for (String seekers : GameManager.seekers) {

            if (plugin.getServer().getPlayer(seekers) != null) {
                plugin.getServer().getPlayer(seekers).setLevel(timeleft);
                plugin.getServer().getPlayer(seekers).setExp((((timeleft / GameManager.starting_time) > 1) ? (float) 1 : (float) timeleft / GameManager.starting_time));

            }
        }
    }

}

package me.tomski.utils;

import java.io.IOException;

import me.tomski.bungee.Pinger;

public class PingTimer implements Runnable {

    private Pinger pinger;


    public PingTimer(Pinger ping) {
        this.pinger = ping;
    }

    @Override
    public void run() {
        try {
            pinger.sendServerData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

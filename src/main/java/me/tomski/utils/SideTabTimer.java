package me.tomski.utils;

import me.tomski.prophunt.GameManager;

public class SideTabTimer implements Runnable {


    private SideBarStats sbs;


    public SideTabTimer(SideBarStats sbs) {
        this.sbs = sbs;
    }


    @Override
    public void run() {
        if (GameManager.useSideStats) {
            if (sbs != null) {
                sbs.updateBoard();
            }
        }
    }

}

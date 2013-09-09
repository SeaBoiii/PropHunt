package me.tomski.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;


public class LocationBox {

    private String configString;
    private Location location;

    public LocationBox(Location loc) {
        this.location = loc;
    }

    public LocationBox(String configString) {
        this.configString = configString;
    }

    public String box() {
        String worldName = location.getWorld().getName();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();
        return worldName + "|" + x + "|" + y + "|" + z + "|" + yaw + "|" + pitch;
    }

    public Location unBox() {
        String[] args = configString.split("\\|");
        String worldName = args[0];
        double x = Double.valueOf(args[1]);
        double y = Double.valueOf(args[2]);
        double z = Double.valueOf(args[3]);
        float yaw = Float.valueOf(args[4]);
        float pitch = Float.valueOf(args[5]);
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

}

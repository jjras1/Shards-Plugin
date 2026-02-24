package com.shards.plugin.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AFKZone {
    private String name;
    private String worldName;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int rewardPerMinute;

    public boolean contains(Location location) {
        if (!location.getWorld().getName().equals(worldName)) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= minX && x <= maxX &&
               y >= minY && y <= maxY &&
               z >= minZ && z <= maxZ;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public Location getMinLocation() {
        World world = getWorld();
        if (world == null) return null;
        return new Location(world, minX, minY, minZ);
    }

    public Location getMaxLocation() {
        World world = getWorld();
        if (world == null) return null;
        return new Location(world, maxX, maxY, maxZ);
    }
}
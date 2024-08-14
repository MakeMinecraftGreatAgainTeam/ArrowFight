package org.mmga.mycraft.arrowfight.runnable;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;

/**
 * Created On 2022/8/11 15:58
 *
 * @author wzp
 * @version 1.0.0
 */
public class ArrowRain extends BukkitRunnable {
    private final Location main;

    public ArrowRain(Location main) {
        this.main = main;
    }

    @Override
    public void run() {
        int blockX = main.getBlockX();
        int blockY = main.getBlockY() + 60;
        int blockZ = main.getBlockZ();
        World world = main.getWorld();
        ArrowFightPlugin arrowFightPlugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                Location loc = new Location(world, blockX + x, blockY, blockZ + z);
                new ClearArrow(loc.getWorld().spawnEntity(loc, EntityType.ARROW)).runTaskLater(arrowFightPlugin, 20 * 5);
            }
        }
    }

    private static class ClearArrow extends BukkitRunnable {
        private final Entity entity;

        private ClearArrow(Entity entity) {
            this.entity = entity;
        }

        @Override
        public void run() {
            entity.remove();
        }
    }
}

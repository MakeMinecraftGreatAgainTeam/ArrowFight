package org.mmga.mycraft.arrowfight.events;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created On 2022/8/10 19:26
 *
 * @author wzp
 * @version 1.0.0
 */
public class BlockPlace implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        Location location = blockPlaced.getLocation();
        int blockZ = location.getBlockZ();
        if (blockZ == 7) {
            event.setCancelled(true);
        }
    }
}

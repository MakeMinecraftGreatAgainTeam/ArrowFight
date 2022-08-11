package org.mmga.mycraft.arrowfight.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.runnable.AfterTickReplaceBlock;

import java.util.List;

/**
 * Created On 2022/8/11 14:04
 *
 * @author wzp
 * @version 1.0.0
 */
public class BlockExplode implements Listener {
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();
        JavaPlugin plugin = JavaPlugin.getPlugin(ArrowFightPlugin.class);
        for (Block block : blocks) {
            Material type = block.getType();
            if (Material.MOVING_PISTON.equals(type)) {
                new AfterTickReplaceBlock(block).runTaskLater(plugin, 2);
            }
        }

    }
}

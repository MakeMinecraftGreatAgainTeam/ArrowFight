package org.mmga.mycraft.arrowfight.runnable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created On 2022/8/11 15:18
 *
 * @author wzp
 * @version 1.0.0
 */
public class AfterTickReplaceBlock extends BukkitRunnable {
    private final Block block;
    private final Material material;

    public AfterTickReplaceBlock(Block block) {
        this.block = block;
        this.material = block.getType();
    }

    @Override
    public void run() {
        this.block.setType(material);
    }
}

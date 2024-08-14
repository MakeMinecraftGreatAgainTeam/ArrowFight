package org.mmga.mycraft.arrowfight.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.Collection;
import java.util.HashSet;

public class PlayerDropItem implements Listener {

    private static final Collection<Material> cannotDropItems;

    static {
        cannotDropItems = new HashSet<>();
        cannotDropItems.add(Material.BOW);
        cannotDropItems.add(Material.SLIME_BALL);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Material itemType = event.getItemDrop().getItemStack().getType();
        String worldName = event.getPlayer().getWorld().getName();
        if (worldName.equalsIgnoreCase("lobby")){
            return;
        }
        if (cannotDropItems.contains(itemType)) {
            event.setCancelled(true);
        }
    }
}

package org.mmga.mycraft.arrowfight.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.naming.NamingEnumeration;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onPlayerTryToLeaveGame(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        String worldName = player.getWorld().getName();
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (worldName.equalsIgnoreCase("lobby")){
            return;
        }
        if (mainHandItem.getType().equals(Material.SLIME_BALL)){
            player.performCommand("af leave");
        }
    }
}

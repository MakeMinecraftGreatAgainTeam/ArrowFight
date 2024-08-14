package org.mmga.mycraft.arrowfight.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.rmi.MarshalException;
import java.util.Random;

/**
 * Created On 2022/8/9 16:36
 *
 * @author wzp
 * @version 1.0.0
 */
public class BreakBlock implements Listener {
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (MapObject.PLAYERS.containsKey(player)) {
            Block block = event.getBlock();
            Material type = block.getType();
            if (event.isDropItems()) {
                if (Material.IRON_ORE.equals(type)) {
                    event.setDropItems(false);
                    Location location = block.getLocation();
                    World world = location.getWorld();
                    Item entity = (Item) world.spawnEntity(location, EntityType.DROPPED_ITEM);
                    entity.setItemStack(new ItemStack(Material.IRON_INGOT, new Random().nextInt(4) + 1));
                }
                if (Material.COAL.equals(type)){
                    event.setDropItems(false);
                    Location location = block.getLocation();
                    World world = location.getWorld();
                    Item entity = (Item) world.spawnEntity(location, EntityType.DROPPED_ITEM);
                    entity.setItemStack(new ItemStack(Material.COAL, new Random().nextInt(6) + 1));
                }
                if (Material.GOLD_ORE.equals(type)) {
                    event.setDropItems(false);
                    Location location = block.getLocation();
                    World world = location.getWorld();
                    Item entity = (Item) world.spawnEntity(location, EntityType.DROPPED_ITEM);
                    entity.setItemStack(new ItemStack(Material.GOLD_INGOT, new Random().nextInt(2) + 1));
                }
            }
        }
    }
}

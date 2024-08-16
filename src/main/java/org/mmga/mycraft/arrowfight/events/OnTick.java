package org.mmga.mycraft.arrowfight.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created On 2022/8/7 18:18
 *
 * @author wzp
 * @version 1.0.0
 */
public class OnTick extends BukkitRunnable {
    private static final int TICK_SEC = 20;
    private final Logger log;
    int tick = 0;
    public OnTick(){
        this.log = JavaPlugin.getPlugin(ArrowFightPlugin.class).getSLF4JLogger();
    }
    public static void replaceAir(Material block, Location location) {
        if (location.getBlock().getType().isAir()) {
            location.getBlock().setType(block);
        }
    }

    @Override
    public void run() {
        boolean runSec = tick % TICK_SEC == 0;
        tick++;
        for (MapObject mapObject : MapObject.GAMES.keySet()) {
            CopyOnWriteArrayList<GameObject> gameObjects = MapObject.GAMES.get(mapObject);
            if (gameObjects != null) {
                for (GameObject gameObject : gameObjects) {
                    gameObject.addTick();
                    //显示游戏内tick
                    //gameObject.sendToAll(ChatColor.RED + "tick:" + GameObject.tick, Sound.BLOCK_NOTE_BLOCK_BIT);
                    World copyWorld = gameObject.getCopyWorld();
                    Collection<Arrow> entitiesByClass = copyWorld.getEntitiesByClass(Arrow.class);
                    for (Arrow byClass : entitiesByClass) {
                        PotionData basePotionData = byClass.getBasePotionData();
                        PotionType type = basePotionData.getType();
                        Location arrowLocation = byClass.getLocation();
                        World world = arrowLocation.getWorld();
                        if (PotionType.LUCK.equals(type)) {
                            int blockX = arrowLocation.getBlockX();
                            int blockY = arrowLocation.getBlockY();
                            int blockZ = arrowLocation.getBlockZ();
                            for (int x = -3; x <= 3; x++) {
                                for (int y = -1; y <= 1; y++) {
                                    for (int z = -3; z <= 3; z++) {
                                        Block block = new Location(world, blockX + x, blockY + y, blockZ + z).getBlock();
                                        Material material = block.getType();
                                        if (Material.BEDROCK.equals(material)) {
                                            continue;
                                        }
                                        if (Material.MOVING_PISTON.equals(material)){
                                            continue;
                                        }
                                        block.setType(Material.AIR);
                                    }
                                }
                            }
                            boolean onGround = byClass.isOnGround();
                            if (onGround) {
                                byClass.remove();
                            }
                        }
                    }
                    Collection<Villager> entities = copyWorld.getEntitiesByClass(Villager.class);
                    for (Villager entity : entities) {
                        if (entity.getScoreboardTags().contains(EntityDamage.GAME_TAG)) {
                            for (PotionEffect activePotionEffect : entity.getActivePotionEffects()) {
                                PotionEffectType effectType = activePotionEffect.getType();
                                    entity.removePotionEffect(effectType);
                            }
                        }
                    }
                    if (runSec) {
                        gameObject.sec();
                    }
                }
            }
        }
    }
}

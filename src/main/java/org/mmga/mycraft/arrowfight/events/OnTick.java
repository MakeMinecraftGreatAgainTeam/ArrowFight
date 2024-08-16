package org.mmga.mycraft.arrowfight.events;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;
import org.mmga.mycraft.arrowfight.runnable.ArrowRain;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created On 2022/8/7 18:18
 *
 * @author wzp
 * @version 1.0.0
 */
public class OnTick extends BukkitRunnable {
    private static final int TICK_SEC = 20;
    private static final Logger log = LoggerFactory.getLogger(OnTick.class);
    int tick = 0;

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
                        boolean extended = basePotionData.isExtended();
                        boolean upgraded = basePotionData.isUpgraded();
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
                        if (PotionType.INSTANT_DAMAGE.equals(type)) {
                            if (byClass.isOnGround()) {
                                Location fireballLocation = new Location(world, arrowLocation.getX(), 170, arrowLocation.getZ());
                                Fireball fireball = world.spawn(fireballLocation, Fireball.class);
                                // 设置火球的速度、方向和爆炸威力
                                Vector velocity = new Vector(0, -1, 0);
                                Vector direction = new Vector(0, -1, 0);
                                double speed = 3;
                                fireball.setDirection(direction.multiply(speed));
                                fireball.setYield(8.0f);
                                fireball.setVelocity(velocity.multiply(speed));
                                log.info(direction.toString());
                                byClass.remove();
                            }
                        }
                        if (PotionType.POISON.equals(type)) {
                            boolean onGround = byClass.isOnGround();
                            if (onGround) {
                                if (!extended && !upgraded) {
                                    for (int i = 0; i < 10; i++) {
                                        arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.CREEPER);
                                    }
                                } else {
                                    for (int i = 0; i < 20; i++) {
                                        arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.ZOMBIE);
                                    }

                                }
                                byClass.remove();
                            }
                        }
                        if (PotionType.INVISIBILITY.equals(type)) {
                            boolean onGround = byClass.isOnGround();
                            if (onGround) {
                                if (!extended && !upgraded) {
                                    arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.PRIMED_TNT);
                                } else {
                                    ProjectileSource shooter = byClass.getShooter();
                                    if (shooter instanceof Player) {
                                        Map<Player, GameObject.GameTeam> teamPlayers = gameObject.getTeamPlayers();
                                        GameObject.GameTeam gameTeam = teamPlayers.get(shooter);
                                        boolean has = false;
                                        Location rainMain = null;
                                        for (Player player : teamPlayers.keySet()) {
                                            GameObject.GameTeam value = teamPlayers.get(player);
                                            if (!value.equals(gameTeam)) {
                                                player.sendMessage(ChatColor.RED + "TNT雨来袭，快找个地方躲起来！");
                                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                                if (new Random().nextBoolean() && !has) {
                                                    has = true;
                                                    rainMain = player.getLocation().clone();
                                                }
                                            }
                                        }
                                        if (!has) {
                                            MapObject map = gameObject.getMapObject();
                                            if (gameTeam.equals(GameObject.GameTeam.RED)) {
                                                rainMain = map.getBlueSpawn().clone();
                                            } else {
                                                rainMain = map.getRedSpawn().clone();
                                            }
                                        }
                                        rainMain.add(0, 60, 0);
                                        int blockX = rainMain.getBlockX();
                                        int blockY = rainMain.getBlockY();
                                        int blockZ = rainMain.getBlockZ();
                                        Location rain1 = new Location(copyWorld, blockX + 2, blockY, blockZ);
                                        Location rain2 = new Location(copyWorld, blockX - 2, blockY, blockZ);
                                        Location rain3 = new Location(copyWorld, blockX, blockY, blockZ + 2);
                                        Location rain4 = new Location(copyWorld, blockX, blockY, blockZ - 2);
                                        Location rain5 = new Location(copyWorld, blockX + 2, blockY, blockZ + 2);
                                        Location rain6 = new Location(copyWorld, blockX + 2, blockY, blockZ - 2);
                                        Location rain7 = new Location(copyWorld, blockX - 2, blockY, blockZ + 2);
                                        Location rain8 = new Location(copyWorld, blockX - 2, blockY, blockZ - 2);
                                        copyWorld.spawnEntity(rainMain, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain1, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain2, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain3, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain4, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain5, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain6, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain7, EntityType.PRIMED_TNT);
                                        copyWorld.spawnEntity(rain8, EntityType.PRIMED_TNT);
                                    }


                                }
                                byClass.remove();
                            }
                        }
                        if (PotionType.SLOW_FALLING.equals(type)) {
                            boolean onGround = byClass.isOnGround();
                            if (onGround) {
                                ArrowFightPlugin arrowFightPlugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
                                if (extended) {
                                    World worldCopied = gameObject.getCopyWorld();
                                    worldCopied.strikeLightning(arrowLocation);
//                                    LightningStrike spawn = worldCopied.spawn(arrowLocation, LightningStrike.class);
//                                    spawn.setLifeTicks(20);
                                } else {
                                    for (int i = 1; i < 3; i++) {
                                        new ArrowRain(arrowLocation).runTaskLater(arrowFightPlugin, i * 5);
                                    }
                                }
                                byClass.remove();
                            }
                        }
                        if (PotionType.FIRE_RESISTANCE.equals(type)) {
                            if (byClass.isOnGround()) {
                                Material material;
                                if (extended) {
                                    material = Material.FIRE;
                                } else {
                                    material = Material.LAVA;
                                }
                                int blockX = arrowLocation.getBlockX();
                                int blockY = arrowLocation.getBlockY();
                                int blockZ = arrowLocation.getBlockZ();
                                replaceAir(material, arrowLocation);
                                replaceAir(material, new Location(copyWorld, blockX + 1, blockY, blockZ));
                                replaceAir(material, new Location(copyWorld, blockX - 1, blockY, blockZ));
                                replaceAir(material, new Location(copyWorld, blockX, blockY, blockZ + 1));
                                replaceAir(material, new Location(copyWorld, blockX, blockY, blockZ - 1));
                                replaceAir(material, new Location(copyWorld, blockX + 1, blockY, blockZ + 1));
                                replaceAir(material, new Location(copyWorld, blockX + 1, blockY, blockZ - 1));
                                replaceAir(material, new Location(copyWorld, blockX - 1, blockY, blockZ + 1));
                                replaceAir(material, new Location(copyWorld, blockX - 1, blockY, blockZ - 1));
                                byClass.remove();
                            }
                        }
                        if (PotionType.SLOWNESS.equals(type)) {
                            if (byClass.isOnGround()) {
                                AreaEffectCloud entity = (AreaEffectCloud) world.spawnEntity(arrowLocation, EntityType.AREA_EFFECT_CLOUD);
                                entity.setBasePotionData(new PotionData(PotionType.POISON, true, false));
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
//                            entity.setAI(false);
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

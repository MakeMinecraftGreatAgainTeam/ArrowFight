package org.mmga.mycraft.arrowfight.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.util.Collection;
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
    int tick = 0;

    public static void fill(Location location, int x1, int y1, int z1, int x2, int y2, int z2) {
        World world = location.getWorld();
        Location location1 = location.add(x1, y1, z1);
        Location location2 = location.add(x2, y2, z2);
        fill(world, location1, location2);
    }

    public static void fill(World world, Location location1, Location location2) {
        fill(world, location1.getBlockX(), location1.getBlockY(), location1.getBlockZ(), location2.getBlockX(), location2.getBlockY(), location2.getBlockZ());
    }

    public static void fill(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    Material type = block.getType();
                    if (!Material.BEDROCK.equals(type)) {
                        block.setType(Material.AIR);
                    }
                }
            }
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
                    World copyWorld = gameObject.getCopyWorld();
                    Collection<Arrow> entitiesByClass = copyWorld.getEntitiesByClass(Arrow.class);
                    for (Arrow byClass : entitiesByClass) {
                        PotionData basePotionData = byClass.getBasePotionData();
                        PotionType type = basePotionData.getType();
                        boolean extended = basePotionData.isExtended();
                        boolean upgraded = basePotionData.isUpgraded();
                        Location arrowLocation = byClass.getLocation();
                        if (PotionType.LUCK.equals(type)) {
                            fill(arrowLocation, 1, 2, 1, -1, -2, -1);
                            fill(arrowLocation, 2, 1, 1, 2, -1, -1);
                            fill(arrowLocation, -2, 1, 1, -2, -1, -1);
                            fill(arrowLocation, 1, 1, 2, -1, -1, 2);
                            fill(arrowLocation, 1, 1, -2, -1, -1, -2);
                            boolean onGround = byClass.isOnGround();
                            if (onGround) {
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
                                        Location rain1 = new Location(copyWorld, blockX + 1, blockY, blockZ);
                                        Location rain2 = new Location(copyWorld, blockX - 1, blockY, blockZ);
                                        Location rain3 = new Location(copyWorld, blockX, blockY, blockZ + 1);
                                        Location rain4 = new Location(copyWorld, blockX, blockY, blockZ - 1);
                                        Location rain5 = new Location(copyWorld, blockX + 1, blockY, blockZ + 1);
                                        Location rain6 = new Location(copyWorld, blockX + 1, blockY, blockZ - 1);
                                        Location rain7 = new Location(copyWorld, blockX - 1, blockY, blockZ + 1);
                                        Location rain8 = new Location(copyWorld, blockX - 1, blockY, blockZ - 1);
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
                    }
                    if (runSec) {
                        gameObject.sec();
                    }
                }
            }
        }
    }
}

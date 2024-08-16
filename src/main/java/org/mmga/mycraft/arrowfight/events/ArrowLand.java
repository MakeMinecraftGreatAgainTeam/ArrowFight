package org.mmga.mycraft.arrowfight.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;
import org.mmga.mycraft.arrowfight.runnable.ArrowRain;

import java.util.Map;
import java.util.Random;

import static org.mmga.mycraft.arrowfight.events.OnTick.replaceAir;

public class ArrowLand implements Listener {

    @EventHandler
    public void onArrowLand(ProjectileHitEvent event){
        EntityType type = event.getEntity().getType();
        if (!EntityType.ARROW.equals(type)) {
            return;
        }
        Arrow arrow = (Arrow) event.getEntity();
        Location arrowLocation = arrow.getLocation();
        World world = arrow.getWorld();
        if (MapObject.GAMES.values().stream().noneMatch(e -> e.stream().map(GameObject::getCopyWorld).anyMatch(world::equals))) {
            return;
        }
        PotionData basePotionData = arrow.getBasePotionData();
        PotionType potionType = basePotionData.getType();
        if (event.getHitEntity() != null) {
            return;
        }
        boolean extended = basePotionData.isExtended();
        boolean upgraded = basePotionData.isUpgraded();
        switch (potionType) {
            case FIRE_RESISTANCE: {
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
                replaceAir(material, new Location(world, blockX + 1, blockY, blockZ));
                replaceAir(material, new Location(world, blockX - 1, blockY, blockZ));
                replaceAir(material, new Location(world, blockX, blockY, blockZ + 1));
                replaceAir(material, new Location(world, blockX, blockY, blockZ - 1));
                replaceAir(material, new Location(world, blockX + 1, blockY, blockZ + 1));
                replaceAir(material, new Location(world, blockX + 1, blockY, blockZ - 1));
                replaceAir(material, new Location(world, blockX - 1, blockY, blockZ + 1));
                replaceAir(material, new Location(world, blockX - 1, blockY, blockZ - 1));
                break;
            }
            case INSTANT_DAMAGE: {
                Location fireballLocation = new Location(world, arrowLocation.getX(), 170, arrowLocation.getZ());
                Fireball fireball = world.spawn(fireballLocation, Fireball.class);
                Vector velocity = new Vector(0, -1, 0);
                Vector direction = new Vector(0, -1, 0);
                double speed = 3;
                fireball.setVelocity(velocity.multiply(speed));
                fireball.setDirection(direction.multiply(speed));
                fireball.setYield(8.0f);
                break;
            }
            case POISON: {
                if (!extended && !upgraded) {
                    for (int i = 0; i < 10; i++) {
                        arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.CREEPER);
                    }
                } else {
                    for (int i = 0; i < 20; i++) {
                        arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.ZOMBIE);
                    }
                }
                break;
            }
            case INVISIBILITY: {
                if (!extended && !upgraded) {
                    arrowLocation.getWorld().spawnEntity(arrowLocation, EntityType.PRIMED_TNT);
                } else {
                    ProjectileSource shooter = arrow.getShooter();
                    if (shooter instanceof Player) {
                        GameObject gameObject = MapObject.PLAYERS.get(shooter);
                        Map<Player, GameObject.GameTeam> teamPlayers = gameObject.getTeamPlayers();
                        GameObject.GameTeam gameTeam = teamPlayers.get(shooter);
                        boolean has = false;
                        Location rainMain = null;
                        for (Player player : teamPlayers.keySet()) {
                            GameObject.GameTeam value = teamPlayers.get(player);
                            if (!value.equals(gameTeam)) {
                                player.sendMessage(Component.text("TNT雨来袭，快找个地方躲起来！", NamedTextColor.RED));
                                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                                if (new Random().nextBoolean() && !has) {
                                    has = true;
                                    rainMain = player.getLocation().clone();
                                }
                            }
                        }
                        if (!has) {
                            MapObject mapObject = gameObject.getMapObject();
                            rainMain = mapObject.getBlueSpawn();
                            if (GameObject.GameTeam.BLUE.equals(gameTeam)) {
                                rainMain = mapObject.getRedSpawn();
                            }
                        }
                        rainMain.add(0, 60, 0);
                        int blockX = rainMain.getBlockX();
                        int blockY = rainMain.getBlockY();
                        int blockZ = rainMain.getBlockZ();
                        Location rain1 = new Location(world, blockX + 2, blockY, blockZ);
                        Location rain2 = new Location(world, blockX - 2, blockY, blockZ);
                        Location rain3 = new Location(world, blockX, blockY, blockZ + 2);
                        Location rain4 = new Location(world, blockX, blockY, blockZ - 2);
                        Location rain5 = new Location(world, blockX + 2, blockY, blockZ + 2);
                        Location rain6 = new Location(world, blockX + 2, blockY, blockZ - 2);
                        Location rain7 = new Location(world, blockX - 2, blockY, blockZ + 2);
                        Location rain8 = new Location(world, blockX - 2, blockY, blockZ - 2);
                        world.spawnEntity(rainMain, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain1, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain2, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain3, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain4, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain5, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain6, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain7, EntityType.PRIMED_TNT);
                        world.spawnEntity(rain8, EntityType.PRIMED_TNT);
                    }
                }
                break;
            }
            case SLOW_FALLING: {
                ArrowFightPlugin plugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
                if (extended) {
                    world.strikeLightning(arrowLocation);
                } else {
                    for (int i = 1; i < 3; i++) {
                        new ArrowRain(arrowLocation).runTaskLater(plugin, i * 5);
                    }
                }
                break;
            }
            case SLOWNESS: {
                AreaEffectCloud entity = (AreaEffectCloud) world.spawnEntity(arrowLocation, EntityType.AREA_EFFECT_CLOUD);
                entity.setBasePotionData(new PotionData(PotionType.POISON, true, false));
                break;
            }
        }

        arrow.remove();
    }
}

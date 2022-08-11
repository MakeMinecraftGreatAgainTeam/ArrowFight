package org.mmga.mycraft.arrowfight.events;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.MapObject;
import org.mmga.mycraft.arrowfight.runnable.RemovePlayerPotionEffect;

/**
 * Created On 2022/8/9 14:57
 *
 * @author wzp
 * @version 1.0.0
 */
public class EntityDamage implements Listener {
    public static final String GAME_TAG = "game";

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        Entity damager = event.getDamager();
        EntityType damagerType = damager.getType();
        if (EntityType.VILLAGER.equals(type) && entity.getScoreboardTags().contains(GAME_TAG)) {
            event.setCancelled(true);
        }
        if (EntityType.PLAYER.equals(type) && EntityType.ARROW.equals(damagerType)) {
            Player player = (Player) entity;
            Arrow arrow = (Arrow) damager;
            if (MapObject.PLAYERS.containsKey(player)) {
                PotionData basePotionData = arrow.getBasePotionData();
                PotionType pType = basePotionData.getType();
                PotionEffectType effectType = pType.getEffectType();
                if (effectType != null) {
                    new RemovePlayerPotionEffect(player, effectType).runTaskLater(JavaPlugin.getPlugin(ArrowFightPlugin.class), 2);
                }
            }
        }
        if (EntityType.PLAYER.equals(type) && EntityType.PLAYER.equals(damagerType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByBlockEvent event) {
        Entity entity = event.getEntity();
        EntityType type = entity.getType();
        if (EntityType.VILLAGER.equals(type) && entity.getScoreboardTags().contains(GAME_TAG)) {
            event.setCancelled(true);
        }
    }
}

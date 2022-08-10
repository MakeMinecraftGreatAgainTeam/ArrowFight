package org.mmga.mycraft.arrowfight.events;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created On 2022/8/10 14:40
 *
 * @author wzp
 * @version 1.0.0
 */
public class RemovePlayerPotionEffect extends BukkitRunnable {
    private final Player player;
    private final PotionEffectType effect;

    public RemovePlayerPotionEffect(Player player, PotionEffectType effect) {
        this.player = player;
        this.effect = effect;
    }

    @Override
    public void run() {
        player.removePotionEffect(effect);
    }
}

package org.mmga.mycraft.arrowfight.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.util.Objects;

/**
 * Created On 2022/8/9 15:28
 *
 * @author wzp
 * @version 1.0.0
 */
public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(Component.text(""));
        ArrowFightPlugin plugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
        if (MapObject.PLAYERS.containsKey(player)) {
            GameObject gameObject = MapObject.PLAYERS.get(player);
            if (!gameObject.isStart()) {
                gameObject.getPlayers().remove(player);
                player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("lobby")));
                MapObject.PLAYERS.remove(player);
            } else {
                gameObject.getTeamPlayers().remove(player);
                gameObject.leavePlayers.add(player);
                PlayerJoin.PLAYERS.put(player.getName(), player);
            }
        }
    }
}

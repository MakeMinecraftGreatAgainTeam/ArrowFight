package org.mmga.mycraft.arrowfight.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

/**
 * Created On 2022/8/9 16:28
 *
 * @author wzp
 * @version 1.0.0
 */
public class PlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (MapObject.PLAYERS.containsKey(entity)) {
            GameObject gameObject = MapObject.PLAYERS.get(entity);
            if (gameObject.isStart()) {
                gameObject.getPlayers().remove(entity);
                gameObject.getTeamPlayers().remove(entity);
                MapObject.PLAYERS.remove(entity);
                entity.setScoreboard(entity.getServer().getScoreboardManager().getMainScoreboard());
                entity.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
}

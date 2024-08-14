package org.mmga.mycraft.arrowfight.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.util.Map;

/**
 * Created On 2022/8/11 20:11
 *
 * @author wzp
 * @version 1.0.0
 */
public class PlayerReSpawn implements Listener {
    @EventHandler
    public void onPlayerReSpawn(PlayerRespawnEvent event) {
        Player entity = event.getPlayer();
        if (MapObject.PLAYERS.containsKey(entity)) {
            GameObject gameObject = MapObject.PLAYERS.get(entity);
            if (gameObject.isStart()) {
                gameObject.getPlayers().remove(entity);
                gameObject.getDeathPlayers().add(entity);
                Map<Player, GameObject.GameTeam> teamPlayers = gameObject.getTeamPlayers();
                GameObject.GameTeam gameTeam = teamPlayers.get(entity);
                MapObject mapObject = gameObject.getMapObject();
                if (GameObject.GameTeam.BLUE.equals(gameTeam)) {
                    entity.teleport(mapObject.getBlueSpawn());
                } else {
                    entity.teleport(mapObject.getRedSpawn());
                }
                teamPlayers.remove(entity);
                MapObject.PLAYERS.remove(entity);
                entity.setScoreboard(entity.getServer().getScoreboardManager().getMainScoreboard());
                entity.setGameMode(GameMode.SPECTATOR);
            }
        }
    }
}

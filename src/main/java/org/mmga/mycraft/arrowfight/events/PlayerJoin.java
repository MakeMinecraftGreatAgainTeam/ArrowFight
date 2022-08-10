package org.mmga.mycraft.arrowfight.events;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created On 2022/8/9 15:42
 *
 * @author wzp
 * @version 1.0.0
 */
public class PlayerJoin implements Listener {
    public static final Map<String, Player> PLAYERS = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text(""));
        Player player = event.getPlayer();
        String name = player.getName();
        if (PLAYERS.containsKey(name)) {
            Player oldP = PLAYERS.get(name);
            PLAYERS.remove(name);
            GameObject gameObject = MapObject.PLAYERS.get(oldP);
            if (gameObject.isStart()) {
                gameObject.leavePlayers.remove(oldP);
                List<Player> players = gameObject.getPlayers();
                players.remove(oldP);
                players.add(player);
                Map<Player, GameObject.GameTeam> teamPlayers = gameObject.getTeamPlayers();
                teamPlayers.put(player, teamPlayers.get(oldP));
                teamPlayers.remove(oldP);
                MapObject.PLAYERS.put(player, gameObject);
                MapObject.PLAYERS.remove(oldP);
            }
        }
    }
}

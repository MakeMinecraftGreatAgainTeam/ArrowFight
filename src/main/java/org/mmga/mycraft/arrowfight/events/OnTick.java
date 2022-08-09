package org.mmga.mycraft.arrowfight.events;

import org.bukkit.scheduler.BukkitRunnable;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

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

    @Override
    public void run() {
        boolean runSec = tick % TICK_SEC == 0;
        tick++;
        for (MapObject mapObject : MapObject.GAMES.keySet()) {
            CopyOnWriteArrayList<GameObject> gameObjects = MapObject.GAMES.get(mapObject);
            if (gameObjects != null) {
                for (GameObject gameObject : gameObjects) {
                    gameObject.addTick();
                    if (runSec) {
                        gameObject.sec();
                    }
                }
            }
        }
    }
}

package org.mmga.mycraft.arrowfight.entities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.mmga.mycraft.arrowfight.utils.CopyOnWriteArrayListHashMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created On 2022/8/6 12:24
 *
 * @author wzp
 * @version 1.0.0
 */
public class MapObject implements ConfigurationSerializable {
    public static final CopyOnWriteArrayListHashMap<MapObject, GameObject> GAMES = new CopyOnWriteArrayListHashMap<>();
    public static final Map<Player, GameObject> PLAYERS = new HashMap<>();
    private static final int STRING_LONG = 4;
    private Integer min;
    private Integer max;
    private String world;
    private Location lobby;
    private Location redSpawn;
    private Location blueSpawn;
    private Location redVillagerSpawn;
    private Location blueVillagerSpawn;

    public MapObject() {
        min = 2;
        max = 16;
        world = null;
        lobby = null;
        redSpawn = null;
        blueSpawn = null;
        redVillagerSpawn = null;
        blueVillagerSpawn = null;
    }

    @SuppressWarnings("unused")
    public MapObject(Map<String, Object> map) {
        this.min = (Integer) map.get("min");
        this.max = (Integer) map.get("max");
        this.world = (String) map.get("world");
        this.lobby = (Location) map.get("lobby");
        this.redSpawn = (Location) map.get("red_spawn");
        this.blueSpawn = (Location) map.get("blue_spawn");
        this.redVillagerSpawn = (Location) map.get("red_villager_spawn");
        this.blueVillagerSpawn = (Location) map.get("blue_villager_spawn");
    }

    public boolean check() {
        return this.getMin() != null && this.getMax() != null && this.getWorld() != null && this.getLobby() != null && this.getRedSpawn() != null && this.getBlueSpawn() != null && this.getRedVillagerSpawn() != null && this.getBlueVillagerSpawn() != null;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public Location getRedSpawn() {
        return redSpawn;
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
    }

    public Location getBlueSpawn() {
        return blueSpawn;
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public Location getRedVillagerSpawn() {
        return redVillagerSpawn;
    }

    public void setRedVillagerSpawn(Location redVillagerSpawn) {
        this.redVillagerSpawn = redVillagerSpawn;
    }

    public Location getBlueVillagerSpawn() {
        return blueVillagerSpawn;
    }

    public void setBlueVillagerSpawn(Location blueVillagerSpawn) {
        this.blueVillagerSpawn = blueVillagerSpawn;
    }

    public void join(Player player) {
        if (PLAYERS.containsKey(player)) {
            player.sendMessage(ChatColor.RED + "你已经在一个房间中了！");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "正在寻找房间...");
        CopyOnWriteArrayList<GameObject> strings = GAMES.get(this);
        if (strings == null || strings.isEmpty()) {
            startGame(player);
        } else {
            boolean has = false;
            for (GameObject string : strings) {
                if (string.getPlayers().size() < this.getMax() && !string.isStart()) {
                    has = true;
                    string.join(player);
                    break;
                }
            }
            if (!has) {
                startGame(player);
            }
        }
    }

    public void startGame(Player player) {
        player.sendMessage(ChatColor.GREEN + "开始新游戏");
        GameObject gameObject = new GameObject(this.getRandomStringName(), new CopyOnWriteArrayList<>(), this);
        gameObject.join(player);
        GAMES.incr(this, gameObject);
    }

    public String getRandomStringName() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < STRING_LONG; i++) {
            Random random = new Random();
            int i1 = random.nextInt(4);
            char s;
            switch (i1) {
                case 0:
                    s = (char) (random.nextInt(57 - 48 + 1) + 48);
                    break;
                case 1:
                    s = (char) (random.nextInt(122 - 97 + 1) + 97);
                    break;
                case 2:
                    s = (char) (random.nextInt(90 - 65 + 1) + 65);
                    break;
                default:
                    s = '_';
                    break;
            }
            result.append(s);
        }
        return this.world + "-" + result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MapObject that = (MapObject) o;

        if (!Objects.equals(min, that.min)) {
            return false;
        }
        if (!Objects.equals(max, that.max)) {
            return false;
        }
        if (!Objects.equals(world, that.world)) {
            return false;
        }
        if (!Objects.equals(lobby, that.lobby)) {
            return false;
        }
        if (!Objects.equals(redSpawn, that.redSpawn)) {
            return false;
        }
        if (!Objects.equals(blueSpawn, that.blueSpawn)) {
            return false;
        }
        if (!Objects.equals(redVillagerSpawn, that.redVillagerSpawn)) {
            return false;
        }
        return Objects.equals(blueVillagerSpawn, that.blueVillagerSpawn);
    }

    @Override
    public int hashCode() {
        int result = min != null ? min.hashCode() : 0;
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (world != null ? world.hashCode() : 0);
        result = 31 * result + (lobby != null ? lobby.hashCode() : 0);
        result = 31 * result + (redSpawn != null ? redSpawn.hashCode() : 0);
        result = 31 * result + (blueSpawn != null ? blueSpawn.hashCode() : 0);
        result = 31 * result + (redVillagerSpawn != null ? redVillagerSpawn.hashCode() : 0);
        result = 31 * result + (blueVillagerSpawn != null ? blueVillagerSpawn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GameObject{" + "min=" + min +
                ", max=" + max +
                ", world='" + world + '\'' +
                ", lobby=" + lobby +
                ", redSpawn=" + redSpawn +
                ", blueSpawn=" + blueSpawn +
                ", redVillagerSpawn=" + redVillagerSpawn +
                ", blueVillagerSpawn=" + blueVillagerSpawn +
                '}';
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>(8);
        result.put("min", min);
        result.put("max", max);
        result.put("world", world);
        result.put("lobby", lobby);
        result.put("red_spawn", redSpawn);
        result.put("blue_spawn", blueSpawn);
        result.put("red_villager_spawn", redVillagerSpawn);
        result.put("blue_villager_spawn", blueVillagerSpawn);
        return result;
    }
}

package org.mmga.mycraft.arrowfight.entities;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created On 2022/8/7 17:54
 *
 * @author wzp
 * @version 1.0.0
 */
public class GameObject {
    private final static int SEC_TICK = 20;
    private final static int FIVE_SEC = 5;
    private final static int FIFTEEN_SEC = FIVE_SEC * 3;
    private final static int HALF_MIN_SEC = FIFTEEN_SEC * 2;
    private final static int MIN_SEC = HALF_MIN_SEC * 2;
    private final String name;
    private final List<Player> players;
    private final Map<Player, Team> teamPlayers = new HashMap<>();
    private final World copyWorld;
    private final MapObject mapObject;
    private int tick;
    private int beforeRemove = 100;
    private boolean done = false;
    private boolean isStart;

    public GameObject(String name, List<Player> players, MapObject mapObject) {
        this.name = name;
        this.players = players;
        this.mapObject = mapObject;
        this.isStart = false;
        this.tick = -1;
        ArrowFightPlugin plugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
        Server server = plugin.getServer();
        PluginManager pluginManager = server.getPluginManager();
        MultiverseCore core = (MultiverseCore) pluginManager.getPlugin("Multiverse-Core");
        assert core != null;
        MVWorldManager mvWorldManager = core.getMVWorldManager();
        mvWorldManager.cloneWorld(this.mapObject.getWorld(), this.name);
        this.copyWorld = mvWorldManager.getMVWorld(this.name).getCBWorld();
    }

    public void join(Player player) {
        MapObject.PLAYERS.put(player, this);
        this.players.add(player);
        Integer min = this.mapObject.getMin();
        int size = this.players.size();
        Location lobbyLoc = this.mapObject.getLobby().clone();
        lobbyLoc.setWorld(copyWorld);
        player.teleport(lobbyLoc);
        if ((this.mapObject.getMax() - min) >= size) {
            tick = (20 * 75) - 1;
        }
        for (Player player1 : players) {
            player1.sendMessage(ChatColor.GREEN + "玩家" + player.getName() + "加入了游戏   " + size + "/" + this.mapObject.getMax());
            if (min > size) {
                player1.sendMessage(ChatColor.YELLOW + "还差" + (min - size) + "人开始游戏！");
            }
        }
    }

    public void leave(Player player) {
        this.players.remove(player);
        MapObject.PLAYERS.remove(player);
        if (this.isStart) {
            this.teamPlayers.remove(player);
        }
        ArrowFightPlugin plugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
        Server server = plugin.getServer();
        player.teleport(Objects.requireNonNull(plugin.getConfig().getLocation("lobby")));
        player.setScoreboard(server.getScoreboardManager().getMainScoreboard());
        if (this.players.size() == 0) {
            MapObject.GAMES.decr(this.mapObject, this);
            Logger log = plugin.getSLF4JLogger();

            PluginManager pluginManager = server.getPluginManager();
            MultiverseCore core = (MultiverseCore) pluginManager.getPlugin("Multiverse-Core");
            assert core != null;
            MVWorldManager mvWorldManager = core.getMVWorldManager();
            boolean is = mvWorldManager.deleteWorld(this.name);
            if (is) {
                log.info(ChatColor.GREEN + "移除世界：" + name + "成功");
            } else {
                log.error(ChatColor.RED + "移除世界：" + name + "失败");
            }
        }
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
        if (isStart) {
            int b = 0;
            int r = 0;
            for (Player player : players) {
                boolean blue = false;
                if (b >= players.size() / 2) {
                    r++;
                    teamPlayers.put(player, Team.RED);
                } else if (r >= players.size() / 2) {
                    b++;
                    teamPlayers.put(player, Team.BLUE);
                    blue = true;
                } else {
                    Random random = new Random();
                    boolean isB = random.nextBoolean();
                    if (isB) {
                        b++;
                        teamPlayers.put(player, Team.BLUE);
                        blue = true;
                    } else {
                        r++;
                        teamPlayers.put(player, Team.RED);
                    }
                }
                Location clone;
                if (blue) {
                    clone = this.mapObject.getBlueSpawn().clone();
                } else {
                    clone = this.mapObject.getRedSpawn().clone();
                }
                clone.setWorld(this.copyWorld);
                player.teleport(clone);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GameObject that = (GameObject) o;

        if (!Objects.equals(name, that.name)) {
            return false;
        }
        if (!Objects.equals(players, that.players)) {
            return false;
        }
        return Objects.equals(mapObject, that.mapObject);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (players != null ? players.hashCode() : 0);
        result = 31 * result + (mapObject != null ? mapObject.hashCode() : 0);
        return result;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addTick() {
        if (players.size() >= this.mapObject.getMin()) {
            this.tick += 1;
            if (tick == 1) {
                sendToAll(ChatColor.GREEN + "1m30s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * HALF_MIN_SEC) {
                sendToAll(ChatColor.GREEN + "1m后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * MIN_SEC) {
                sendToAll(ChatColor.GREEN + "30s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC)) {
                sendToAll(ChatColor.GREEN + "15s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC)) {
                sendToAll(ChatColor.GREEN + "10s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC + FIVE_SEC)) {
                sendToAll(ChatColor.GREEN + "5s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC + FIVE_SEC + 1)) {
                sendToAll(ChatColor.GREEN + "4s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC + FIVE_SEC + 1 + 1)) {
                sendToAll(ChatColor.GREEN + "3s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC + FIVE_SEC + 1 + 1 + 1)) {
                sendToAll(ChatColor.GREEN + "2s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (MIN_SEC + FIFTEEN_SEC + FIVE_SEC + FIVE_SEC + 1 + 1)) {
                sendToAll(ChatColor.GREEN + "1s后开始游戏", Sound.BLOCK_NOTE_BLOCK_BIT);
            }
            if (tick == SEC_TICK * (HALF_MIN_SEC + MIN_SEC)) {
                sendToAll(ChatColor.GREEN + "游戏开始！", Sound.ENTITY_PLAYER_LEVELUP);
                this.setStart(true);
            }
        }
        if (!this.done && this.teamPlayers.size() == 1) {
            Team team = Team.RED;
            for (Player player : this.teamPlayers.keySet()) {
                team = this.teamPlayers.get(player);
                this.teamPlayers.remove(player);
            }
            sendToAll(team.color + team.name + ChatColor.GOLD + "获得胜利！", Sound.ENTITY_PLAYER_LEVELUP);
            this.done = true;
        }
        if (this.done) {
            this.beforeRemove--;
        }
        if (this.beforeRemove == 0) {
            for (Player player : players) {
                this.leave(player);
            }
        }
    }

    public void sec() {
        List<String> entries = new ArrayList<>();
        JavaPlugin plugin = JavaPlugin.getPlugin(ArrowFightPlugin.class);
        ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
        Scoreboard mainScoreboard = scoreboardManager.getMainScoreboard();
        if (!isStart) {
            if (players.size() < this.mapObject.getMin()) {
                entries.add(ChatColor.GREEN + "还差" + ChatColor.GOLD + (this.mapObject.getMin() - players.size()) + ChatColor.GREEN + "人开始游戏");
            } else {
                entries.add(ChatColor.GREEN + "还有" + ChatColor.GOLD + (90 - (this.tick / 20)) + "s" + ChatColor.GREEN + "开始游戏");
            }
        } else {
            entries.add(ChatColor.GREEN + "游戏已进行" + ChatColor.GOLD + ((this.tick / 20) - 90) + "s");
        }
        entries.add(ChatColor.DARK_AQUA + "地图：" + this.mapObject.getWorld());
        entries.add(ChatColor.DARK_PURPLE + "人数：" + this.players.size() + "/" + this.mapObject.getMax());
        List<Player> redPlayers = new ArrayList<>();
        List<Player> bluePlayers = new ArrayList<>();
        if (!teamPlayers.isEmpty()) {
            for (Player player1 : teamPlayers.keySet()) {
                Team value = teamPlayers.get(player1);
                if (value.equals(Team.BLUE)) {
                    bluePlayers.add(player1);
                } else {
                    redPlayers.add(player1);
                }
            }
        }
        boolean first = false;
        for (Player redPlayer : redPlayers) {
            if (!first) {
                entries.add(ChatColor.RED + ChatColor.BOLD.toString() + "红队：");
                first = true;
            }
            double health = redPlayer.getHealth();
            AttributeInstance healthBase = redPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert healthBase != null;
            double value = healthBase.getValue();
            entries.add(ChatColor.RED + redPlayer.getName() + "    " + ChatColor.GOLD + String.format("%.1f / %.1f", health, value));
        }
        first = false;
        for (Player bluePlayer : bluePlayers) {
            if (!first) {
                entries.add(ChatColor.BLUE + ChatColor.BOLD.toString() + "蓝队：");
                first = true;
            }
            double health = bluePlayer.getHealth();
            AttributeInstance healthBase = bluePlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            assert healthBase != null;
            double value = healthBase.getValue();
            entries.add(ChatColor.BLUE + bluePlayer.getName() + "    " + ChatColor.GOLD + String.format("%.1f / %.1f", health, value));
        }
        entries.add("房间号：" + ChatColor.BLUE + this.getName());
        for (Player player : this.getPlayers()) {
            int score = 0;
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard.equals(mainScoreboard)) {
                scoreboard = scoreboardManager.getNewScoreboard();
            }
            Objective gameInfo = scoreboard.getObjective("game_info");
            if (gameInfo != null) {
                gameInfo.unregister();
            }
            gameInfo = scoreboard.registerNewObjective("game_info", "dummy", Component.text(ChatColor.GOLD + ChatColor.BOLD.toString() + "弓箭大作战"));
            for (String entry : entries) {
                gameInfo.getScore(entry).setScore(score);
                score--;
            }
            gameInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
            player.setScoreboard(scoreboard);
        }
    }

    public void sendToAll(String content, Sound sound) {
        for (Player player : this.getPlayers()) {
            player.sendMessage(content);
            player.sendTitle(content, null, 10, 70, 20);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    enum Team {
        /**
         * 红队
         */
        RED("红队", ChatColor.RED),
        /**
         * 蓝队
         */
        BLUE("蓝队", ChatColor.BLUE);
        public final String name;
        public final ChatColor color;

        Team(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }
    }
}

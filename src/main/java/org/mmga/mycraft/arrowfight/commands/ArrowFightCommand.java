package org.mmga.mycraft.arrowfight.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mmga.mycraft.arrowfight.ArrowFightPlugin;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created On 2022/8/4 22:30
 *
 * @author wzp
 * @version 1.0.0
 */
public class ArrowFightCommand implements CommandExecutor, TabCompleter {
    private final static int SEC_TICK = 20;
    private final static int FIVE_SEC = 5;
    private final static int FIFTEEN_SEC = FIVE_SEC * 3;
    private final static int HALF_MIN_SEC = FIFTEEN_SEC * 2;
    private final static int MIN_SEC = HALF_MIN_SEC * 2;
    private static final String CREATE = "create";
    private static final String FORCESTART = "forcestart";
    private static final String JOIN = "join";
    private static final String SETTINGS = "settings";
    private static final String REMOVE = "remove";
    private static final String SET_MIN = "setMin";
    private static final String SET_MAX = "setMax";
    private static final String SET_WORLD = "setWorld";
    private static final String SET_LOBBY = "setLobby";
    private static final String SET_RED_SPAWN = "setRedSpawn";
    private static final String SET_BLUE_SPAWN = "setBlueSpawn";
    private static final String SET_RED_VILLAGER_SPAWN = "setRedVillagerSpawn";
    private static final String SET_BLUE_VILLAGER_SPAWN = "setBlueVillagerSpawn";
    private static final String HELP = "help";
    private static final String RELOAD = "reload";
    private static final String SAVE = "save";
    private static final String LEAVE = "leave";
    private static final String GLOBAL = "global";
    private static final String SET_MAIN_LOBBY = "setMainLobby";
    private final ArrowFightPlugin plugin;
    private final Server server;

    public ArrowFightCommand() {
        this.plugin = ArrowFightPlugin.getPlugin(ArrowFightPlugin.class);
        this.server = this.plugin.getServer();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int length = args.length;
        switch (length) {
            case 0:
                sendHelp(sender, HELP);
                break;
            case 1:
                args1(sender, args);
                break;
            case 2:
                args2(sender, args);
                break;
            case 3:
                args3(sender, args);
                break;
            case 4:
                args4(sender, args);
                break;
            case 6:
                args6(sender, args);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "错误的指令！");
                break;
        }
        return true;
    }

    private void args1(CommandSender sender, String... args) {
        switch (args[0]) {
            case RELOAD:
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "成功重载配置文件");
                break;
            case SAVE:
                plugin.saveConfig();
                sender.sendMessage(ChatColor.GREEN + "成功保存配置文件");
                break;
            case LEAVE:
                if (sender instanceof Player) {
                    GameObject ob = MapObject.PLAYERS.get(sender);
                    if (ob != null) {
                        MapObject.PLAYERS.get(sender).leave((Player) sender);
                        sender.sendMessage(ChatColor.GREEN + "成功退出游戏");
                    } else {
                        sender.sendMessage(ChatColor.RED + "你还没有加入游戏！");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "控制台无法退出游戏");
                }
                break;
            case JOIN:
                if (sender instanceof Player) {
                    ConfigurationSection games = plugin.getConfig().getConfigurationSection("game");
                    if (games != null) {
                        Set<String> keys = games.getKeys(false);
                        boolean check = false;
                        while (!check) {
                            Random random = new Random();
                            int i = random.nextInt(keys.size());
                            ArrayList<String> strings = new ArrayList<>(keys);
                            String s = strings.get(i);
                            MapObject serializable = games.getSerializable(s, MapObject.class);
                            assert serializable != null;
                            check = serializable.check();
                            if (!check) {
                                continue;
                            }
                            serializable.join((Player) sender);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "控制台无法加入游戏");
                }
                break;
            case FORCESTART:
                if (sender instanceof Player) {
                    if (!sender.isOp()) {
                        sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                        return;
                    } else {
                        GameObject ob = MapObject.PLAYERS.get(sender);
                        if (ob != null) {
                            ob.forceStart((Player) sender);
                        }
                    }
                    break;
                }
            default:
                sendHelp(sender, args[0]);
                break;
        }
    }

    private void args2(CommandSender sender, String... args) {
        String a0 = args[0];
        String a1 = args[1];
        switch (a0) {
            case CREATE:
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                    return;
                }
                if (checkNameLegality(sender, a1)) {
                    Object o = plugin.getConfig().get("game." + a1);
                    if (o == null) {
                        plugin.getConfig().set("game." + a1, new MapObject());
                        sender.sendMessage(ChatColor.GREEN + "成功创建地图！");
                    } else {
                        sender.sendMessage(ChatColor.RED + "地图名已存在");
                    }
                }
                break;
            case JOIN:
                if (sender instanceof Player) {
                    MapObject game = plugin.getConfig().getObject("game." + a1, MapObject.class);
                    if (game == null) {
                        sender.sendMessage(ChatColor.RED + "未知的地图！");
                    } else {
                        if (!game.check()) {
                            sender.sendMessage(ChatColor.RED + "此地图未设置完成！");
                        } else {
                            game.join((Player) sender);
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "控制台无法加入地图");
                }
                break;
            case SETTINGS:
                if (sender.isOp()) {
                    sendHelp(sender, SETTINGS);
                } else {
                    sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                }
                break;
            case REMOVE:
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                    return;
                }
                if (checkNameLegality(sender, a1)) {
                    plugin.getConfig().set("game." + a1, null);
                    plugin.saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "成功删除地图");
                }
                break;
            case HELP:
                sendHelp(sender, args[1]);
            default:
                sender.sendMessage(ChatColor.RED + "错误的指令！");
                break;
        }
    }

    private void args3(CommandSender sender, String... args) {
        String a0 = args[0];
        String a1 = args[1];
        String a2 = args[2];
        if (!SETTINGS.equals(a0)) {
            sender.sendMessage(ChatColor.RED + "错误的指令");
        } else {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                return;
            }
            if (checkNameLegality(sender, a1)) {
                MapObject mapObject = plugin.getConfig().getSerializable("game." + a1, MapObject.class);
                if (mapObject == null) {
                    sender.sendMessage(ChatColor.RED + "未知的地图！");
                } else {
                    switch (a2) {
                        case SET_WORLD:
                            if (sender instanceof Player) {
                                String name = ((Player) sender).getWorld().getName();
                                mapObject.setWorld(name);
                                sender.sendMessage(ChatColor.GREEN + "成功设置世界为" + name);
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setWorld <World> 来设置世界！");
                            }
                            break;
                        case SET_RED_SPAWN:
                            if (sender instanceof Player) {
                                Location location = ((Player) sender).getLocation();
                                mapObject.setRedSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置红队出生点为" + location.toVector());
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setRedSpawn <Pos> 来设置红队出生点！");
                            }
                            break;
                        case SET_RED_VILLAGER_SPAWN:
                            if (sender instanceof Player) {
                                Location location = ((Player) sender).getLocation();
                                mapObject.setRedVillagerSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置红队村民出生点为" + location.toVector());
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setRedVillagerSpawn <Pos> 来设置红队村民出生点！");
                            }
                            break;
                        case SET_BLUE_SPAWN:
                            if (sender instanceof Player) {
                                Location location = ((Player) sender).getLocation();
                                mapObject.setBlueSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置蓝队出生点为" + location.toVector());
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setBlueSpawn <Pos> 来设置蓝队出生点！");
                            }
                            break;
                        case SET_BLUE_VILLAGER_SPAWN:
                            if (sender instanceof Player) {
                                Location location = ((Player) sender).getLocation();
                                mapObject.setBlueVillagerSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置蓝队村民出生点为" + location.toVector());
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setBlueVillagerSpawn <Pos> 来设置蓝队村民出生点！");
                            }
                            break;
                        case SET_LOBBY:
                            if (sender instanceof Player) {
                                Location location = ((Player) sender).getLocation();
                                mapObject.setLobby(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置大厅出生点为" + location.toVector());
                            } else {
                                sender.sendMessage(ChatColor.RED + "控制台请使用/af settings <MapName> setLobby <Pos> 来设置大厅出生点！");
                            }
                            break;
                        default:
                            sender.sendMessage(ChatColor.RED + "错误的指令！");
                            break;
                    }
                    plugin.getConfig().set("game." + a1, mapObject);
                }
            }
        }
    }

    private void args4(CommandSender sender, String... args) {
        String a0 = args[0];
        String a1 = args[1];
        String a2 = args[2];
        String a3 = args[3];
        if (!SETTINGS.equals(a0)) {
            sender.sendMessage(ChatColor.RED + "错误的指令");
        } else {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                return;
            }
            if (checkNameLegality(sender, a1)) {
                MapObject mapObject = plugin.getConfig().getSerializable("game." + a1, MapObject.class);
                if (mapObject == null) {
                    sender.sendMessage(ChatColor.RED + "未知的地图！");
                } else {
                    switch (a2) {
                        case SET_MIN:
                            mapObject.setMin(Integer.parseInt(a3));
                            sender.sendMessage(ChatColor.GREEN + "成功设置最少开始人数为：" + a3);
                            break;
                        case SET_MAX:
                            mapObject.setMax(Integer.parseInt(a3));
                            sender.sendMessage(ChatColor.GREEN + "成功设置最大开始人数为：" + a3);
                            break;
                        default:
                            sender.sendMessage(ChatColor.RED + "错误的指令！");
                            break;
                    }
                    plugin.getConfig().set("game." + a1, mapObject);
                }
            }
        }
    }

    private World getWorld(CommandSender sender) {
        World world;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = server.getWorld("world");
        }
        return world;
    }

    private void args6(CommandSender sender, String... args) {
        String a0 = args[0];
        String a1 = args[1];
        String a2 = args[2];
        int x = Integer.parseInt(args[3]);
        int y = Integer.parseInt(args[4]);
        int z = Integer.parseInt(args[5]);
        if (!SETTINGS.equals(a0)) {
            sender.sendMessage(ChatColor.RED + "错误的指令");
        } else {
            if (!sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "你没有权限使用此指令");
                return;
            }
            if (GLOBAL.equals(a1)) {
                if (SET_MAIN_LOBBY.equals(a2)) {
                    World world;
                    if (sender instanceof Player) {
                        world = ((Player) sender).getWorld();
                    } else {
                        world = server.getWorld("world");
                    }
                    Location location = new Location(world, x, y, z);
                    plugin.getConfig().set("lobby", location);
                } else {
                    sender.sendMessage(ChatColor.RED + "错误的指令");
                }
            } else {
                if (checkNameLegality(sender, a1)) {
                    MapObject mapObject = plugin.getConfig().getSerializable("game." + a1, MapObject.class);
                    if (mapObject == null) {
                        sender.sendMessage(ChatColor.RED + "未知的地图！");
                    } else {
                        switch (a2) {
                            case SET_LOBBY:
                                World world = getWorld(sender);
                                Location location = new Location(world, x, y, z);
                                mapObject.setLobby(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置大厅出生点为" + location.toVector());
                                break;
                            case SET_RED_SPAWN:
                                world = getWorld(sender);
                                location = new Location(world, x, y, z);
                                mapObject.setRedSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置红队出生点为" + location.toVector());
                                break;
                            case SET_RED_VILLAGER_SPAWN:
                                world = getWorld(sender);
                                location = new Location(world, x, y, z);
                                mapObject.setRedVillagerSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置红队村民出生点为" + location.toVector());
                                break;
                            case SET_BLUE_SPAWN:
                                world = getWorld(sender);
                                location = new Location(world, x, y, z);
                                mapObject.setBlueSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置蓝队出生点为" + location.toVector());
                                break;
                            case SET_BLUE_VILLAGER_SPAWN:
                                world = getWorld(sender);
                                location = new Location(world, x, y, z);
                                mapObject.setBlueVillagerSpawn(location);
                                sender.sendMessage(ChatColor.GREEN + "成功设置蓝队村民出生点为" + location.toVector());
                                break;
                            default:
                                sender.sendMessage(ChatColor.RED + "错误的指令！");
                                break;
                        }
                        plugin.getConfig().set("game." + a1, mapObject);
                    }
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        int length = args.length;
        List<String> result = new ArrayList<>();
        switch (length) {
            case 1:
                args1Tab(sender, result);
                break;
            case 2:
                args2Tab(sender, result, args);
                break;
            case 3:
                args3Tab(sender, result, args);
                break;
            case 4:
                args4Tab(result, sender, args);
                break;
            case 5:
                args5Tab(result, sender, args);
                break;
            case 6:
                args6Tab(result, sender, args);
                break;
            default:
                break;
        }
        List<String> r = new ArrayList<>();
        for (String s : result) {
            if (s.contains(args[args.length - 1])) {
                r.add(s);
            }
        }
        return r;
    }

    private void args1Tab(CommandSender sender, List<String> result) {
        if (sender.isOp()) {
            result.add(CREATE);
            result.add(SETTINGS);
            result.add(REMOVE);
            result.add(RELOAD);
            result.add(SAVE);
            result.add(FORCESTART);
        }
        result.add(JOIN);
        result.add(HELP);
        result.add(LEAVE);
    }

    private void args2Tab(CommandSender sender, List<String> result, String... args) {
        String base = args[0];
        if (SETTINGS.equals(base) || REMOVE.equals(base) || JOIN.equals(base)) {
            ConfigurationSection games = plugin.getConfig().getConfigurationSection("game");
            if (SETTINGS.equals(base) || REMOVE.equals(base)) {
                if (sender.isOp()) {
                    if (games != null) {
                        result.addAll(games.getKeys(false));
                    }
                    if (SETTINGS.equals(base)) {
                        result.add(GLOBAL);
                    }
                }
            } else {
                if (games != null) {
                    result.addAll(games.getKeys(false));
                }
            }

        }
    }

    private void args3Tab(CommandSender sender, List<String> result, String @NotNull ... args) {
        if (sender.isOp()) {
            String base = args[0];
            if (SETTINGS.equals(base)) {
                String game = args[1];
                if (GLOBAL.equals(game)) {
                    result.add(SET_MAIN_LOBBY);
                } else {
                    result.add(SET_MIN);
                    result.add(SET_MAX);
                    result.add(SET_WORLD);
                    result.add(SET_LOBBY);
                    result.add(SET_RED_SPAWN);
                    result.add(SET_BLUE_SPAWN);
                    result.add(SET_RED_VILLAGER_SPAWN);
                    result.add(SET_BLUE_VILLAGER_SPAWN);
                }
            }
        }

    }

    private void args4Tab(List<String> result, CommandSender sender, String... args) {
        if (sender.isOp()) {
            String base = args[0];
            if (SETTINGS.equals(base)) {
                String model = args[2];
                if (SET_WORLD.equals(model)) {
                    for (World world : server.getWorlds()) {
                        result.add(world.getName());
                    }
                }
                if (SET_LOBBY.equals(model) || SET_RED_SPAWN.equals(model) || SET_RED_VILLAGER_SPAWN.equals(model) || SET_BLUE_SPAWN.equals(model) || SET_BLUE_VILLAGER_SPAWN.equals(model) || SET_MAIN_LOBBY.equals(model)) {
                    int x = 0;
                    if (sender instanceof Player) {
                        Block targetBlock = ((Player) sender).getTargetBlock(8);
                        if (targetBlock != null) {
                            x = targetBlock.getLocation().getBlockX();
                        }
                    }
                    result.add(String.valueOf(x));
                }
            }
        }
    }

    private void args5Tab(List<String> result, CommandSender sender, String... args) {
        if (sender.isOp()) {
            String base = args[0];
            if (SETTINGS.equals(base)) {
                String model = args[2];
                if (SET_LOBBY.equals(model) || SET_RED_SPAWN.equals(model) || SET_RED_VILLAGER_SPAWN.equals(model) || SET_BLUE_SPAWN.equals(model) || SET_BLUE_VILLAGER_SPAWN.equals(model) || SET_MAIN_LOBBY.equals(model)) {
                    int y = 0;
                    if (sender instanceof Player) {
                        Block targetBlock = ((Player) sender).getTargetBlock(8);
                        if (targetBlock != null) {
                            y = targetBlock.getLocation().getBlockY();
                        }
                    }
                    result.add(String.valueOf(y));
                }
            }
        }
    }

    private void args6Tab(List<String> result, CommandSender sender, String... args) {
        if (sender.isOp()) {
            String base = args[0];
            if (SETTINGS.equals(base)) {
                String model = args[2];
                if (SET_LOBBY.equals(model) || SET_RED_SPAWN.equals(model) || SET_RED_VILLAGER_SPAWN.equals(model) || SET_BLUE_SPAWN.equals(model) || SET_BLUE_VILLAGER_SPAWN.equals(model) || SET_MAIN_LOBBY.equals(model)) {
                    int z = 0;
                    if (sender instanceof Player) {
                        Block targetBlock = ((Player) sender).getTargetBlock(8);
                        if (targetBlock != null) {
                            z = targetBlock.getLocation().getBlockZ();
                        }
                    }
                    result.add(String.valueOf(z));
                }
            }
        }
    }

    public void sendHelp(CommandSender sender, String module) {
        sender.sendMessage("[ArrowFight] 指令系统-帮助");
        switch (module) {
            case CREATE:
                createHelp(sender);
                break;
            case JOIN:
                joinHelp(sender);
                break;
            case SETTINGS:
                settingsHelp(sender);
                break;
            case REMOVE:
                removeHelp(sender);
                break;
            case HELP:
                allHelp(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "错误的指令！");
                break;
        }
    }

    public void createHelp(CommandSender player) {
        if (player.isOp()) {
            player.sendMessage("/af create <MapName> 创建一个地图");
        } else {
            player.sendMessage(ChatColor.RED + "错误的指令");
        }
    }

    public void removeHelp(CommandSender player) {
        if (player.isOp()) {
            player.sendMessage("/af remove <MapName> 删除一个地图");
        } else {
            player.sendMessage(ChatColor.RED + "错误的指令");
        }
    }

    public void settingsHelp(CommandSender player) {
        if (player.isOp()) {
            player.sendMessage("/af settings <MapName> setMin <Num> 设置最小玩家数（默认2）");
            player.sendMessage("/af settings <MapName> setMax <Num> 设置最大玩家数（默认16）");
            player.sendMessage("/af settings <MapName> setWorld [World] 设置游戏世界在当前世界或世界World");
            player.sendMessage("/af settings <MapName> setLobby [Pos] 设置大厅生成位置在玩家位置或Pos的位置");
            player.sendMessage("/af settings <MapName> setRedSpawn [Pos] 设置红队生成位置在玩家位置或Pos的位置");
            player.sendMessage("/af settings <MapName> setBlueSpawn [Pos] 设置蓝队生成位置在玩家位置或Pos的位置");
            player.sendMessage("/af settings <MapName> setRedVillagerSpawn [Pos] 设置红队生成位置在玩家位置或Pos的位置");
            player.sendMessage("/af settings <MapName> setBlueVillagerSpawn [Pos] 设置蓝队生成位置在玩家位置或Pos的位置");
            player.sendMessage("/af settings global setMainLobby [Pos] 设置主大厅位置（即玩家游戏结束后被传送至的位置）");
        } else {
            player.sendMessage(ChatColor.RED + "错误的指令");
        }
    }

    public void joinHelp(CommandSender player) {
        player.sendMessage("/af join <MapName> 加入一场游戏");
    }

    public void allHelp(CommandSender player) {
        if (player.isOp()) {
            player.sendMessage("/af create 关于创建地图的部分指令");
            player.sendMessage("/af settings 关于地图设置的部分指令");
            player.sendMessage("/af remove 关于移除地图的部分指令");
            player.sendMessage("/af save 保存游戏设置到配置文件");
        }
        player.sendMessage("/af join 关于加入游戏的部分指令");
        player.sendMessage("/af leave 退出这场游戏");
        player.sendMessage("/af help 获取此帮助");
    }

    public boolean checkNameLegality(CommandSender sender, String name) {
        boolean result = !name.contains("~") || name.contains("!") || name.contains("|") || name.contains("/") || name.contains(".") || name.contains("\\") || name.contains("*") || name.equals(GLOBAL);
        if (!result) {
            sender.sendMessage(ChatColor.RED + "非法的地图名！");
        }
        return result;
    }
}
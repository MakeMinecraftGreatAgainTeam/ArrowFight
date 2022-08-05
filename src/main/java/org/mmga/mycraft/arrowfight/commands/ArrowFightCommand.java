package org.mmga.mycraft.arrowfight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created On 2022/8/4 22:30
 *
 * @author wzp
 * @version 1.0.0
 */
public class ArrowFightCommand implements CommandExecutor, TabCompleter {
    public static final String CREATE = "create";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int length = args.length;
        if (length == 0) {

        }
        return true;
    }

    public void sendHelp(CommandSender sender, String module) {
        sender.sendMessage("[ArrowFight] 指令系统-帮助");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        int length = args.length;
        List<String> result = new ArrayList<>();
        if (length == 0) {
            result.add(CREATE);
        }
        if (length == 1) {

        }
        return result;
    }

    public void createHelp(CommandSender player) {
        player.sendMessage("/af create <GameName> 创建一个游戏");
    }

    public void removeHelp(CommandSender player) {
        player.sendMessage("/af delete <GameName> 删除一个游戏");
    }

    public void settingsHelp(CommandSender player) {
        player.sendMessage("/af settings setMin 设置最小玩家数");
        player.sendMessage("/af settings ");
    }

    public void joinHelp(CommandSender player) {
        player.sendMessage("/af setting <GameName>加入一场游戏");
    }
}
package org.mmga.mycraft.arrowfight;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mmga.mycraft.arrowfight.commands.ArrowFightCommand;
import org.slf4j.Logger;

/**
 * Created on 2022/8/3 16:56:17
 *
 * @author wzp
 * @version 1.0.0
 */

public final class ArrowFightPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger log = super.getSLF4JLogger();
        log.info(ChatColor.GREEN + "Start Loading...");
        boolean b = this.checkEnvironment();
        if (b) {
            log.error(ChatColor.RED + "缺少插件依赖！");
            disableMe();
            return;
        }
        this.hookPapi();
        PluginCommand af = super.getCommand("af");
        if (af == null) {
            log.error(ChatColor.RED + "指令af注册失败！");
            disableMe();
            return;
        }
        ArrowFightCommand arrowFightCommand = new ArrowFightCommand();
        af.setExecutor(arrowFightCommand);
        af.setTabCompleter(arrowFightCommand);
        log.info("指令af注册成功！");
    }

    @Override
    public void onDisable() {
        Logger log = super.getSLF4JLogger();
        log.info("Start Disable");
    }

    public void disableMe() {
        super.getPluginLoader().disablePlugin(this);
    }

    public void hookPapi() {
    }

    public boolean checkEnvironment() {
        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        Plugin papi = pluginManager.getPlugin("PlaceholderAPI");
        Plugin mv = pluginManager.getPlugin("Multiverse-Core");
        return papi == null || mv == null;
    }
}

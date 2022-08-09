package org.mmga.mycraft.arrowfight;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mmga.mycraft.arrowfight.commands.ArrowFightCommand;
import org.mmga.mycraft.arrowfight.entities.GameObject;
import org.mmga.mycraft.arrowfight.entities.MapObject;
import org.mmga.mycraft.arrowfight.events.OnTick;
import org.slf4j.Logger;

import java.util.concurrent.CopyOnWriteArrayList;

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
        ConfigurationSerialization.registerClass(MapObject.class);
        super.saveDefaultConfig();
        new OnTick().runTaskTimer(this, 0, 1);
    }

    @Override
    public void onDisable() {
        Logger log = super.getSLF4JLogger();
        log.info("Start Disable");
        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        MultiverseCore core = (MultiverseCore) pluginManager.getPlugin("Multiverse-Core");
        assert core != null;
        MVWorldManager mvWorldManager = core.getMVWorldManager();
        for (CopyOnWriteArrayList<GameObject> value : MapObject.GAMES.values()) {
            for (GameObject gameObject : value) {
                String name = gameObject.getName();
                boolean is = mvWorldManager.deleteWorld(name);
                if (is) {
                    log.info(ChatColor.GREEN + "移除世界：" + name + "成功");
                } else {
                    log.error(ChatColor.RED + "移除世界：" + name + "失败");
                }

            }
        }
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

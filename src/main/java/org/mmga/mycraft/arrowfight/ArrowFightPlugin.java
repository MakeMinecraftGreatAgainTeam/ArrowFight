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
import org.mmga.mycraft.arrowfight.events.*;
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
        log.info("{}Start Loading...", ChatColor.GREEN);
        boolean b = this.checkEnvironment();
        if (b) {
            log.error("{}缺少插件依赖！", ChatColor.RED);
            disableMe();
            return;
        }
        this.hookPapi();
        PluginCommand af = super.getCommand("af");
        if (af == null) {
            log.error("{}指令af注册失败！", ChatColor.RED);
            disableMe();
            return;
        }
        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new EntityDamage(), this);
        pluginManager.registerEvents(new PlayerLeave(), this);
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new BreakBlock(), this);
        pluginManager.registerEvents(new BlockExplode(), this);
        pluginManager.registerEvents(new PlayerReSpawn(), this);
        pluginManager.registerEvents(new PlayerInteract(), this);
        pluginManager.registerEvents(new PlayerDropItem(), this);
        pluginManager.registerEvents(new ArrowLand(), this);
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

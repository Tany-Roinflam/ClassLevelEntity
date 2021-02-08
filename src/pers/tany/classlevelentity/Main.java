package pers.tany.classlevelentity;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pers.tany.classlevelentity.command.Commands;
import pers.tany.classlevelentity.listenevent.Events;
import pers.tany.classlevelentity.task.SaveTask;
import pers.tany.classlevelentity.util.CommonlyUtil;
import pers.tany.classlevelentity.util.ConfigUtil;

import java.io.File;
import java.util.HashSet;

public class Main extends JavaPlugin {
    public static Plugin plugin;
    public static Economy economy = null;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getConsoleSender().sendMessage("§f[§7ClassLevelEntity§f]§a插件已加载");
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveResource("config.yml", false);
        }
        if (!new File(getDataFolder(), "data.yml").exists()) {
            saveResource("data.yml", false);
        }
        if (!new File(getDataFolder(), "list.yml").exists()) {
            saveResource("list.yml", false);
        }
        if (!new File(getDataFolder(), "message.yml").exists()) {
            saveResource("message.yml", false);
        }
        this.getCommand("ClassLevelEntity").setExecutor(new Commands());
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        new SaveTask().runTaskTimerAsynchronously(Main.plugin, 1200, 1200);
        new BukkitRunnable() {

            @Override
            public void run() {
                for (String s : ConfigUtil.list.getConfigurationSection("Info").getKeys(false)) {
                    Events.levelList.put(s, ConfigUtil.list.getString("Info." + s));
                }
            }

        }.runTaskAsynchronously(this);

        economy = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }

    @Override
    public void onDisable() {
        for (Entity entity : new HashSet<>(Events.entitySet)) {
            if (!entity.isValid()) {
                Events.levelList.remove(entity.getUniqueId().toString());
                Events.entitySet.remove(entity);
            }
        }
        for (String s : new HashSet<>(Events.levelList.keySet())) {
            if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(Events.levelList.get(s))) {
                Events.levelList.remove(s);
            }
        }
        for (String s : ConfigUtil.list.getConfigurationSection("Info").getKeys(false)) {
            ConfigUtil.list.set("Info." + s, null);
        }
        for (String uuid : Events.levelList.keySet()) {
            ConfigUtil.list.set("Info." + uuid, Events.levelList.get(uuid));
        }
        CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.list, "list", false);
        Bukkit.getConsoleSender().sendMessage("§f[§7ClassLevelEntity§f]§c插件已卸载");
    }
}

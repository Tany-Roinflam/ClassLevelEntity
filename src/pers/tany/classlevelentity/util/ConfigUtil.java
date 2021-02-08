package pers.tany.classlevelentity.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pers.tany.classlevelentity.Main;

import java.io.File;

public class ConfigUtil {
    public static File file;
    public static FileConfiguration config;
    public static FileConfiguration data;
    public static FileConfiguration list;
    public static FileConfiguration message;

    static {
        file = new File(Main.plugin.getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        file = new File(Main.plugin.getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(file);
        file = new File(Main.plugin.getDataFolder(), "list.yml");
        list = YamlConfiguration.loadConfiguration(file);
        file = new File(Main.plugin.getDataFolder(), "message.yml");
        message = YamlConfiguration.loadConfiguration(file);
    }
}

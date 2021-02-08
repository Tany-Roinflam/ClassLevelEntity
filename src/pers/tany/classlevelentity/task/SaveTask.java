package pers.tany.classlevelentity.task;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import pers.tany.classlevelentity.Main;
import pers.tany.classlevelentity.listenevent.Events;
import pers.tany.classlevelentity.util.CommonlyUtil;
import pers.tany.classlevelentity.util.ConfigUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SaveTask extends BukkitRunnable {
    HashMap<String, Integer> noFountEntityList = new HashMap<>();

    @Override
    public void run() {
        Set<String> set = new HashSet<>();
        if (Bukkit.getOnlinePlayers().size() >= 1) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        set.add(livingEntity.getUniqueId().toString());
                    }
                }
            }
            for (String s : new HashSet<>(Events.levelList.keySet())) {
                if (!set.contains(s)) {
                    int number = noFountEntityList.getOrDefault(s, 0) + 1;
                    noFountEntityList.put(s, number);
                    if (noFountEntityList.get(s) > 1024) {
                        noFountEntityList.remove(s);
                        Events.levelList.remove(s);
                    }
                } else {
                    noFountEntityList.remove(s);
                }
            }
        }
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
        CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.list, "list", true);
    }
}

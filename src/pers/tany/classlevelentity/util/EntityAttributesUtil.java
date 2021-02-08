package pers.tany.classlevelentity.util;

import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTListCompound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class EntityAttributesUtil {

    public static double getMaxHealth(LivingEntity livingEntity) {
        NBTEntity nbtEntity = new NBTEntity(livingEntity);
        NBTList<NBTListCompound> list = nbtEntity.getCompoundList("Attributes");
        for (NBTListCompound nbtListCompound : list) {
            if (nbtListCompound.getString("Name").equalsIgnoreCase("minecraft:generic.max_health")) {
                return nbtListCompound.getDouble("Base");
            }
            if (nbtListCompound.getString("Name").equalsIgnoreCase("generic.maxHealth")) {
                return nbtListCompound.getDouble("Base");
            }
        }
        return 0;
    }

    public static void setMaxHealth(LivingEntity livingEntity, double maxHealth, boolean restore) {
        NBTEntity nbtEntity = new NBTEntity(livingEntity);
        NBTList<NBTListCompound> list = nbtEntity.getCompoundList("Attributes");
        for (NBTListCompound nbtListCompound : list) {
            if (nbtListCompound.getString("Name").equalsIgnoreCase("minecraft:generic.max_health")) {
                nbtListCompound.setDouble("Base", maxHealth);
                break;
            }
            if (nbtListCompound.getString("Name").equalsIgnoreCase("generic.maxHealth")) {
                nbtListCompound.setDouble("Base", maxHealth);
                break;
            }
        }
        if (restore) {
            livingEntity.setHealth(livingEntity.getMaxHealth());
        }
    }

    public static double getMovementSpeed(LivingEntity livingEntity) {
        NBTEntity nbtEntity = new NBTEntity(livingEntity);
        NBTList<NBTListCompound> list = nbtEntity.getCompoundList("Attributes");
        for (NBTListCompound nbtListCompound : list) {
            if (nbtListCompound.getString("Name").equalsIgnoreCase("minecraft:generic.movement_speed")) {
                return nbtListCompound.getDouble("Base");
            }
            if (nbtListCompound.getString("Name").equalsIgnoreCase("generic.movementSpeed")) {
                return nbtListCompound.getDouble("Base");
            }
        }
        return 0;
    }

    public static void setMovementSpeed(LivingEntity livingEntity, double movementSpeed) {
        NBTEntity nbtEntity = new NBTEntity(livingEntity);
        NBTList<NBTListCompound> list = nbtEntity.getCompoundList("Attributes");
        for (NBTListCompound nbtListCompound : list) {
            if (nbtListCompound.getString("Name").equalsIgnoreCase("minecraft:generic.movement_speed")) {
                nbtListCompound.setDouble("Base", movementSpeed);
            }
            if (nbtListCompound.getString("Name").equalsIgnoreCase("generic.movementSpeed")) {
                nbtListCompound.setDouble("Base", movementSpeed);
                break;
            }
        }
    }

    public static void setEntityHealth(LivingEntity livingEntity, double health) {
        if (livingEntity.isValid()) {
            if (health > livingEntity.getMaxHealth()) {
                health = livingEntity.getMaxHealth();
            }
            if (health < 0) {
                health = 0;
            }
            livingEntity.setHealth(health);
        }
    }
}

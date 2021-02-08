package pers.tany.classlevelentity.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.tany.classlevelentity.util.BasicLibrary;
import pers.tany.classlevelentity.util.CommonlyUtil;
import pers.tany.classlevelentity.util.ConfigUtil;

import java.util.*;

public class ListInterface implements InventoryHolder {
    Inventory inventory;
    Player player;
    int page;
    HashMap<Integer, String> itemLocation = new HashMap<>();

    public ListInterface(Player player, int page) {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("ListTitle").replace("[page]", page + "")));
        ItemStack frame = BasicLibrary.stainedglass.get(1);
        ItemStack last = BasicLibrary.stainedglass.get(11);
        ItemStack next = BasicLibrary.stainedglass.get(14);
        ItemMeta frameMeta = frame.getItemMeta();
        ItemMeta lastMeta = last.getItemMeta();
        ItemMeta nextMeta = next.getItemMeta();

        frameMeta.setDisplayName("§2介绍");
        lastMeta.setDisplayName("§3上一页");
        nextMeta.setDisplayName("§6下一页");

        List<String> lore = new ArrayList<String>(CommonlyUtil.listReplace(ConfigUtil.message.getStringList("ListLore"), "&", "§"));
        frameMeta.setLore(lore);
        lore.clear();

        lore.add("§a翻到下一页");
        nextMeta.setLore(lore);
        lore.clear();

        lore.add("§a翻到上一页");
        lastMeta.setLore(lore);
        lore.clear();

        frame.setItemMeta(frameMeta);
        last.setItemMeta(lastMeta);
        next.setItemMeta(nextMeta);

        HashMap<String, Integer> hashMap = new HashMap<>();
        for (String s : ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false)) {
            hashMap.put(s, (int) (Double.parseDouble(ConfigUtil.data.getString("LevelInfo." + s + ".SpawnProbability").replace("%", "")) * 10000));
        }
        List<Map.Entry<String, Integer>> entryArrayList = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
        if (ConfigUtil.config.getBoolean("ListSequence")) {
            entryArrayList.sort(new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> small, Map.Entry<String, Integer> big) {
                    return small.getValue().compareTo(big.getValue());
                }
            });
        }
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entryArrayList) {
            list.add(entry.getKey());
        }

        if (page > 1) {
            inventory.setItem(45, last);
        } else {
            inventory.setItem(45, frame);
        }
        for (int i = 46; i <= 52; i++) {
            inventory.setItem(i, frame);
        }
        if (list.size() > 45 + (page - 1) * 45) {
            inventory.setItem(53, next);
        } else {
            inventory.setItem(53, frame);
        }

        int index = (page - 1) * 45;
        int location = 0;
        int size = list.size() - 1;
        while (index <= size && index <= 44 + (page - 1) * 45) {
            String level = list.get(index);
            ItemStack itemStack = CommonlyUtil.getItemStack(ConfigUtil.data.getString("LevelInfo." + level + ".Model"));
            ItemMeta itemMeta = itemStack.getItemMeta();
            String spawnProbability = ConfigUtil.data.getString("LevelInfo." + level + ".SpawnProbability");
            int extraHealth = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraHealth");
            String extraHealthPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraHealthPercentage");
            int extraDamage = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraDamage");
            String extraDamagePercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraDamagePercentage");
            int extraArmor = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraArmor");
            String extraSpeedPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraSpeedPercentage");
            lore = ConfigUtil.data.getStringList("LevelInfo." + level + ".Lore");
            int minMoney = ConfigUtil.data.getInt("LevelInfo." + level + ".MinMoney");
            int maxMoney = ConfigUtil.data.getInt("LevelInfo." + level + ".MaxMoney");
            int minExp = ConfigUtil.data.getInt("LevelInfo." + level + ".MinExp");
            int maxExp = ConfigUtil.data.getInt("LevelInfo." + level + ".MaxExp");
            int minLevel = ConfigUtil.data.getInt("LevelInfo." + level + ".MinLevel");
            int maxLevel = ConfigUtil.data.getInt("LevelInfo." + level + ".MaxLevel");

            lore = CommonlyUtil.listReplace(lore, "&", "§");
            lore = CommonlyUtil.listReplace(lore, "[spawn]", spawnProbability);
            lore = CommonlyUtil.listReplace(lore, "[healthPercentage]", extraHealthPercentage);
            lore = CommonlyUtil.listReplace(lore, "[health]", extraHealth + "");
            lore = CommonlyUtil.listReplace(lore, "[damagePercentage]", extraDamagePercentage);
            lore = CommonlyUtil.listReplace(lore, "[damage]", extraDamage + "");
            lore = CommonlyUtil.listReplace(lore, "[armor]", extraArmor + "");
            lore = CommonlyUtil.listReplace(lore, "[speedPercentage]", extraSpeedPercentage);
            lore = CommonlyUtil.listReplace(lore, "[minMoney]", minMoney + "");
            lore = CommonlyUtil.listReplace(lore, "[maxMoney]", maxMoney + "");
            lore = CommonlyUtil.listReplace(lore, "[minExp]", minExp + "");
            lore = CommonlyUtil.listReplace(lore, "[maxExp]", maxExp + "");
            lore = CommonlyUtil.listReplace(lore, "[minLevel]", minLevel + "");
            lore = CommonlyUtil.listReplace(lore, "[maxLevel]", maxLevel + "");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(location, itemStack);
            itemLocation.put(location, level);

            location++;
            index++;
            lore.clear();
        }

        this.inventory = inventory;
        this.player = player;
        this.page = page;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public HashMap<Integer, String> getItemLocation() {
        return itemLocation;
    }
}

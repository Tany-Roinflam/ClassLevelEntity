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

public class ItemInterface implements InventoryHolder {
    Inventory inventory;
    Player player;
    int page;
    int listPage;
    String level;
    HashMap<Integer, String> itemLocation = new HashMap<>();

    public ItemInterface(Player player, int page, String level, int listPage) {
        Inventory inventory = Bukkit.createInventory(this, 54, ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("ItemTitle").replace("[page]", page + "").replace("[class]", level)));
        ItemStack frame = BasicLibrary.stainedglass.get(1);
        ItemStack last = BasicLibrary.stainedglass.get(11);
        ItemStack next = BasicLibrary.stainedglass.get(14);
        ItemMeta frameMeta = frame.getItemMeta();
        ItemMeta lastMeta = last.getItemMeta();
        ItemMeta nextMeta = next.getItemMeta();

        frameMeta.setDisplayName("§2介绍");
        lastMeta.setDisplayName("§3上一页");
        nextMeta.setDisplayName("§6下一页");

        List<String> lore = new ArrayList<String>();

        lore.addAll(CommonlyUtil.listReplace(ConfigUtil.message.getStringList("ItemLore"), "&", "§"));
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

        HashMap<String, Double> hashMap = new HashMap<>();
        HashMap<String, String> amoutMap = new HashMap<>();
        for (String s : ConfigUtil.data.getStringList("LevelInfo." + level + ".ItemStack")) {
            hashMap.put(s.split(":")[1], Double.parseDouble(s.split(":")[0].replace("%", "")));
        }
        for (String s : ConfigUtil.data.getStringList("LevelInfo." + level + ".ItemStack")) {
            amoutMap.put(Double.parseDouble(s.split(":")[0].replace("%", "")) + ":" + s.split(":")[1], s);
        }
        List<Map.Entry<String, Double>> entryArrayList = new ArrayList<Map.Entry<String, Double>>(hashMap.entrySet());
        if (ConfigUtil.config.getBoolean("ItemSequence")) {
            entryArrayList.sort(new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> small, Map.Entry<String, Double> big) {
                    return small.getValue().compareTo(big.getValue());
                }
            });
        }

        if (page > 1) {
            inventory.setItem(45, last);
        } else {
            inventory.setItem(45, frame);
        }
        for (int i = 46; i <= 52; i++) {
            inventory.setItem(i, frame);
        }
        if (entryArrayList.size() > 45 + (page - 1) * 45) {
            inventory.setItem(53, next);
        } else {
            inventory.setItem(53, frame);
        }

        int index = (page - 1) * 45;
        int location = 0;
        int size = entryArrayList.size() - 1;
        while (index <= size && index <= 44 + (page - 1) * 45) {
            Map.Entry<String, Double> entry = entryArrayList.get(index);
            String info = amoutMap.get(entry.getValue() + ":" + entry.getKey());
            String[] i = info.split(":");
            String probability = i[0];
            ItemStack itemStack = CommonlyUtil.getItemStack(i[1]);
            ItemMeta itemMeta = itemStack.getItemMeta();
            int min = Integer.parseInt(i[2]);
            int max = Integer.parseInt(i[3]);

            if (itemMeta.hasLore()) {
                lore = itemMeta.getLore();
                lore.add("");
            }
            lore.addAll(ConfigUtil.message.getStringList("ProbabilityLore"));
            if (player.isOp()) {
                lore.add("");
                lore.add("§e点击移除此物品");
            }

            lore = CommonlyUtil.listReplace(lore, "&", "§");
            lore = CommonlyUtil.listReplace(lore, "[probability]", probability);
            lore = CommonlyUtil.listReplace(lore, "[min]", min + "");
            lore = CommonlyUtil.listReplace(lore, "[max]", max + "");

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(location, itemStack);
            itemLocation.put(location, info);

            location++;
            index++;
            lore.clear();
        }

        this.inventory = inventory;
        this.player = player;
        this.page = page;
        this.level = level;
        this.listPage = listPage;
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

    public String getLevel() {
        return level;
    }

    public int getListPage() {
        return listPage;
    }

    public HashMap<Integer, String> getItemLocation() {
        return itemLocation;
    }
}

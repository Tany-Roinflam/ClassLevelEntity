package pers.tany.classlevelentity.listenevent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pers.tany.classlevelentity.Main;
import pers.tany.classlevelentity.command.Commands;
import pers.tany.classlevelentity.gui.ItemInterface;
import pers.tany.classlevelentity.gui.ListInterface;
import pers.tany.classlevelentity.util.CommonlyUtil;
import pers.tany.classlevelentity.util.ConfigUtil;
import pers.tany.classlevelentity.util.EntityAttributesUtil;
import pers.tany.classlevelentity.util.ItemUtil;

import java.util.*;
import java.util.Map.Entry;

public class Events implements Listener {
    public static Set<Entity> entitySet = new HashSet<>();
    public static HashMap<String, String> levelList = new HashMap<>();
    boolean disable = false;
    boolean close = true;

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent evt) {
        Player player = evt.getPlayer();
        if (Commands.checkList.contains(player.getName()) && evt.getRightClicked() instanceof LivingEntity) {
            player.sendMessage(evt.getRightClicked().getType().toString());
            player.sendMessage(evt.getRightClicked().getUniqueId().toString());
        }
        if (Commands.changeLevel.containsKey(player.getName()) && evt.getRightClicked() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) evt.getRightClicked();
            if (levelList.containsKey(livingEntity.getUniqueId().toString())) {
                player.sendMessage("§c这个生物已经有等级了！");
                return;
            }
            String level = Commands.changeLevel.get(player.getName());
            if (ConfigUtil.data.getInt("LevelInfo." + level + ".Type") == 1) {
                if (!(livingEntity instanceof Monster)) {
                    player.sendMessage("§c这个生物不是怪物！");
                    return;
                }
            } else if (ConfigUtil.data.getInt("LevelInfo." + level + ".Type") == 2) {
                if (!(livingEntity instanceof Animals)) {
                    player.sendMessage("§c这个生物不是动物！");
                    return;
                }
            }
            entitySet.add(livingEntity);
            levelList.put(livingEntity.getUniqueId().toString(), level);
            int extraHealth = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraHealth");
            String extraHealthPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraHealthPercentage");
            String extraSpeedPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraSpeedPercentage");

            double maxHealth = EntityAttributesUtil.getMaxHealth(livingEntity);
            maxHealth *= CommonlyUtil.percentageNumber(extraHealthPercentage);
            maxHealth += extraHealth;
            EntityAttributesUtil.setMaxHealth(livingEntity, maxHealth, true);

            double speed = EntityAttributesUtil.getMovementSpeed(livingEntity);
            speed *= CommonlyUtil.percentageNumber(extraSpeedPercentage);
            EntityAttributesUtil.setMovementSpeed(livingEntity, speed);
            if (livingEntity.getName() != null) {
                livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", livingEntity.getCustomName())));
            } else {
                String typeName = livingEntity.getType().toString().toLowerCase();
                typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", typeName)));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager() instanceof Player)) {
            if (evt.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) evt.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) projectile.getShooter();
                    if (levelList.containsKey(livingEntity.getUniqueId().toString())) {
                        String level = levelList.get(livingEntity.getUniqueId().toString());
                        int extraDamage = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraDamage");
                        String extraDamagePercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraDamagePercentage");

                        double damage = evt.getDamage();
                        damage *= CommonlyUtil.percentageNumber(extraDamagePercentage);
                        damage += extraDamage;
                        evt.setDamage(damage);
                    }
                }
            } else if (evt.getDamager() instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) evt.getDamager();
                if (levelList.containsKey(livingEntity.getUniqueId().toString())) {
                    String level = levelList.get(livingEntity.getUniqueId().toString());
                    int extraDamage = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraDamage");
                    String extraDamagePercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraDamagePercentage");

                    double damage = evt.getDamage();
                    damage *= CommonlyUtil.percentageNumber(extraDamagePercentage);
                    damage += extraDamage;
                    evt.setDamage(damage);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof LivingEntity && !(evt.getEntity() instanceof Player)) {
            LivingEntity livingEntity = (LivingEntity) evt.getEntity();
            if (levelList.containsKey(livingEntity.getUniqueId().toString())) {
                String level = levelList.get(livingEntity.getUniqueId().toString());
                int extraArmor = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraArmor");
                double newDamageMagnification = 1 - ((double) extraArmor / (extraArmor + 100));
                evt.setDamage(evt.getDamage() * newDamageMagnification);
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent evt) {
        LivingEntity livingEntity = evt.getEntity();
        if (ConfigUtil.config.getStringList("BlackEntityList").contains(livingEntity.getType().toString())) {
            return;
        }
        if (ConfigUtil.config.getStringList("BlackWorldList").contains(livingEntity.getWorld().getName())) {
            return;
        }
        if (ConfigUtil.config.getString("FixedLevel." + livingEntity.getType().toString()) != null) {
            String level = ConfigUtil.config.getString("FixedLevel." + livingEntity.getType().toString());
            entitySet.add(livingEntity);
            levelList.put(livingEntity.getUniqueId().toString(), level);
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (livingEntity.isValid()) {
                        int extraHealth = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraHealth");
                        String extraHealthPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraHealthPercentage");
                        String extraSpeedPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraSpeedPercentage");

                        double maxHealth = EntityAttributesUtil.getMaxHealth(livingEntity);
                        maxHealth *= CommonlyUtil.percentageNumber(extraHealthPercentage);
                        maxHealth += extraHealth;
                        EntityAttributesUtil.setMaxHealth(livingEntity, maxHealth, true);

                        double speed = EntityAttributesUtil.getMovementSpeed(livingEntity);
                        speed *= CommonlyUtil.percentageNumber(extraSpeedPercentage);
                        EntityAttributesUtil.setMovementSpeed(livingEntity, speed);

                        if (ConfigUtil.config.getBoolean("CustomName")) {
                            if (livingEntity.getCustomName() != null) {
                                livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", livingEntity.getCustomName())));
                            } else {
                                String typeName = livingEntity.getType().toString().toLowerCase();
                                typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                                livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", typeName)));
                            }
                        }
                    }
                }

            }.runTaskLater(Main.plugin, 20);
        } else {
            HashMap<String, Integer> hashMap = new HashMap<>();
            for (String s : ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false)) {
                hashMap.put(s, (int) (Double.parseDouble(ConfigUtil.data.getString("LevelInfo." + s + ".SpawnProbability").replace("%", "")) * 10000));
            }
            List<Entry<String, Integer>> entryArrayList = new ArrayList<Entry<String, Integer>>(hashMap.entrySet());
            entryArrayList.sort(new Comparator<Entry<String, Integer>>() {
                @Override
                public int compare(Entry<String, Integer> small, Entry<String, Integer> big) {
                    return small.getValue().compareTo(big.getValue());
                }
            });
            for (Entry<String, Integer> entry : entryArrayList) {
                if (CommonlyUtil.randomNumber(0, 999999) < entry.getValue()) {
                    String level = entry.getKey();
                    if (ConfigUtil.data.getInt("LevelInfo." + level + ".Type") == 1) {
                        if (!(livingEntity instanceof Monster)) {
                            continue;
                        }
                    } else if (ConfigUtil.data.getInt("LevelInfo." + level + ".Type") == 2) {
                        if (!(livingEntity instanceof Animals)) {
                            continue;
                        }
                    }
                    entitySet.add(livingEntity);
                    levelList.put(livingEntity.getUniqueId().toString(), level);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (livingEntity.isValid()) {
                                int extraHealth = ConfigUtil.data.getInt("LevelInfo." + level + ".ExtraHealth");
                                String extraHealthPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraHealthPercentage");
                                String extraSpeedPercentage = ConfigUtil.data.getString("LevelInfo." + level + ".ExtraSpeedPercentage");

                                double maxHealth = EntityAttributesUtil.getMaxHealth(livingEntity);
                                maxHealth *= CommonlyUtil.percentageNumber(extraHealthPercentage);
                                maxHealth += extraHealth;
                                EntityAttributesUtil.setMaxHealth(livingEntity, maxHealth, true);

                                double speed = EntityAttributesUtil.getMovementSpeed(livingEntity);
                                speed *= CommonlyUtil.percentageNumber(extraSpeedPercentage);
                                EntityAttributesUtil.setMovementSpeed(livingEntity, speed);

                                if (ConfigUtil.config.getBoolean("CustomName")) {
                                    if (livingEntity.getCustomName() != null) {
                                        livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", livingEntity.getCustomName())));
                                    } else {
                                        String typeName = livingEntity.getType().toString().toLowerCase();
                                        typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                                        livingEntity.setCustomName(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("CustomNameFormat").replace("[class]", level).replace("[name]", typeName)));
                                    }
                                }
                            }
                        }

                    }.runTaskLater(Main.plugin, 20);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent evt) {
        if (!(evt.getEntity() instanceof Player) && evt.getEntity().getKiller() instanceof Player) {
            LivingEntity livingEntity = evt.getEntity();
            Player player = evt.getEntity().getKiller();
            if (levelList.containsKey(livingEntity.getUniqueId().toString())) {
                String level = levelList.get(livingEntity.getUniqueId().toString());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("KillMessage").replace("[class]", level)));
                if (!ConfigUtil.data.getString("LevelInfo." + level + ".Bulletin").equals("空")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.data.getString("LevelInfo." + level + ".Bulletin").replace("[player]", player.getName())));
                }
                int getMoney = CommonlyUtil.randomNumber(ConfigUtil.data.getInt("LevelInfo." + level + ".MinMoney"), ConfigUtil.data.getInt("LevelInfo." + level + ".MaxMoney"));
                int getExp = CommonlyUtil.randomNumber(ConfigUtil.data.getInt("LevelInfo." + level + ".MinExp"), ConfigUtil.data.getInt("LevelInfo." + level + ".MaxExp"));
                int getLevel = CommonlyUtil.randomNumber(ConfigUtil.data.getInt("LevelInfo." + level + ".MinLevel"), ConfigUtil.data.getInt("LevelInfo." + level + ".MaxLevel"));
                if (getMoney > 0) {
                    Main.economy.depositPlayer(player, getMoney);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("KillMoneyMessage").replace("[class]", level).replace("[money]", getMoney + "")));
                }
                if (getExp > 0) {
                    evt.setDroppedExp(evt.getDroppedExp() + getExp);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("KillExpMessage").replace("[class]", level).replace("[exp]", getExp + "")));
                }
                if (getLevel > 0) {
                    player.giveExpLevels(getLevel);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigUtil.message.getString("KillLevelMessage").replace("[class]", level).replace("[level]", getLevel + "")));
                }
                HashMap<String, Double> hashMap = new HashMap<>();
                HashMap<String, String> amoutMap = new HashMap<>();
                for (String s : ConfigUtil.data.getStringList("LevelInfo." + level + ".ItemStack")) {
                    hashMap.put(s.split(":")[1], Double.parseDouble(s.split(":")[0].replace("%", "")));
                }
                for (String s : ConfigUtil.data.getStringList("LevelInfo." + level + ".ItemStack")) {
                    amoutMap.put(Double.parseDouble(s.split(":")[0].replace("%", "")) + ":" + s.split(":")[1], s);
                }
                List<Entry<String, Double>> entryArrayList = new ArrayList<Entry<String, Double>>(hashMap.entrySet());
                entryArrayList.sort(new Comparator<Entry<String, Double>>() {
                    @Override
                    public int compare(Entry<String, Double> small, Entry<String, Double> big) {
                        return small.getValue().compareTo(big.getValue());
                    }
                });
                for (Map.Entry<String, Double> entry : entryArrayList) {
                    String info = amoutMap.get(entry.getValue() + ":" + entry.getKey());
                    String[] i = info.split(":");
                    int probability = (int) (Double.parseDouble(i[0].replace("%", "")) * 10000);
                    ItemStack itemStack = CommonlyUtil.getItemStack(i[1]);
                    int min = Integer.parseInt(i[2]);
                    int max = Integer.parseInt(i[3]);

                    int amount = CommonlyUtil.randomNumber(min, max);
                    if (CommonlyUtil.randomNumber(0, 999999) < probability && amount > 0) {
                        if (ConfigUtil.config.getBoolean("CompatibleMode")) {
                            if (itemStack.getMaxStackSize() > amount && itemStack.getMaxStackSize() > 1) {
                                itemStack.setAmount(amount);
                                livingEntity.getWorld().dropItem(livingEntity.getLocation(), itemStack);
                            } else {
                                int itemAmount = amount % itemStack.getMaxStackSize();
                                int number = amount / itemStack.getMaxStackSize();
                                if (itemAmount > 0) {
                                    itemStack.setAmount(itemAmount);
                                    livingEntity.getWorld().dropItem(livingEntity.getLocation(), itemStack);
                                }
                                ItemStack itemStack_Clone = itemStack.clone();
                                itemStack_Clone.setAmount(itemStack.getMaxStackSize());
                                for (int a = 0; a < number; a++) {
                                    livingEntity.getWorld().dropItem(livingEntity.getLocation(), itemStack_Clone);
                                }
                            }
                        } else {
                            if (itemStack.getMaxStackSize() > amount && itemStack.getMaxStackSize() > 1) {
                                itemStack.setAmount(amount);
                                evt.getDrops().add(itemStack);
                            } else {
                                int itemAmount = amount % itemStack.getMaxStackSize();
                                int number = amount / itemStack.getMaxStackSize();
                                if (itemAmount > 0) {
                                    itemStack.setAmount(itemAmount);
                                    evt.getDrops().add(itemStack);
                                }
                                ItemStack itemStack_Clone = itemStack.clone();
                                itemStack_Clone.setAmount(itemStack.getMaxStackSize());
                                for (int a = 0; a < number; a++) {
                                    evt.getDrops().add(itemStack_Clone);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        if (evt.getInventory() != null && evt.getWhoClicked() instanceof Player) {
            if (!ItemUtil.isEmpty(evt.getCurrentItem())) {
                if (evt.getInventory().getHolder() != null) {
                    if (evt.getInventory().getHolder() instanceof ListInterface) {
                        InventoryHolder inventoryHolder = evt.getInventory().getHolder();
                        evt.setCancelled(true);
                        if (evt.getClickedInventory().getHolder() instanceof ListInterface) {
                            Player player = (Player) evt.getWhoClicked();
                            ListInterface listInterface = (ListInterface) inventoryHolder;
                            int page = listInterface.getPage();
                            if (evt.getCurrentItem().hasItemMeta() && evt.getCurrentItem().getItemMeta().hasDisplayName()) {
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§2介绍")) {
                                    return;
                                }
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§3上一页")) {
                                    Inventory inventory = new ListInterface(player, --page).getInventory();
                                    player.openInventory(inventory);
                                    return;
                                }
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§6下一页")) {
                                    Inventory inventory = new ListInterface(player, ++page).getInventory();
                                    player.openInventory(inventory);
                                    return;
                                }
                                Inventory inventory = new ItemInterface(player, 1, evt.getCurrentItem().getItemMeta().getDisplayName(), page).getInventory();
                                player.openInventory(inventory);
                            }
                        }
                        return;
                    }
                    if (evt.getInventory().getHolder() instanceof ItemInterface) {
                        InventoryHolder inventoryHolder = evt.getInventory().getHolder();
                        evt.setCancelled(true);
                        if (evt.getClickedInventory().getHolder() instanceof ItemInterface) {
                            Player player = (Player) evt.getWhoClicked();
                            ItemInterface itemInterface = (ItemInterface) inventoryHolder;
                            int page = itemInterface.getPage();
                            int listPage = itemInterface.getListPage();
                            String level = itemInterface.getLevel();
                            if (evt.getCurrentItem().hasItemMeta() && evt.getCurrentItem().getItemMeta().hasDisplayName()) {
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§2介绍")) {
                                    return;
                                }
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§3上一页")) {
                                    Inventory inventory = new ItemInterface(player, --page, level, listPage).getInventory();
                                    close = false;
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            close = true;
                                        }

                                    }.runTaskLater(Main.plugin, 1);
                                    player.openInventory(inventory);
                                    return;
                                }
                                if (evt.getCurrentItem().getItemMeta().getDisplayName().equals("§6下一页")) {
                                    Inventory inventory = new ItemInterface(player, ++page, level, listPage).getInventory();
                                    close = false;
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            close = true;
                                        }

                                    }.runTaskLater(Main.plugin, 1);
                                    player.openInventory(inventory);
                                    return;
                                }
                            }
                            if (player.isOp()) {
                                String info = itemInterface.getItemLocation().get(evt.getRawSlot());
                                List<String> list = ConfigUtil.data.getStringList("LevelInfo." + level + ".ItemStack");
                                list.remove(info);
                                ConfigUtil.data.set("LevelInfo." + level + ".ItemStack", list);
                                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ItemInterface) {
                                        if (((ItemInterface) p.getOpenInventory().getTopInventory().getHolder()).getLevel().equals(level)) {
                                            ItemInterface i = (ItemInterface) p.getOpenInventory().getTopInventory().getHolder();
                                            p.openInventory(new ItemInterface(player, page, level, i.getListPage()).getInventory());
                                        }
                                    }
                                }
                                player.sendMessage("§a移除成功！");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent evt) {
        if (evt.getInventory().getHolder() instanceof ItemInterface && close) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    ItemInterface itemInterface = (ItemInterface) evt.getInventory().getHolder();
                    Player player = (Player) evt.getPlayer();
                    Inventory inventory = new ListInterface(player, itemInterface.getListPage()).getInventory();
                    player.openInventory(inventory);
                }

            }.runTaskLater(Main.plugin, 1);
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent evt) {
        Player player = evt.getPlayer();
        if (Commands.mode.containsKey(player.getName())) {
            evt.setCancelled(true);
            int stage = Commands.modeStage.get(player.getName());
            String level = Commands.mode.get(player.getName());
            String message = evt.getMessage();
            int number = 0;
            switch (stage) {
                case 1:
                    try {
                        number = Integer.parseInt(message);
                        if (number < 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraHealth", number);
                    player.sendMessage("§a请输入此等级原血量百分比");
                    break;
                case 2:
                    if (!message.endsWith("%")) {
                        player.sendMessage("§c请以%结尾！");
                        return;
                    }
                    try {
                        number = Integer.parseInt(message.replace("%", ""));
                        if (number <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraHealthPercentage", message);
                    player.sendMessage("§a请输入此等级增加的固定伤害");
                    break;
                case 3:
                    try {
                        number = Integer.parseInt(message);
                        if (number < 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraDamage", number);
                    player.sendMessage("§a请输入此等级原伤害百分比");
                    break;
                case 4:
                    if (!message.endsWith("%")) {
                        player.sendMessage("§c请以%结尾！");
                        return;
                    }
                    try {
                        number = Integer.parseInt(message.replace("%", ""));
                        if (number <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraDamagePercentage", message);
                    player.sendMessage("§a请输入此等级的护甲值");
                    break;
                case 5:
                    try {
                        number = Integer.parseInt(message);
                        if (number < 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraArmor", number);
                    player.sendMessage("§a请输入此等级原速度百分比");
                    break;
                default:
                    if (!message.endsWith("%")) {
                        player.sendMessage("§c请以%结尾！");
                        return;
                    }
                    try {
                        number = Integer.parseInt(message.replace("%", ""));
                        if (number <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        player.sendMessage("§c请输入大于0的数字！");
                        return;
                    }
                    ConfigUtil.data.set("LevelInfo." + level + ".ExtraSpeedPercentage", message);
                    player.sendMessage("§a编辑完成！");
                    Commands.mode.remove(player.getName());
                    Commands.modeStage.remove(player.getName());
                    CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", true);
                    return;
            }
            Commands.modeStage.put(player.getName(), stage + 1);
        }
    }
}

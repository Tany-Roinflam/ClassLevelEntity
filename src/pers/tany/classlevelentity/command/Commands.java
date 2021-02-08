package pers.tany.classlevelentity.command;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.tany.classlevelentity.Main;
import pers.tany.classlevelentity.gui.ItemInterface;
import pers.tany.classlevelentity.gui.ListInterface;
import pers.tany.classlevelentity.util.CommonlyUtil;
import pers.tany.classlevelentity.util.ConfigUtil;

import java.io.File;
import java.util.*;

public class Commands implements CommandExecutor {
    public static Set<String> checkList = new HashSet<>();
    public static HashMap<String, String> changeLevel = new HashMap<>();
    public static HashMap<String, String> mode = new HashMap<>();
    public static HashMap<String, Integer> modeStage = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            args[1] = args[1].replace("&", "§");
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                File file = new File(Main.plugin.getDataFolder(), "config.yml");
                ConfigUtil.config = YamlConfiguration.loadConfiguration(file);
                file = new File(Main.plugin.getDataFolder(), "data.yml");
                ConfigUtil.data = YamlConfiguration.loadConfiguration(file);
                file = new File(Main.plugin.getDataFolder(), "list.yml");
                ConfigUtil.list = YamlConfiguration.loadConfiguration(file);
                file = new File(Main.plugin.getDataFolder(), "message.yml");
                ConfigUtil.message = YamlConfiguration.loadConfiguration(file);
                sender.sendMessage("§a重载成功");
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                sender.sendMessage("§f/cle setspawn 名称 生成概率  §7设置生成概率");
                sender.sendMessage("§f/cle settype 名称 类别  §7设置生成类型（0-2）");
                sender.sendMessage("§f/cle sethealth 名称 固定血量 血量百分比  §7设置血量");
                sender.sendMessage("§f/cle setdamage 名称 固定伤害 伤害百分比 §7设置伤害");
                sender.sendMessage("§f/cle setarmor 名称 固定护甲 §7设置护甲");
                sender.sendMessage("§f/cle setspeed 名称 速度百分比  §7设置速度");
                sender.sendMessage("");
                sender.sendMessage("§f/cle setmoney 名称 最少游戏币 最多游戏币  §7设置掉落游戏币");
                sender.sendMessage("§f/cle setexp 名称 最少经验 最多经验  §7设置掉落经验");
                sender.sendMessage("§f/cle setlevel 名称 最少等级 最多等级  §7设置掉落等级");
                sender.sendMessage("§f/cle setbc 名称 内容  §7添加被击杀公告（[player]替换玩家名）");
                sender.sendMessage("");
                if (!CommonlyUtil.consoleUse(sender)) {
                    sender.sendMessage("§f/cle additem 名称 掉落概率 最小数量 最大数量  §7设置此物品掉落概率及数量");
                }
                sender.sendMessage("§f/cle addlore 名称 内容  §7给此等级添加一行lore（空格替换为空格）");
                sender.sendMessage("§f/cle dellore 名称  §7删除此等级最下面一行lore");
                return true;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("reset")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Type", ConfigUtil.config.getInt("DefaultType"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".SpawnProbability", ConfigUtil.config.getString("SpawnProbability"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealth", ConfigUtil.config.getInt("ExtraHealth"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealthPercentage", ConfigUtil.config.getString("ExtraHealthPercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamage", ConfigUtil.config.getInt("ExtraDamage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamagePercentage", ConfigUtil.config.getString("ExtraDamagePercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraArmor", ConfigUtil.config.getInt("ExtraArmor"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraSpeedPercentage", ConfigUtil.config.getString("ExtraSpeedPercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Lore", ConfigUtil.config.getStringList("Lore"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinMoney", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxMoney", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinExp", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxExp", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinLevel", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxLevel", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ItemStack", new ArrayList<>());
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Bulletin", "空");
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a初始化设置成功");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ListInterface) {
                        ListInterface listInterface = (ListInterface) p.getOpenInventory().getTopInventory().getHolder();
                        Inventory inventory = new ListInterface(p, listInterface.getPage()).getInventory();
                        p.openInventory(inventory);
                    }
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ItemInterface) {
                        ItemInterface itemInterface = (ItemInterface) p.getOpenInventory().getTopInventory().getHolder();
                        Inventory inventory = new ItemInterface(p, itemInterface.getPage(), args[1], itemInterface.getListPage()).getInventory();
                        p.openInventory(inventory);
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1], null);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a删除成功！");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ListInterface) {
                        ListInterface listInterface = (ListInterface) p.getOpenInventory().getTopInventory().getHolder();
                        Inventory inventory = new ListInterface(p, listInterface.getPage()).getInventory();
                        p.openInventory(inventory);
                    }
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ItemInterface) {
                        if (((ItemInterface) p.getOpenInventory().getTopInventory().getHolder()).getLevel().equals(args[1])) {
                            p.closeInventory();
                        }
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("dellore")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                List<String> lore = ConfigUtil.data.getStringList("LevelInfo." + args[1] + ".Lore");
                if (lore.size() <= 0) {
                    sender.sendMessage("§c没有任何lore！");
                    return true;
                }
                lore.remove(lore.size() - 1);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Lore", lore);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a删除成功！");
                return true;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setspawn")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (!args[2].endsWith("%")) {
                    sender.sendMessage("§c请以%结尾！");
                    return true;
                }
                double number = 0;
                try {
                    number = Double.parseDouble(args[2].replace("%", ""));
                    if (number <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入大于0的数字！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".SpawnProbability", args[2]);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("settype")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                int type = 0;
                try {
                    type = Integer.parseInt(args[2]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                if (type > 2 || type < 0) {
                    sender.sendMessage("§c数字范围为0-2");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Type", type);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setarmor")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                int number = 0;
                try {
                    number = Integer.parseInt(args[2]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraArmor", number);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setspeed")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (!args[2].endsWith("%")) {
                    sender.sendMessage("§c请以%结尾！");
                    return true;
                }
                double number = 0;
                try {
                    number = Double.parseDouble(args[2].replace("%", ""));
                    if (number <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入大于0的数字！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraSpeedPercentage", args[2]);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("addlore")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                List<String> lore = ConfigUtil.data.getStringList("LevelInfo." + args[1] + ".Lore");
                lore.add(ChatColor.translateAlternateColorCodes('&', args[2].replace("空格", " ")));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Lore", lore);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a添加成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setbc")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Bulletin", args[2].replace("&", "§").replace("空格", " "));
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("sethealth")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (!args[3].endsWith("%")) {
                    sender.sendMessage("§c请以%结尾！");
                    return true;
                }
                int number = 0;
                try {
                    number = Integer.parseInt(args[3].replace("%", ""));
                    if (number <= 0) {
                        throw new NumberFormatException();
                    }
                    number = Integer.parseInt(args[2]);
                    if (number < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入大于0的数字！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealth", number);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealthPercentage", args[3]);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setdamage")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (!args[3].endsWith("%")) {
                    sender.sendMessage("§c请以%结尾！");
                    return true;
                }
                int number = 0;
                try {
                    number = Integer.parseInt(args[3].replace("%", ""));
                    if (number <= 0) {
                        throw new NumberFormatException();
                    }
                    number = Integer.parseInt(args[2]);
                    if (number < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入大于0的数字！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamage", number);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamagePercentage", args[3]);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setmoney")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                int min = 0;
                int max = 0;
                try {
                    min = Integer.parseInt(args[2]);
                    max = Integer.parseInt(args[3]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                if (min < 0) {
                    sender.sendMessage("§c最小值不能小于0！");
                    return true;
                }
                if (min > max) {
                    sender.sendMessage("§c最大值不能小于最小值！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinMoney", min);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxMoney", max);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setexp")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                int min = 0;
                int max = 0;
                try {
                    min = Integer.parseInt(args[2]);
                    max = Integer.parseInt(args[3]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                if (min < 0) {
                    sender.sendMessage("§c最小值不能小于0！");
                    return true;
                }
                if (min > max) {
                    sender.sendMessage("§c最大值不能小于最小值！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinExp", min);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxExp", max);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
            if (args[0].equalsIgnoreCase("setlevel")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    sender.sendMessage("§c没有这个等级！");
                    return true;
                }
                int min = 0;
                int max = 0;
                try {
                    min = Integer.parseInt(args[2]);
                    max = Integer.parseInt(args[3]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                if (min < 0) {
                    sender.sendMessage("§c最小值不能小于0！");
                    return true;
                }
                if (min > max) {
                    sender.sendMessage("§c最大值不能小于最小值！");
                    return true;
                }
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinLevel", min);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxLevel", max);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a设置成功！");
                return true;
            }
        }
        if (CommonlyUtil.consoleUse(sender)) {
            sender.sendMessage("§f/cle set  §7查看等级设置属性指令");
            sender.sendMessage("§f/cle reset 名称  §7初始化设置");
            sender.sendMessage("§f/cle remove 名称  §7删除这个等级");
            sender.sendMessage("§f/cle reload  §7重载插件");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!player.hasPermission("cle.list")) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                Inventory inventory = new ListInterface(player, 1).getInventory();
                player.openInventory(inventory);
                return true;
            }
            if (args[0].equalsIgnoreCase("show")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (checkList.remove(player.getName())) {
                    player.sendMessage("§c成功关闭模式");
                } else {
                    checkList.add(player.getName());
                    player.sendMessage("§a成功开启模式");
                }
                return true;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("change")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    player.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (changeLevel.containsKey(player.getName())) {
                    changeLevel.remove(player.getName());
                    player.sendMessage("§c成功关闭模式");
                } else {
                    changeLevel.put(player.getName(), args[1]);
                    player.sendMessage("§a成功开启模式");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    player.sendMessage("§c已经有这个等级了！");
                    return true;
                }
                if (CommonlyUtil.emptyItem(player)) {
                    player.sendMessage("§c不能为空手！");
                    return true;
                }
                ItemStack itemStack = player.getItemInHand().clone();
                itemStack.setAmount(1);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(args[1]);
                itemMeta.setLore(new ArrayList<>());
                itemStack.setItemMeta(itemMeta);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Model", CommonlyUtil.getItemData(itemStack));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Type", ConfigUtil.config.getInt("DefaultType"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".SpawnProbability", ConfigUtil.config.getString("SpawnProbability"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealth", ConfigUtil.config.getInt("ExtraHealth"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraHealthPercentage", ConfigUtil.config.getString("ExtraHealthPercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamage", ConfigUtil.config.getInt("ExtraDamage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraDamagePercentage", ConfigUtil.config.getString("ExtraDamagePercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraArmor", ConfigUtil.config.getInt("ExtraArmor"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ExtraSpeedPercentage", ConfigUtil.config.getString("ExtraSpeedPercentage"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Lore", ConfigUtil.config.getStringList("Lore"));
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinMoney", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxMoney", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinExp", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxExp", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MinLevel", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".MaxLevel", 0);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ItemStack", new ArrayList<>());
                ConfigUtil.data.set("LevelInfo." + args[1] + ".Bulletin", "空");
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                player.sendMessage("§a创建成功！");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ListInterface) {
                        ListInterface listInterface = (ListInterface) p.getOpenInventory().getTopInventory().getHolder();
                        Inventory inventory = new ListInterface(p, listInterface.getPage()).getInventory();
                        p.openInventory(inventory);
                    }
                }
                if (ConfigUtil.config.getBoolean("EditMode")) {
                    if (!mode.containsKey(player.getName())) {
                        mode.put(player.getName(), args[1]);
                        modeStage.put(player.getName(), 1);
                        player.sendMessage("§a请输入此等级增加的固定血量");
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("mode")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    player.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (mode.containsKey(player.getName())) {
                    player.sendMessage("§c请走完当前流程！");
                } else {
                    mode.put(player.getName(), args[1]);
                    modeStage.put(player.getName(), 1);
                    player.sendMessage("§a请输入此等级增加的固定血量");
                }
                return true;
            }
        }
        if (args.length == 5) {
            if (args[0].equalsIgnoreCase("additem")) {
                if (CommonlyUtil.opUseCommand(sender)) {
                    sender.sendMessage("§c你没有权限执行此命令");
                    return true;
                }
                if (!ConfigUtil.data.getConfigurationSection("LevelInfo").getKeys(false).contains(args[1])) {
                    player.sendMessage("§c没有这个等级！");
                    return true;
                }
                if (!args[2].endsWith("%")) {
                    sender.sendMessage("§c请以%结尾！");
                    return true;
                }
                int min = 0;
                int max = 0;
                try {
                    min = Integer.parseInt(args[3]);
                    max = Integer.parseInt(args[4]);
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入数字！");
                    return true;
                }
                if (min < 0) {
                    sender.sendMessage("§c最小值不能小于0！");
                    return true;
                }
                if (min > max) {
                    sender.sendMessage("§c最大值不能小于最小值！");
                    return true;
                }
                if (CommonlyUtil.emptyItem(player)) {
                    player.sendMessage("§c不能为空手！");
                    return true;
                }
                double number = 0;
                try {
                    number = Double.parseDouble(args[2].replace("%", ""));
                    if (number <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException numberFormatException) {
                    sender.sendMessage("§c请输入大于0的数字！");
                    return true;
                }
                ItemStack itemStack = player.getItemInHand().clone();
                itemStack.setAmount(1);
                List<String> list = ConfigUtil.data.getStringList("LevelInfo." + args[1] + ".ItemStack");
                if (CommonlyUtil.listContains(list, CommonlyUtil.getItemData(itemStack), true) != null) {
                    player.sendMessage("§c这个物品已经添加过了！");
                    return true;
                }
                list.add(args[2] + ":" + CommonlyUtil.getItemData(itemStack) + ":" + min + ":" + max);
                ConfigUtil.data.set("LevelInfo." + args[1] + ".ItemStack", list);
                CommonlyUtil.saveConfig(Main.plugin, ConfigUtil.data, "data", false);
                sender.sendMessage("§a添加成功！");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getOpenInventory().getTopInventory().getHolder() instanceof ItemInterface) {
                        ItemInterface itemInterface = (ItemInterface) p.getOpenInventory().getTopInventory().getHolder();
                        Inventory inventory = new ItemInterface(p, itemInterface.getPage(), args[1], itemInterface.getListPage()).getInventory();
                        p.openInventory(inventory);
                    }
                }
                return true;
            }
        }
        if (player.isOp()) {
            player.sendMessage("§f---------------------------------------------------------------");
            player.sendMessage("§f/cle show  §7开关右键显示生物名称及UUID模式");
            player.sendMessage("§f/cle list  §7查看所有等级的属性");
            player.sendMessage("§f/cle set  §7查看等级设置属性指令");
            player.sendMessage("§f/cle change 名称  §7下次交互时把此生物改为此等级");
            player.sendMessage("§f/cle create 名称  §7新建一个等级（材质为手上物品，可带颜色代码）");
            player.sendMessage("§f/cle mode 名称  §7进入设置此等级属性模式");
            player.sendMessage("§f/cle reset 名称  §7初始化设置");
            player.sendMessage("§f/cle remove 名称  §7删除这个等级");
            player.sendMessage("§f/cle reload  §7重载插件");
            player.sendMessage("§f---------------------------------------------------------------");
        } else {
            player.sendMessage("§f/cle list  §7查看所有等级的属性");
        }
        return true;
    }
}

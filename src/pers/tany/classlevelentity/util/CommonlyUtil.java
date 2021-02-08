package pers.tany.classlevelentity.util;

import com.comphenix.protocol.utility.StreamSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


public class CommonlyUtil {
    //		命令判断是否为OP，不是则true
    public static boolean opUseCommand(CommandSender player) {
        return !player.isOp();
    }

    //		命令判断是否为控制台，是则true
    public static boolean consoleUse(CommandSender sender) {
        return !(sender instanceof Player);
    }

    //		序列化ItemStack
    public static String getItemData(ItemStack itemStack) {
        try {
            return new StreamSerializer().serializeItemStack(itemStack);
        } catch (Exception e) {
            return null;
        }
    }

    //		反序列化ItemStack
    public static ItemStack getItemStack(String data) {
        try {
            return new StreamSerializer().deserializeItemStack(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //		生成长度固定的随机字符串
    public static String createRandomString(int length) {
        String number = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            String str = random.nextInt(2) % 2 == 0 ? "num" : "char";
            if ("char".equalsIgnoreCase(str)) {
                int nextInt = random.nextInt(2) % 2 == 0 ? 65 : 97;
                number += (char) (nextInt + random.nextInt(26));
            } else {
                number += String.valueOf(random.nextInt(10));
            }
        }
        return number;
    }

    //		获取玩家背包空格数
    public static int backpackEmptyNumber(Player player) {
        int number = 0;
        int empty = 0;
        while (number <= 35) {
            if (player.getInventory().getItem(number) == null) {
                empty++;
            }
            number++;
        }
        return empty;
    }

    //		增加/扣除玩家的经验（支持负数降级）
    public static void giveExp(Player player, int xp) {
        int level = player.getLevel();
        int toNextLevel = getUpgradeExp(level);
        int floatExp = (int) (player.getExp() * toNextLevel) + xp;
        while (floatExp >= toNextLevel) {
            floatExp -= toNextLevel;
            toNextLevel = getUpgradeExp(++level);
        }
        while (floatExp < 0) {
            floatExp += (toNextLevel = getUpgradeExp(--level));
            if (level < 0) {
                level = floatExp = 0;
            }
        }
        player.setLevel(level);
        player.setExp((float) floatExp / toNextLevel);
        player.setTotalExperience(getTotalExp(level) + floatExp);
    }

    public static int getUpgradeExp(int level) {
        return level < 16 ? level * 2 + 7 : level < 30 ? level * 5 - 38 : level * 9 - 158;
    }

    public static int getTotalExp(int level) {
        return (int) (level < 17 ? level * (level + 6) : level < 31 ? level * (level * 2.5 - 40.5) + 360 : level * (level * 4.5 - 162.5) + 2220);
    }

    //		给予玩家物品，如果背包满则在原地生成掉落物
    public static void giveItem(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        for (ItemStack i : player.getInventory().addItem(item).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    //		判断玩家是否为空手，空手则true
    public static Boolean emptyItem(Player player) {
        return player.getInventory().getItemInHand() == null || player.getInventory().getItemInHand().getType() == Material.AIR;
    }


    //		返回这个物品的NBT
    //		需反射
    public static String getItemNBT(ItemStack item) {
        Class<?> nbttagcompound;
        Class<?> itemstack;
        Method asNMSCopy;
        Method save;
        Class<?> saveitem;
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            saveitem = Class.forName("net.minecraft.server." + version + ".ItemStack");
            itemstack = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
            nbttagcompound = Class.forName("net.minecraft.server." + version + ".NBTTagCompound");
            asNMSCopy = itemstack.getMethod("asNMSCopy", ItemStack.class);
            save = saveitem.getMethod("save", nbttagcompound);

            Object tag = nbttagcompound.newInstance();
            Object nmsitem = asNMSCopy.invoke(null, item);
            Object saves = save.invoke(nmsitem, tag);

            return saves.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    //		遍历数组，替换数组字符串
    public static List<String> listReplace(List<String> list, String wasReplaced, String replace) {
        ArrayList<String> news = new ArrayList<>();
        for (String s : list) {
            news.add(s.replace(wasReplaced, replace));
        }
        return news;
    }

    //	  	判断交互手
    public static boolean getInteractHand(PlayerInteractEvent evt) {
        try {
            return evt.getHand() == null || !evt.getHand().equals(EquipmentSlot.HAND);
        } catch (Exception exception) {
            return false;
        }
    }

    //	  	判断交互实体手
    public static boolean getInteractHand(PlayerInteractEntityEvent evt) {
        try {
            return evt.getHand() == null || !evt.getHand().equals(EquipmentSlot.HAND);
        } catch (Exception exception) {
            return false;
        }
    }

    //	  	判断放置手
    public static boolean getInteractHand(BlockPlaceEvent evt) {
        try {
            return evt.getHand() == null || !evt.getHand().equals(EquipmentSlot.HAND);
        } catch (Exception exception) {
            return false;
        }
    }

    //		把几率%转化为真正的百分比
    public static double percentageNumber(String percentage) {
        try {
            return Double.parseDouble(percentage.replace("%", "")) / 100;
        } catch (Exception b) {
            return 9.99;
        }
    }

    //		把几率%转化为真正的百分比,并且进行乘法计算
    public static double percentage(double number, String percentage) {
        try {
            number = number * (Double.parseDouble(percentage.replace("%", "")) / 100);
        } catch (Exception a) {
            return 9.99;
        }
        return number;
    }

    //		把玩家传送到这个区块位置
    public static void telePortChunk(Player player, World world, Chunk chunk) {
        Location location = player.getLocation();
        location.setWorld(world);
        double x = chunk.getX() * 16;
        double z = chunk.getZ() * 16;
        location.setX(x);
        location.setZ(z);
        location.setY(world.getHighestBlockAt(location).getY());
        player.teleport(location);
    }

    //		返回这个世界的玩家数
    public static int playerNumber(World world) {
        int i = 0;
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) {
                i++;
            }
        }
        return i;
    }

    //		返回服务器总MS
    public static double getMS() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class<?> minecraftserver = Class.forName("net.minecraft.server." + version + ".MinecraftServer");
            Method getserver = minecraftserver.getMethod("getServer");
            Class<?> c = getserver.invoke(minecraftserver).getClass();

            Field h = null;
            for (Field field : c.getFields()) {
                if (field.getType().getName().equalsIgnoreCase("[J")) {
                    h = field;
                    break;
                }
            }

            long average = 0;
            for (long b : (long[]) h.get(getserver.invoke(minecraftserver))) {
                average += b;
            }
            average /= ((long[]) h.get(getserver.invoke(minecraftserver))).length;

            return average * 1e-6;
        } catch (Exception a) {
            return 0;
        }
    }

    //		返回服务器总TPS
    public static double getTPS() {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            Class<?> minecraftserver = Class.forName("net.minecraft.server." + version + ".MinecraftServer");
            Method getserver = minecraftserver.getMethod("getServer");

            Field h = null;
            for (Field field : minecraftserver.getFields()) {
                if (field.getType().getName().equalsIgnoreCase("[J")) {
                    h = field;
                    break;
                }
            }

            long average = 0;
            for (long b : (long[]) h.get(getserver.invoke(minecraftserver))) {
                average += b;
            }
            average /= ((long[]) h.get(getserver.invoke(minecraftserver))).length;


            return Math.min(1000 / (average * 1e-6), 20.0);
        } catch (Exception a) {
            return 0;
        }
    }

    //		随机一个范围的数字
    public static int randomNumber(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    //      计算百分比几率
    public static boolean percentageChance(double number) {
        return randomNumber(1, 100) <= number;
    }

    public static boolean percentageChance(String number) {
        try {
            return randomNumber(1, 100) <= Double.parseDouble(number.replace("%", ""));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    //    获取这个月有多少天
    public static int getMonthDay() {
        String timegroup = new SimpleDateFormat("yyyy:MM").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        int nowMonth = Integer.parseInt(timegroup.split(":")[1]);
        if (nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
            return 31;
        } else if (nowMonth == 2) {
            if (nowYear % 4 == 0 && nowYear % 100 != 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }

    public static int getMonthDay(int nowMonth) {
        String timegroup = new SimpleDateFormat("yyyy").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        if (nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
            return 31;
        } else if (nowMonth == 2) {
            if (nowYear % 4 == 0 && nowYear % 100 != 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }

    public static int getMonthDay(int nowMonth, int nowYear) {
        if (nowMonth == 1 || nowMonth == 3 || nowMonth == 5 || nowMonth == 7 || nowMonth == 8 || nowMonth == 10 || nowMonth == 12) {
            return 31;
        } else if (nowMonth == 2) {
            if (nowYear % 4 == 0 && nowYear % 100 != 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 30;
        }
    }

    //    获取系统时间+输入的时间
    public static String getTime() {
        String timegroup = new SimpleDateFormat("yyyy:MM:dd:HH:mm").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        int nowMonth = Integer.parseInt(timegroup.split(":")[1]);
        int nowDay = Integer.parseInt(timegroup.split(":")[2]);
        int nowHour = Integer.parseInt(timegroup.split(":")[3]);
        int nowMinute = Integer.parseInt(timegroup.split(":")[4]);
        return nowYear + ":" + nowMonth + ":" + nowDay + ":" + nowHour + ":" + nowMinute;
    }

    public static String getTime(int m) {
        String timegroup = new SimpleDateFormat("yyyy:MM:dd:HH:mm").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        int nowMonth = Integer.parseInt(timegroup.split(":")[1]);
        int nowDay = Integer.parseInt(timegroup.split(":")[2]);
        int nowHour = Integer.parseInt(timegroup.split(":")[3]);
        int nowMinute = Integer.parseInt(timegroup.split(":")[4]);

        int monthday = getMonthDay();

        int minute = m + nowMinute;
        int hour = nowHour;
        int day = nowDay;
        int month = nowMonth;
        int year = nowYear;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        if (day > monthday) {
            int t_d = day;
            int t_m = month;
            int t_y = year;
            boolean first = true;
            while (true) {
                if (t_d - monthday <= 0) {
                    if (t_d - monthday != 0) {
                        if (first) {
                            month++;
                        }
                    }
                    day = t_d;
                    break;
                } else {
                    first = false;
                    month++;
                    t_d -= monthday;
                    if (++t_m > 12) {
                        t_m = 1;
                        t_y += 1;
                    }
                    monthday = getMonthDay(t_m, t_y);
                }
            }
        }
        if (month > 12) {
            year += month / 12;
            month %= 12;
        }
        return year + ":" + month + ":" + day + ":" + hour + ":" + minute;
    }

    public static String getTime(int m, int h) {
        String timegroup = new SimpleDateFormat("yyyy:MM:dd:HH:mm").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        int nowMonth = Integer.parseInt(timegroup.split(":")[1]);
        int nowDay = Integer.parseInt(timegroup.split(":")[2]);
        int nowHour = Integer.parseInt(timegroup.split(":")[3]);
        int nowMinute = Integer.parseInt(timegroup.split(":")[4]);

        int monthday = getMonthDay();

        int minute = m + nowMinute;
        int hour = h + nowHour;
        int day = nowDay;
        int month = nowMonth;
        int year = nowYear;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        if (day > monthday) {
            int t_d = day;
            int t_m = month;
            int t_y = year;
            boolean first = true;
            while (true) {
                if (t_d - monthday <= 0) {
                    if (t_d - monthday != 0) {
                        if (first) {
                            month++;
                        }
                    }
                    day = t_d;
                    break;
                } else {
                    first = false;
                    month++;
                    t_d -= monthday;
                    if (++t_m > 12) {
                        t_m = 1;
                        t_y += 1;
                    }
                    monthday = getMonthDay(t_m, t_y);
                }
            }
        }
        if (month > 12) {
            year += month / 12;
            month %= 12;
        }
        return year + ":" + month + ":" + day + ":" + hour + ":" + minute;
    }

    public static String getTime(int m, int h, int d) {
        String timegroup = new SimpleDateFormat("yyyy:MM:dd:HH:mm").format(new Date());
        int nowYear = Integer.parseInt(timegroup.split(":")[0]);
        int nowMonth = Integer.parseInt(timegroup.split(":")[1]);
        int nowDay = Integer.parseInt(timegroup.split(":")[2]);
        int nowHour = Integer.parseInt(timegroup.split(":")[3]);
        int nowMinute = Integer.parseInt(timegroup.split(":")[4]);

        int monthday = getMonthDay();

        int minute = m + nowMinute;
        int hour = h + nowHour;
        int day = d + nowDay;
        int month = nowMonth;
        int year = nowYear;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        if (day > monthday) {
            int t_d = day;
            int t_m = month;
            int t_y = year;
            boolean first = true;
            while (true) {
                if (t_d - monthday <= 0) {
                    if (t_d - monthday != 0) {
                        if (first) {
                            month++;
                        }
                    }
                    day = t_d;
                    break;
                } else {
                    first = false;
                    month++;
                    t_d -= monthday;
                    if (++t_m > 12) {
                        t_m = 1;
                        t_y += 1;
                    }
                    monthday = getMonthDay(t_m, t_y);
                }
            }
        }
        if (month > 12) {
            year += month / 12;
            month %= 12;
        }
        return year + ":" + month + ":" + day + ":" + hour + ":" + minute;
    }

    public static String getDay(int m) {
        int minute = m;
        int hour = 0;
        int day = 0;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        return day + ":" + hour + ":" + minute;
    }

    public static String getDay(int m, int h) {
        int minute = m;
        int hour = h;
        int day = 0;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        return day + ":" + hour + ":" + minute;
    }

    public static String getDay(int m, int h, int d) {
        int minute = m;
        int hour = h;
        int day = d;

        if (minute >= 60) {
            hour += minute / 60;
            minute %= 60;
        }
        if (hour >= 24) {
            day += hour / 24;
            hour %= 24;
        }
        return day + ":" + hour + ":" + minute;
    }

    //    向玩家发送actionbar
    public static void sendActionBar(Plugin plugin, Player player, String m, int time) {
        if (time < 3) {
            time = 3;
        }
        int finalTime = time;
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                try {
                    String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
                    Field playerConnection = Class.forName("net.minecraft.server." + version + ".EntityPlayer").getField("playerConnection");
                    Class<?> packetPlayOutChat = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
                    Method send = Class.forName("net.minecraft.server." + version + ".PlayerConnection").getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet"));
                    Method getHandle = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").getMethod("getHandle");
                    Class<?> iChatBaseComponent = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
                    Class<?> chatComponentText = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                    Class<?> chatMessageType = Class.forName("net.minecraft.server." + version + ".ChatMessageType");
                    Enum e = null;
                    for (Enum inside_enum : (Enum[]) chatMessageType.getEnumConstants()) {
                        if (inside_enum.name().equalsIgnoreCase("GAME_INFO")) {
                            e = inside_enum;
                            break;
                        }
                    }
                    Object message = chatComponentText.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', m));
                    Object packet;
                    try {
                        packet = packetPlayOutChat.getConstructor(iChatBaseComponent, chatMessageType).newInstance(message, e);
                    } catch (Exception a) {
                        packet = packetPlayOutChat.getConstructor(iChatBaseComponent, chatMessageType, player.getUniqueId().getClass()).newInstance(message, e, player.getUniqueId());
                    }
                    send.invoke(playerConnection.get(getHandle.invoke(player)), packet);

//                  CraftPlayer cp = (CraftPlayer) sender;
//                  ChatComponentText c = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&f" + args[2]));
//                  PacketPlayOutChat pack = new PacketPlayOutChat(c, ChatMessageType.GAME_INFO);
//                  PlayerConnection pc = cp.getHandle().playerConnection;
//                  pc.sendPacket(pack);
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§c版本/服务端不兼容！");
                }
                i++;
                if (i >= finalTime - 2) {
                    cancel();
                }
            }

        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

    //    保存配置文件数据
    public static void saveConfig(Plugin plugin, FileConfiguration config, String name, boolean task) {
        if (task) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    try {
                        File file = new File(plugin.getDataFolder(), name + ".yml");
                        config.save(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }.runTask(plugin);
        } else {
            try {
                File file = new File(plugin.getDataFolder(), name + ".yml");
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //    查看list是否以另一个list开头
    public static boolean listStartsWith(List<String> list, List<String> l) {
        return String.join("%分隔符%", list).startsWith(String.join("%分隔符%", l));
    }

    public static String listStartsWith(List<String> list, String string, Boolean order) {
        if (order) {
            for (String l : list) {
                if (l.startsWith(string)) {
                    return l;
                }
            }
        } else {
            for (String l : list) {
                if (string.startsWith(l)) {
                    return l;
                }
            }
        }
        return null;
    }

    //    查看list是否包含另一个list
    public static boolean listContains(List<String> list, List<String> l) {
        return String.join("%分隔符%", list).contains(String.join("%分隔符%", l));
    }

    public static String listContains(List<String> list, String string, Boolean order) {
        if (order) {
            for (String l : list) {
                if (l.contains(string)) {
                    return l;
                }
            }
        } else {
            for (String l : list) {
                if (string.contains(l)) {
                    return l;
                }
            }
        }
        return null;
    }

    //    移除list中第一个list中的内容
    public static List<String> listRemoveFirstList(List<String> list, List<String> l) {
        return new ArrayList<>(Arrays.asList(replaceFirst(String.join("%分隔符%", list), String.join("%分隔符%", l), "").split("%分隔符%")));
    }

    //    查看字符串里这个字符串有几个
    public static int stringNumber(String string, String s) {
        string = string.replace("[", "%前括号%").replace("]", "%后括号%").replace("+", "%加号%").replace("-", "%减号%").replace("$", "%钱号%");
        s = s.replace("[", "%前括号%").replace("]", "%后括号%").replace("+", "%加号%").replace("-", "%减号%").replace("$", "%钱号%");
        int number = 0;
        while (string.contains(s)) {
            string = string.replaceFirst(s, "");
            number++;
        }
        return number;
    }

    //    查看列表里这个字符串有几个
    public static int stringNumber(List<String> list, String string) {
        int number = 0;
        while (list.contains(string)) {
            list.remove(string);
            number++;
        }
        return number;
    }

    //    查看list这个字符串开头第一个的位置
    public static int listStartsWithIndex(List<String> list, String string, Boolean bool) {
        if (bool) {
            for (String l : list) {
                if (l.startsWith(string)) {
                    return list.indexOf(l);
                }
            }
        } else {
            for (String l : list) {
                if (string.startsWith(l)) {
                    return list.indexOf(l);
                }
            }
        }
        return -1;
    }

    //    查看list包含这个字符串第一个的位置
    public static int listContainsIndex(List<String> list, String string, Boolean bool) {
        if (bool) {
            for (String l : list) {
                if (l.contains(string)) {
                    return list.indexOf(l);
                }
            }
        } else {
            for (String l : list) {
                if (string.contains(l)) {
                    return list.indexOf(l);
                }
            }
        }
        return -1;
    }

    //    查看list这个字符串开头最后一个的位置
    public static int listStartsWithLastIndex(List<String> list, String string, Boolean bool) {
        int index = 0;
        int i = 0;
        if (bool) {
            for (String l : list) {
                i++;
                if (l.startsWith(string)) {
                    index = i;
                }
            }
        } else {
            for (String l : list) {
                i++;
                if (string.startsWith(l)) {
                    index = i;
                }
            }
        }
        return index;
    }

    //    查看list包含这个字符串最后一个的位置
    public static int listContainsLastIndex(List<String> list, String string, Boolean bool) {
        int index = 0;
        int i = 0;
        if (bool) {
            for (String l : list) {
                if (l.contains(string)) {
                    index = i;
                }
                i++;
            }
        } else {
            for (String l : list) {
                if (string.contains(l)) {
                    index = i;
                }
                i++;
            }
        }
        return index;
    }

    //    执行replace，但只替换第一次
    public static String replaceFirst(String string, String oldString, String newString) {
        string = string.replace("[", "%前括号%").replace("]", "%后括号%").replace("+", "%加号%").replace("-", "%减号%").replace("$", "%钱号%");
        oldString = oldString.replace("[", "%前括号%").replace("]", "%后括号%").replace("+", "%加号%").replace("-", "%减号%").replace("$", "%钱号%");
        newString = newString.replace("[", "%前括号%").replace("]", "%后括号%").replace("+", "%加号%").replace("-", "%减号%").replace("$", "%钱号%");
        return string.replaceFirst(oldString, newString).replace("%前括号%", "[").replace("%后括号%", "]").replace("%加号%", "+").replace("%减号%", "-").replace("%钱号%", "$");
    }
}

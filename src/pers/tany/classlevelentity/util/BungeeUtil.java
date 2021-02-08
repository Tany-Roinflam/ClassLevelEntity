package pers.tany.classlevelentity.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeUtil {
    //	向玩家发送信息，鼠标放上去后会显示其他信息
    public static void sendShowMessage(Player player, String message, String showmessage) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        send.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
            return;
        }
        player.spigot().sendMessage(send);
    }

    //	向玩家发送信息，鼠标放上去后会显示物品信息
    public static void sendShowItemMessage(Player player, String message, String showmessage) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        send.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
            return;
        }
        player.spigot().sendMessage(send);
    }

    //	其中一段字符串显示信息
    public static void sendPartShowMessage(Player player, String message, String part, String changemessage, String showmessage) {
        part = part.replace("[", "").replace("]", "");
        message = message.replace("[", "").replace("]", "");
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");
            boolean first = true;
            while (message.contains(part)) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
                all.addExtra(parts);
                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
                first = false;
                message = message.replaceFirst(part, "");
            }
            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }
            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    //	其中一段字符串显示物品信息
    public static void sendPartShowItemMessage(Player player, String message, String part, String changemessage, String showitem) {
        part = part.replace("[", "").replace("]", "");
        message = message.replace("[", "").replace("]", "");
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");
            boolean first = true;
            while (message.contains(part)) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem)).create()));
                all.addExtra(parts);
                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
                first = false;
                message = message.replaceFirst(part, "");
            }
            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }
            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    //		向玩家发送信息，鼠标放上去后会显示其他信息
    //		usecommand为true时，直接执行command，否则会在聊天框填充对应内容（指令需带/）
    public static void sendCommandMessage(Player player, String message, String showmessage, String command, Boolean usecommand) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        String value = null;
        if (usecommand) {
            value = "RUN_COMMAND";
        } else {
            value = "SUGGEST_COMMAND";
        }
        send.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value), command));
        send.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
            return;
        }
        player.spigot().sendMessage(send);
    }

    //		向玩家发送信息，鼠标放上去后会显示物品信息
    //		usecommand为true时，直接执行command，否则会在聊天框填充对应内容（指令需带/）
    public static void sendCommandItemMessage(Player player, String message, String showitem, String command, Boolean usecommand) {
        TextComponent send = new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
        String value = null;
        if (usecommand) {
            value = "RUN_COMMAND";
        } else {
            value = "SUGGEST_COMMAND";
        }
        send.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value), command));
        send.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem)).create()));
        if (player == null) {
            Bukkit.spigot().broadcast(send);
            return;
        }
        player.spigot().sendMessage(send);
    }

    //			其中一段字符串显示信息，并且点击后执行
    public static void sendPartShowCommandMessage(Player player, String message, String part, String changemessage, String showmessage, String command, Boolean usecommand) {
        part = part.replace("[", "").replace("]", "");
        message = message.replace("[", "").replace("]", "");
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");
            boolean first = true;
            while (message.contains(part)) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }
                parts.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);
                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
                first = false;
                message = message.replaceFirst(part, "");
            }
            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }
            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    //			其中一段字符串显示物品信息，并且点击后执行
    public static void sendPartShowCommandItemMessage(Player player, String message, String part, String changemessage, String showitem, String command, Boolean usecommand) {
        part = part.replace("[", "").replace("]", "");
        message = message.replace("[", "").replace("]", "");
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");
            boolean first = true;
            while (message.contains(part)) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showitem)).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }
                parts.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);
                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
                first = false;
                message = message.replaceFirst(part, "");
            }
            if (player == null) {
                Bukkit.spigot().broadcast(all);
                return;
            }
            player.spigot().sendMessage(all);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    //			转化版SendPartShowCommandMessage
    public static BaseComponent TransformPartShowCommandMessage(String message, String part, String changemessage, String showmessage, String command, Boolean usecommand) {
        part = part.replace("[", "").replace("]", "");
        message = message.replace("[", "").replace("]", "");
        if (message.contains(part)) {
            TextComponent all = new TextComponent("");
            boolean first = true;
            while (message.contains(part)) {
                if (first) {
                    try {
                        all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[0])));
                    } catch (ArrayIndexOutOfBoundsException ignored) {

                    }
                }
                TextComponent parts = new TextComponent(new TextComponent(ChatColor.translateAlternateColorCodes('&', part.replace(part, changemessage))));
                parts.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', showmessage)).create()));
                String value = null;
                if (usecommand) {
                    value = "RUN_COMMAND";
                } else {
                    value = "SUGGEST_COMMAND";
                }
                parts.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(value), command));
                all.addExtra(parts);
                try {
                    all.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', message.split(part)[1])));
                } catch (ArrayIndexOutOfBoundsException ignored) {

                }
                first = false;
                message = message.replaceFirst(part, "");
            }
            return all;
        } else {
            return new TextComponent(message);
        }
    }
}


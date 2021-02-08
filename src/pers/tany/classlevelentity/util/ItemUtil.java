package pers.tany.classlevelentity.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemUtil {
    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getType().equals(Material.AIR);
    }

    public static ItemStack getItemStack(String type) {
        try {
            return new ItemStack(Material.valueOf(type));
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ItemStack(Material.valueOf("LEGACY_" + type));
        }
    }

    public static ItemStack getItemStack(String type, int amount) {
        try {
            return new ItemStack(Material.valueOf(type), amount);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ItemStack(Material.valueOf("LEGACY_" + type), amount);
        }
    }

    public static ItemStack getItemStack(String type, int amount, short durability) {
        try {
            return new ItemStack(Material.valueOf(type), amount, durability);
        } catch (IllegalArgumentException illegalArgumentException) {
            return new ItemStack(Material.valueOf("LEGACY_" + type), amount, durability);
        }
    }

    public static boolean removeItemStack(Inventory inventory, ItemStack itemStack) {
        int index = 0;
        while (index < inventory.getContents().length - 1) {
            ItemStack i = inventory.getItem(index);
            if (!isEmpty(i) && i.equals(itemStack)) {
                inventory.setItem(index, null);
                return true;
            }
            index++;
        }
        return false;
    }

    public static int removeItemStack(Inventory inventory, ItemStack itemStack, int number) {
        int index = 0;
        while (index < inventory.getContents().length) {
            if (number <= 0) {
                return 0;
            }
            ItemStack i = inventory.getItem(index);
            if (!isEmpty(itemStack) && i.equals(itemStack)) {
                inventory.setItem(index, null);
                number--;
            }
            index++;
        }
        return number;
    }

    public static int deductItemStack(Inventory inventory, ItemStack itemStack, boolean clear) {
        int amount = itemStack.getAmount();
        for (ItemStack i : new ArrayList<ItemStack>(Arrays.asList(inventory.getContents()))) {
            if (itemStack.isSimilar(i)) {
                if (i.getAmount() >= amount) {
                    if (clear) {
                        if (i.getAmount() > amount) {
                            i.setAmount(i.getAmount() - amount);
                        } else {
                            removeItemStack(inventory, i);
                        }
                    }
                    amount = 0;
                    break;
                } else {
                    amount -= i.getAmount();
                    if (clear) {
                        removeItemStack(inventory, i);
                    }
                }
            }
        }
        return amount;
    }
}

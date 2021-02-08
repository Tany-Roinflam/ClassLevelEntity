package pers.tany.classlevelentity.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BasicLibrary {
    public static List<ItemStack> stainedglass = new ArrayList<ItemStack>();

    static {
//		如果需要使用请在plugin.yml里加上api-version: 1.13
//		已过时
        try {
            for (int i = 0; i <= 15; i++) {
                ItemStack glass = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"));
                glass.setDurability((short) i);
                stainedglass.add(glass);
            }
        } catch (Exception a) {
            stainedglass.add(new ItemStack(Material.valueOf("WHITE_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("ORANGE_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("MAGENTA_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("LIGHT_BLUE_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("YELLOW_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("LIME_STAINED_GLASS_PANE")));

            stainedglass.add(new ItemStack(Material.valueOf("PINK_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("LIGHT_GRAY_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("CYAN_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("PURPLE_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("BLUE_STAINED_GLASS_PANE")));

            stainedglass.add(new ItemStack(Material.valueOf("BROWN_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("GREEN_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("RED_STAINED_GLASS_PANE")));
            stainedglass.add(new ItemStack(Material.valueOf("BLACK_STAINED_GLASS_PANE")));
        }
    }

}

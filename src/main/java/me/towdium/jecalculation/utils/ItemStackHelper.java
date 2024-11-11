package me.towdium.jecalculation.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackHelper {

    public static final Item EMPTY_ITEM = null;
    public static final ItemStack EMPTY_ITEM_STACK = new ItemStack((Item) null);

    private static final String GREGTECH_LARGE_FLUID_CELL_ID = "gt.metaitem.01";

    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack == EMPTY_ITEM_STACK || stack.getItem() == EMPTY_ITEM;
    }

    /**
     * Checks if the given ItemStack is a GregTech large fluid container. This method checks for all variants of the
     * item.
     *
     * @param itemStack The ItemStack to check.
     * @return True if the ItemStack is a GregTech large fluid container, false otherwise.
     */
    public static boolean isGregTechLargeFluidContainer(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }

        return item.getUnlocalizedName()
            .equals(GREGTECH_LARGE_FLUID_CELL_ID);
    }
}

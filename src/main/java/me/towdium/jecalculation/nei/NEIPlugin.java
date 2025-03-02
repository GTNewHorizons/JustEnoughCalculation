package me.towdium.jecalculation.nei;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.gtnh.findit.FindItNetwork;
import com.gtnh.findit.service.itemfinder.FindItemRequest;

import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Version;

public class NEIPlugin {

    private static boolean catalystEnabled = false;

    private static final Version CATALYST_NEI_VERSION = new Version("2.1.0-GTNH");

    public static void init() {
        GuiContainerManager.addTooltipHandler(new JecaTooltipHandler());
        // nei version check
        String neiVersion = Loader.instance()
            .getIndexedModList()
            .get("NotEnoughItems")
            .getVersion();
        JustEnoughCalculation.logger.info("NEI version: " + neiVersion);
        Version version = new Version(neiVersion);
        if (version.isSuccess() && version.compareTo(CATALYST_NEI_VERSION) >= 0) {
            NEIPlugin.catalystEnabled = true;
            JustEnoughCalculation.logger.info("catalyst enabled");
        } else {
            JustEnoughCalculation.logger.info("catalyst disabled");
        }
    }

    private static ItemStack currentItemStack;

    public static boolean isCatalystEnabled() {
        return catalystEnabled;
    }

    public static ILabel getLabelUnderMouse() {
        if (NEIPlugin.currentItemStack == null) return ILabel.EMPTY;
        Object stack = Adapter.convertFluid(NEIPlugin.currentItemStack);
        return ILabel.Converter.from(stack);
    }

    public static void setLabelUnderMouse(ItemStack itemStack) {
        NEIPlugin.currentItemStack = itemStack;
    }

    private static class FluidStackToItemStack {

        private static Method getFluidDisplayStack = null;

        static {
            try {
                final Class<?> gtUtility = ReflectionHelper.getClass(
                    FluidStackToItemStack.class.getClassLoader(),
                    "gregtech.api.util.GTUtility",
                    "gregtech.api.util.GT_Utility");

                getFluidDisplayStack = gtUtility.getMethod("getFluidDisplayStack", FluidStack.class, boolean.class);
            } catch (Exception ignored) {
                /* Do nothing */
            }
        }

        private static ItemStack getItemStack(FluidStack fluidStack) {
            if (getFluidDisplayStack == null) {
                return null;
            }
            Object itemStack;
            try {
                itemStack = getFluidDisplayStack.invoke(null, fluidStack, true);
            } catch (Exception e) {
                return null;
            }
            if (itemStack instanceof ItemStack) {
                return (ItemStack) itemStack;
            }
            return null;
        }
    }

    public static boolean openRecipeGui(Object rep, boolean usage) {
        if (rep instanceof FluidStack) {
            ItemStack itemStack = FluidStackToItemStack.getItemStack((FluidStack) rep);
            if (itemStack != null) {
                rep = itemStack;
            }
        }
        if ((rep instanceof ItemStack || rep instanceof FluidStack)) {
            String id = rep instanceof ItemStack ? "item" : "liquid";
            if (!usage) {
                return GuiCraftingRecipe.openRecipeGui(id, rep);
            } else {
                return GuiUsageRecipe.openRecipeGui(id, rep);
            }
        } else if (rep != null) {
            JustEnoughCalculation.logger.warn("unknown label representation " + rep);
        }
        return false;
    }

    public static boolean findItem(Object rep) {
        if (rep instanceof FluidStack) {
            ItemStack itemStack = FluidStackToItemStack.getItemStack((FluidStack) rep);
            if (itemStack != null) {
                rep = itemStack;
            }
        }
        if (rep instanceof ItemStack) {
            FindItNetwork.CHANNEL.sendToServer(new FindItemRequest((ItemStack) rep));

        }
        return true;
    }

}

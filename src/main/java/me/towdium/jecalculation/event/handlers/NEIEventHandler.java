package me.towdium.jecalculation.event.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

import com.gtnh.findit.FindIt;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.guihook.IContainerInputHandler;
import cpw.mods.fml.common.Loader;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.nei.NEIPlugin;

public class NEIEventHandler implements IContainerInputHandler {

    private static final boolean isFindItLoaded = Loader.isModLoaded("findit");

    @Override
    public boolean keyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char c, int i) {}

    protected String getFindItKeyBind() {
        return FindIt.isExtraUtilitiesLoaded() ? "gui.xu_ping" : "gui.findit.find_item";
    }

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char keyChar, int keyCode) {
        if (guiContainer == Minecraft.getMinecraft().currentScreen && guiContainer instanceof JecaGui) {
            JecaGui gui = (JecaGui) guiContainer;
            ILabel label = gui.getLabelUnderMouse();
            if (label == null) return false;
            Object stack = label.getRepresentation();

            if (keyCode == NEIClientConfig.getKeyBinding("gui.usage")
                || (keyCode == NEIClientConfig.getKeyBinding("gui.recipe") && NEIClientUtils.shiftKey())) {
                return NEIPlugin.openRecipeGui(stack, true);
            }
            if (keyCode == NEIClientConfig.getKeyBinding("gui.recipe")) {
                return NEIPlugin.openRecipeGui(stack, false);
            }
            if (isFindItLoaded) {
                if (keyCode == NEIClientConfig.getKeyBinding(getFindItKeyBind())) {
                    return NEIPlugin.findItem(stack);
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int mouseX, int mouseY, int button) {
        return JecaGui.onMouse();
    }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {}

    @Override
    public void onMouseUp(GuiContainer guiContainer, int i, int i1, int i2) {
        JecaGui.onMouseReleased();
    }

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {
        return JecaGui.onMouse();
    }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {}

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int i, int i1, int i2, long l) {}
}

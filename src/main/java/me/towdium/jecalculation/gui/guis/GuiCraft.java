package me.towdium.jecalculation.gui.guis;

import static me.towdium.jecalculation.data.structure.RecordCraft.Mode.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.data.structure.CostList;
import me.towdium.jecalculation.data.structure.CostList.Calculator;
import me.towdium.jecalculation.data.structure.RecordCraft;
import me.towdium.jecalculation.data.structure.RecordGroupCraft;
import me.towdium.jecalculation.gui.JecaGui;
import me.towdium.jecalculation.gui.Resource;
import me.towdium.jecalculation.gui.widgets.WButton;
import me.towdium.jecalculation.gui.widgets.WButtonIcon;
import me.towdium.jecalculation.gui.widgets.WHelp;
import me.towdium.jecalculation.gui.widgets.WIcon;
import me.towdium.jecalculation.gui.widgets.WLabel;
import me.towdium.jecalculation.gui.widgets.WLabelGroup;
import me.towdium.jecalculation.gui.widgets.WLabelScroll;
import me.towdium.jecalculation.gui.widgets.WLine;
import me.towdium.jecalculation.gui.widgets.WOverlay;
import me.towdium.jecalculation.gui.widgets.WPanel;
import me.towdium.jecalculation.gui.widgets.WText;
import me.towdium.jecalculation.gui.widgets.WTextField;
import me.towdium.jecalculation.nei.NEIPlugin;
import me.towdium.jecalculation.polyfill.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.ItemStackHelper;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.wrappers.Pair;

/**
 * Author: towdium
 * Date: 8/14/17.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class GuiCraft extends Gui {

    Calculator calculator = null;
    RecordCraft record;
    RecordGroupCraft groupCraft;
    WLabel label = new WLabel(31, 7, 20, 20, true).setLsnrUpdate((i, v) -> {
        v.setAmount(getCurrentAmount());
        addLabel(v);
        refreshCrafts();
    });
    WLabelGroup recent = new WLabelGroup(7, 51, 8, 1, false).setLsnrLeftClick((i, v) -> {
        ILabel l = i.get(v)
            .getLabel();
        if (l != ILabel.EMPTY) {
            addLabel(l);
            refreshCrafts();
        }
    });
    WLabelScroll result = new WLabelScroll(7, 107, 8, 4, false).setLsnrLeftClick((i, v) -> {
        Object rep = i.get(v)
            .getLabel()
            .getRepresentation();
        NEIPlugin.openRecipeGui(rep, false);
    })
        .setLsnrRightClick((widget, value) -> {
            Object rep = widget.get(value)
                .getLabel()
                .getRepresentation();
            NEIPlugin.openRecipeGui(rep, true);
        })
        .setFmtAmount(i -> i.getAmountString(true))
        .setFmtTooltip((i, j) -> i.getToolTip(j, true));
    WButton steps = new WButtonIcon(64, 82, 20, 20, Resource.BTN_LIST, "craft.step").setListener(i -> setMode(STEPS));
    WButton catalyst = new WButtonIcon(45, 82, 20, 20, Resource.BTN_CAT, "common.catalyst")
        .setListener(i -> setMode(CATALYST));
    WButton output = new WButtonIcon(26, 82, 20, 20, Resource.BTN_OUT, "craft.output")
        .setListener(i -> setMode(OUTPUT));
    WButton input = new WButtonIcon(7, 82, 20, 20, Resource.BTN_IN, "common.input").setListener(i -> setMode(INPUT));
    WButton invE = new WButtonIcon(149, 82, 20, 20, Resource.BTN_INV_E, "craft.inventory_enabled");
    WButton invD = new WButtonIcon(149, 82, 20, 20, Resource.BTN_INV_D, "craft.inventory_disabled");
    WTextField amount = new WTextField(60, 7, 65).setListener(i -> {
        long amount = getCurrentAmount();
        groupCraft.setAmount(0, amount);
        record.amount = Long.toString(amount);
        refreshCalculator();
    });
    WLabelGroup craftingGroup = new WLabelGroup(7, 31, 8, 1, false).setLsnrLeftClick((i, v) -> {
        ILabel item = i.get(v)
            .getLabel();
        if (item == ILabel.EMPTY) return;
        addLabel(item);
        amount.setText(item.getAmountString(false));
        refreshCrafts();
    })
        .setLsnrRightClick((i, v) -> {
            groupCraft.removeLabel(v + 1);
            refreshCrafts();
        })
        .setFmtAmount(i -> i.getAmountString(true));

    public GuiCraft() {
        record = Controller.getRCraft();
        groupCraft = Controller.getRGroupCraft();
        amount.setText(record.amount);
        add(new WHelp("craft"));
        add(new WPanel(0, 0, 176, 186));
        add(
            new WButtonIcon(7, 7, 20, 20, Resource.BTN_LABEL, "craft.label")
                .setListener(i -> JecaGui.displayGui(new GuiLabel(l -> {
                    JecaGui.displayParent();
                    JecaGui.getCurrent().hand = l;
                }))));
        add(
            new WButtonIcon(130, 7, 20, 20, Resource.BTN_NEW, "craft.recipe")
                .setListener(i -> JecaGui.displayGui(true, true, new GuiRecipe())));
        add(
            new WButtonIcon(149, 7, 20, 20, Resource.BTN_SEARCH, "craft.search")
                .setListener(i -> JecaGui.displayGui(new GuiSearch())));
        add(new WText(53, 13, JecaGui.Font.RAW, "x"));
        add(new WLine(55));
        add(new WIcon(151, 51, 18, 18, Resource.ICN_RECENT, "craft.history"));
        add(
            craftingGroup,
            recent,
            label,
            input,
            output,
            catalyst,
            steps,
            result,
            amount,
            record.inventory ? invE : invD);
        invE.setListener(i -> {
            record.inventory = false;
            Controller.setRCraft(record);
            remove(invE);
            add(invD);
            refreshCalculator();
        });
        invD.setListener(i -> {
            record.inventory = true;
            Controller.setRCraft(record);
            remove(invD);
            add(invE);
            refreshCalculator();
        });
        refreshRecent(true);
        setMode(record.mode);
    }

    @Override
    public void onVisible(JecaGui gui) {
        refreshCalculator();
    }

    @Override
    public boolean acceptsTransfer() {
        return true;
    }

    @Override
    public boolean acceptsLabel() {
        return true;
    }

    void setMode(RecordCraft.Mode mode) {
        record.mode = mode;
        Controller.setRCraft(record);
        input.setDisabled(mode == INPUT);
        output.setDisabled(mode == OUTPUT);
        catalyst.setDisabled(mode == CATALYST);
        steps.setDisabled(mode == STEPS);
        refreshResult();
    }

    void refreshRecent(boolean notify) {
        label.setLabel(record.getLatest(), notify);
        recent.setLabel(record.getHistory(), 0);
    }

    void refreshCalculator() {
        try {
            String s = amount.getText();
            long i = s.isEmpty() ? 1 : Long.parseLong(amount.getText());
            amount.setColor(JecaGui.COLOR_TEXT_WHITE);
            List<ILabel> dest = groupCraft.getCraftList();
            CostList list = record.inventory ? new CostList(getInventory(), dest) : new CostList(dest);
            calculator = list.calculate();
        } catch (NumberFormatException | ArithmeticException e) {
            amount.setColor(JecaGui.COLOR_TEXT_RED);
            calculator = null;
        }
        refreshResult();
    }

    List<ILabel> getInventory() {
        InventoryPlayer inv = Utilities.getPlayer().inventory;
        ArrayList<ILabel> labels = new ArrayList<>();
        Consumer<ItemStack[]> add = i -> Arrays.stream(i)
            .filter(j -> !ItemStackHelper.isEmpty(j))
            .forEach(j -> labels.add(ILabel.Converter.from(j)));
        add.accept(inv.armorInventory);
        add.accept(inv.mainInventory);
        return labels;
    }

    void refreshResult() {
        if (calculator == null) {
            result.setLabels(new ArrayList<>());
        } else {
            switch (record.mode) {
                case INPUT:
                    result.setLabels(calculator.getInputs());
                    break;
                case OUTPUT:
                    result.setLabels(calculator.getOutputs(getInventory()));
                    break;
                case CATALYST:
                    result.setLabels(calculator.getCatalysts());
                    break;
                case STEPS:
                    result.setLabels(calculator.getSteps());
                    break;
            }
        }
    }

    private void refreshLabel(ILabel l, boolean replace, boolean suggest) {
        boolean dup = record.push(l, replace);
        Controller.setRCraft(record);
        refreshRecent(false);
        refreshCalculator();
        if (suggest && findRecipe(l).isEmpty()) {
            Pair<List<ILabel>, List<ILabel>> guess = ILabel.CONVERTER.guess(Collections.singletonList(l), null);
            LinkedHashSet<ILabel> match = new LinkedHashSet<>();
            List<ILabel> fuzzy = new ArrayList<>();
            Stream.of(guess.one, guess.two)
                .flatMap(Collection::stream)
                .forEach(i -> {
                    List<ILabel> list = findRecipe(i);
                    list.forEach(
                        j -> match.add(
                            j.setPercent(false)
                                .setAmount(1)));
                    if (!list.isEmpty()) fuzzy.add(i);
                });
            match.addAll(fuzzy);
            List<ILabel> list = new ArrayList<>(match);
            if (!match.isEmpty()) setOverlay(new Suggest(list.size() > 3 ? list.subList(0, 3) : list, !dup));
        }
    }

    private void refreshCrafts() {
        label.setLabel(groupCraft.getFirstOrEmpty(), false);
        craftingGroup.setLabel(groupCraft.getCraftList(), 1);
        refreshCalculator();
    }

    private long getCurrentAmount() {
        String s = amount.getText();
        try {
            return s.isEmpty() ? 1 : Long.parseLong(amount.getText());
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private void addLabel(ILabel l) {
        if (l == ILabel.EMPTY) return;
        record.push(l, false);
        Controller.setRCraft(record);
        groupCraft.addLabel(l);
        refreshCrafts();
        refreshRecent(false);
    }

    private static List<ILabel> findRecipe(ILabel l) {
        return Controller.recipeIterator()
            .stream()
            .map(i -> i.matches(l))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(ILabel::copy)
            .collect(Collectors.toList());
    }

    class Suggest extends WOverlay {

        boolean replace;

        public Suggest(List<ILabel> labels, boolean replace) {
            this.replace = replace;
            int width = labels.size() * 20;
            add(new WPanel(-width, 2, 56 + width, 30));
            add(
                new WLabel(31, 7, 20, 20, false).setLabel(label.getLabel())
                    .setLsnrUpdate((i, v) -> refresh(v)));
            add(new WIcon(5 - width, 7, 18, 20, Resource.ICN_HELP, "craft.suggest"));
            add(new WLine(26, 7, 20, false));
            for (int i = 0; i < labels.size(); i++) {
                add(
                    new WLabel(3 - i * 20, 7, 20, 20, false).setLabel(labels.get(i))
                        .setLsnrUpdate((j, v) -> refresh(v)));
            }
        }

        public void refresh(ILabel l) {
            GuiCraft.this.setOverlay(null);
            refreshLabel(l, replace, false);
        }
    }
}

package me.towdium.jecalculation.data.structure;

import gregapi.data.IL;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.ILabel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedList;
import java.util.List;

public class RecordGroupCraft implements IRecord {
    public static final String KEY_ITEMS = "items";

    LinkedList<ILabel> craftList = new LinkedList<>();

    public RecordGroupCraft(NBTTagCompound nbt) {

    }

    public void addLabel(ILabel l) {
        if (l == ILabel.EMPTY) return;
        craftList.remove(l);
        craftList.addFirst(l);
    }

    public void setAmount(int index, long amount) {
        if (index < 0 || index >= craftList.size()) return;
        ILabel l = craftList.get(index);
        if (l == ILabel.EMPTY) return;
        l.setAmount(amount);
    }

    public void removeLabel(int index) {
        if (index < 0 || index >= craftList.size()) return;
        craftList.remove(index);
    }

    public List<ILabel> getCraftList() {
        return craftList;
    }

    public ILabel getFirstOrEmpty() {
        return craftList.isEmpty() ? ILabel.EMPTY : craftList.getFirst();
    }

    @Override
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList items = new NBTTagList();
        craftList.forEach(l -> items.appendTag(l.SERIALIZER.serialize(l)));
        nbt.setTag(KEY_ITEMS, items);
        return nbt;
    }
}

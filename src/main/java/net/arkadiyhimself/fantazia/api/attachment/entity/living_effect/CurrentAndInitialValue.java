package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class CurrentAndInitialValue implements INBTSerializable<CompoundTag> {

    private int initialValue = 1;
    private int value = 0;

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("initialValue", initialValue);
        tag.putInt("value", value);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.initialValue = compoundTag.getInt("initialValue");
        this.value = compoundTag.getInt("value");
    }

    public void setInitialValue(int initialDur) {
        this.initialValue = Math.max(1, initialDur);
    }

    public void setValue(int dur) {
        this.value = dur;
    }

    public int initialValue() {
        return initialValue;
    }

    public int value() {
        return value;
    }

    public float percent() {
        return (float) value / initialValue;
    }
}

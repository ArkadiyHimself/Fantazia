package net.arkadiyhimself.fantazia.util.simpleobjects;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.jetbrains.annotations.NotNull;

public class PercentageAttribute extends RangedAttribute {
    public PercentageAttribute(String pDescriptionId, double pDefaultValue) {
        super(pDescriptionId, pDefaultValue, 0, 100);
    }
    @Override
    public @NotNull PercentageAttribute setSyncable(boolean pWatch) {
        super.setSyncable(pWatch);
        return this;
    }
}

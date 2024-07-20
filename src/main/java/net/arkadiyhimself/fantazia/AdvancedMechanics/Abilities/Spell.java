package net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities;

import net.arkadiyhimself.fantazia.util.Interfaces.ITooltipBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;

public abstract class Spell implements ITooltipBuilder {
    private final float MANACOST;
    private final int recharge;
    private final ResourceLocation id;
    private final SoundEvent castSound;
    public Spell(float MANACOST, int recharge, ResourceLocation id, @Nullable SoundEvent castSound) {
        this.recharge = recharge;
        this.MANACOST = MANACOST;
        this.id = id;
        this.castSound = castSound;
    }
    public float getManacost() {
        return MANACOST;
    }
    public int getRecharge() {
        return recharge;
    }
    public ResourceLocation getId() {
        return id;
    }
    public Component getName() {
        return Component.translatable("ability." + this.getId().getNamespace() + "." + this.getId().getPath() + ".name");
    }
    public SoundEvent getCastSound() {
        return castSound;
    }
    @Override
    public List<Component> buildTooltip(@Nullable ItemStack itemStack) {
        return Lists.newArrayList();
    }
}

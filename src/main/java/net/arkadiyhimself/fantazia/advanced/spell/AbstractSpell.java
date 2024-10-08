package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.type.item.ITooltipBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class AbstractSpell implements ITooltipBuilder {
    private final float manacost;
    private final int recharge;
    private final Supplier<SoundEvent> castSound;
    private Cleanse strength = null;
    protected boolean hasCleanse = false;
    protected AbstractSpell(float manacost, int recharge, @Nullable Supplier<SoundEvent> castSound) {
        this.recharge = recharge;
        this.manacost = manacost;
        this.castSound = castSound == null ? () -> null : castSound;
    }
    public AbstractSpell cleanse(Cleanse cleanse) {
        strength = cleanse;
        hasCleanse = true;
        return this;
    }
    public final float getManacost() {
        return manacost;
    }
    public final int getRecharge() {
        return recharge;
    }
    @Nullable
    public final ResourceLocation getID() {
        return FantazicRegistry.SPELLS.getKey(this);
    }
    public final Component getName() {
        if (getID() == null) return null;
        return Component.translatable("spell." + getID().getNamespace() + "." + getID().getPath() + ".name");
    }
    @Nullable
    public SoundEvent getCastSound() {
        return castSound.get();
    }
    public Cleanse getStrength() {
        return strength;
    }
    public boolean hasCleanse() {
        return hasCleanse;
    }
    @Override
    public List<Component> itemTooltip(@Nullable ItemStack itemStack) {
        return Lists.newArrayList();
    }
    public final boolean is(TagKey<AbstractSpell> tagKey) {
        if (getID() == null) return false;
        Optional<Holder.Reference<AbstractSpell>> holder = FantazicRegistry.SPELLS.getHolder(getID());
        return holder.map(abstractSpellReference -> abstractSpellReference.is(tagKey)).orElse(false);
    }
}
